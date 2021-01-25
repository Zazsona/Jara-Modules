package com.zazsona.minecraftchatlink;

import com.zazsona.chatlink.DiscordMessagePacket;
import com.zazsona.chatlink.MessagePacket;
import com.zazsona.chatlink.MinecraftMessagePacket;
import com.zazsona.minecraftchatlink.data.ChatLinkData;
import com.zazsona.minecraftchatlink.data.GuildChatLink;
import com.zazsona.minecraftcommon.FileManager;
import commands.CmdUtil;
import jara.Core;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class ChatLinkClient
{
    private final static int PORT = 25599;
    private static Logger logger = LoggerFactory.getLogger(ChatLinkClient.class);
    private static HashMap<String, ChatLinkClient> instances = new HashMap<>();
    private static MessageListener MESSAGE_LISTENER;

    private String guildId;
    private GuildChatLink guildChatLink;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean clientManuallyStopped;
    private Thread pingThread;

    public static ChatLinkClient getInstance(String guildId)
    {
        if (!instances.containsKey(guildId))
            instances.put(guildId, new ChatLinkClient(guildId));
        return instances.get(guildId);
    }

    private ChatLinkClient(String guildId)
    {
        this.guildId = guildId;
        if (MESSAGE_LISTENER == null)
        {
            MESSAGE_LISTENER = new MessageListener();
            Core.getShardManagerNotNull().addEventListener(MESSAGE_LISTENER);
        }
    }

    public void startClient()
    {
        try
        {
            clientManuallyStopped = false;
            guildChatLink = ChatLinkData.getInstance().getGuild(guildId);
            while (!clientManuallyStopped && guildChatLink != null)
            {
                try
                {
                    String ip = FileManager.getIpWithoutPort(guildChatLink.getGuildId());
                    if (ip == null)
                        throw new ConnectException();
                    socket = new Socket(ip, PORT);
                    input = new ObjectInputStream(socket.getInputStream());
                    output = new ObjectOutputStream(socket.getOutputStream());
                    output.flush();
                    logger.info("Found server. Checking ids...");
                    boolean isRegisteredJaraInstance = isAssociatedJaraInstance();
                    if (isRegisteredJaraInstance)
                    {
                        logger.info("Id match. Launching listener.");
                        pingThread = new Thread(() -> runPingLoop());
                        pingThread.start();
                        linkMinecraftMessages();
                    }
                    else
                        logger.info("Minecraft server attempted to connect, but gave incorrect id.");
                    stopClient();
                    clientManuallyStopped = false;
                }
                catch (SocketTimeoutException | ConnectException e)
                {
                    try{Thread.sleep(3000);}catch(InterruptedException e1){};
                }
            }
            throw new IOException("Client stopped or disabled.");
        }
        catch (IOException e)
        {
            logger.info("Chat link client stopped.");
            e.printStackTrace();
        }
    }

    public void stopClient()
    {
        try
        {
            if (socket != null && !socket.isClosed())
            {
                output.flush();
                socket.close();
            }

            socket = null;
            input = null;
            output = null;
            pingThread.interrupt();
            clientManuallyStopped = true;
        }
        catch (IOException e)
        {
            logger.error("Unable to gracefully close chat link client.");
        }
    }

    private boolean isAssociatedJaraInstance()
    {
        try
        {
            boolean isMatchingId = false;
            output.writeObject(new MessagePacket(guildChatLink.getChatLinkId()));
            output.flush();
            Object response = input.readObject();
            if (response instanceof MessagePacket)
            {
                MessagePacket responseMessagePacket = (MessagePacket) response;
                isMatchingId = guildChatLink.getChatLinkId().equals(responseMessagePacket.getChatLinkUUID());
            }
            return isMatchingId;
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void linkMinecraftMessages()
    {
        TextChannel discordChannel = Core.getShardManagerNotNull().getTextChannelById(guildChatLink.getTextChannelId());
        try
        {
            discordChannel.sendMessage(getEmbed(discordChannel).setDescription("Connected to Minecraft!").build()).queue();
            while (!socket.isClosed())
            {
                Object receivedObject = input.readObject();
                if (receivedObject instanceof MinecraftMessagePacket)
                {
                    MinecraftMessagePacket minecraftMessagePacket = (MinecraftMessagePacket) receivedObject;
                    if (minecraftMessagePacket.getChatLinkUUID() != null && guildChatLink.getChatLinkId().equals(minecraftMessagePacket.getChatLinkUUID()))
                    {
                        discordChannel.sendMessage(getEmbed(discordChannel, minecraftMessagePacket).build()).queue();
                    }
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.info("Could not communicate with Minecraft. Closing the connection. - "+e.getMessage());
        }
        finally
        {
            discordChannel.sendMessage(getEmbed(discordChannel).setDescription("Lost connection to Minecraft.").build()).queue();
        }
    }

    public boolean sendMessageToMinecraft(Message message)
    {
        if (socket != null && !socket.isClosed() && output != null)
        {
            try
            {
                output.writeObject(new DiscordMessagePacket(guildChatLink.getChatLinkId(), message.getMember().getEffectiveName(), message.getContentDisplay()));
                output.flush();
                return true;
            }
            catch (IOException e)
            {
                logger.info("Unable to send message: "+message.getContentDisplay()+"\nReason: "+e.getMessage());
            }
        }
        return false;
    }

    private void runPingLoop()
    {
        try
        {
            while (true)
            {
                if (socket != null && !socket.isClosed())
                {
                    output.writeObject(new MessagePacket(guildChatLink.getChatLinkId()));
                    output.flush();
                    Thread.sleep(5000);
                }
            }
        }
        catch (IOException e)
        {
            stopClient();
            clientManuallyStopped = false;
        }
        catch (InterruptedException e)
        {
            //Ping loop stopped.
        }
    }

    private class MessageListener extends ListenerAdapter
    {
        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
        {
            super.onGuildMessageReceived(event);
            ChatLinkClient receivingClient = instances.get(event.getGuild().getId());
            if (!event.getMember().getUser().isBot() && receivingClient != null && receivingClient.guildChatLink.getTextChannelId().equals(event.getChannel().getId()))
            {
                sendMessageToMinecraft(event.getMessage());
            }
        }
    }

    /**
     * Gets the embed styling for messages from Minecraft
     * @param channel the context
     * @return a pre-styled embed
     */
    private EmbedBuilder getEmbed(TextChannel channel)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.setFooter("Minecraft");
        return embed;
    }

    /**
     * Gets the embed styling for messages from Minecraft
     * @param channel the context
     * @param minecraftMessagePacket the message from Minecraft
     * @return a pre-styled embed
     */
    private EmbedBuilder getEmbed(TextChannel channel, MinecraftMessagePacket minecraftMessagePacket)
    {
        EmbedBuilder embed = getEmbed(channel);
        embed.setAuthor(minecraftMessagePacket.getMinecraftUsername(), null, "https://crafatar.com/avatars/"+minecraftMessagePacket.getUuid()+"?overlay=true.png");
        embed.setDescription(minecraftMessagePacket.getMessageContent());
        return embed;
    }
}
