package com.zazsona.minecraftchatlink;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.configuration.GuildSettings;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleConfig;
import com.zazsona.minecraftchatlink.data.ChatLinkData;
import com.zazsona.minecraftchatlink.data.GuildChatLink;
import com.zazsona.minecraftcommon.FileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class MCLConfig extends ModuleConfig
{
    private static final Logger logger = LoggerFactory.getLogger(MCLConfig.class);
    private boolean isSetup;
    private MessageManager messageManager;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        try
        {
            this.isSetup = isSetup;
            this.messageManager = new MessageManager();
            String ip = FileManager.getIpForGuild(msgEvent.getGuild().getId());
            runEnableMenu(msgEvent);
            GuildChatLink guildChatLink = ChatLinkData.getInstance().getGuild(msgEvent.getGuild().getId());
            if (guildChatLink != null || guildChatLink.isEnabled())
            {
                if (isSetup && ip == null || !isSetup)
                {
                    runIPMenu(msgEvent, isSetup, ip);
                }
                runChannelMenu(msgEvent);
                if (isSetup)
                {
                    MinecraftChatLinkCommand.sendChatLinkUUID(msgEvent.getGuild(), msgEvent.getMember().getUser(), guildChatLink.getChatLinkId());
                }
                EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
                embed.setDescription("Restarting chat link...");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                ChatLinkClient.getInstance(msgEvent.getGuild().getId()).stopClient();
                new Thread(() -> ChatLinkClient.getInstance(msgEvent.getGuild().getId()).startClient()).start();
            }
        }
        catch (IOException e)
        {
            EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
            embed.setDescription("An error occurred when modifying the config - "+e.getMessage());
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            logger.error("Couldn't write chat link config - "+e.getMessage());
        }
    }

    private void runEnableMenu(GuildMessageReceivedEvent msgEvent) throws IOException
    {
        GuildChatLink guildChatLink = ChatLinkData.getInstance().getGuild(msgEvent.getGuild().getId());
        boolean enabled = (guildChatLink != null && guildChatLink.isEnabled());
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        embed.setDescription("Current State: "+((enabled) ? "Enabled" : "Disabled")+"\nWould you like to enable Minecraft Chat Link? (Y/n)");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        while (true)
        {
            Message msg = messageManager.getNextMessage(msgEvent.getChannel(), msgEvent.getMember());
            String content = msg.getContentDisplay();
            if (content.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit") || content.equalsIgnoreCase("quit"))
            {
                if (!isSetup)
                {
                    embed.setDescription("Menu quit.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
                return;
            }
            else if (content.equalsIgnoreCase("n") || content.equalsIgnoreCase("no"))
            {
                if (guildChatLink != null)
                    guildChatLink.setEnabled(false);
                embed.setDescription("Minecraft Chat Link disabled.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                return;
            }
            else if (content.equalsIgnoreCase("y") || content.equalsIgnoreCase("yes"))
            {
                if (guildChatLink == null)
                {
                    ChatLinkData.getInstance().addGuild(msgEvent.getGuild().getId());
                    guildChatLink = ChatLinkData.getInstance().getGuild(msgEvent.getGuild().getId());
                }
                guildChatLink.setEnabled(true);
                embed.setDescription("Minecraft Chat Link enabled.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                return;
            }
        }
    }

    private void runChannelMenu(GuildMessageReceivedEvent msgEvent) throws IOException
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        TextChannel savedChannel = getSavedChannel(msgEvent.getGuild());
        embed.setDescription("Current Channel: "+((savedChannel == null) ? "None" : savedChannel.getName())+"\nPlease mention the channel to link Minecraft chat messages with.");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        while (true)
        {
            Message msg = messageManager.getNextMessage(msgEvent.getChannel(), msgEvent.getMember());
            String content = msg.getContentDisplay();
            if (content.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit") || content.equalsIgnoreCase("quit"))
            {
                if (!isSetup)
                {
                    embed.setDescription("Menu quit.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
                return;
            }
            else
            {
                if (msg.getMentionedChannels().size() > 0)
                {
                    TextChannel channel = msg.getMentionedChannels().get(0);
                    setChannel(msgEvent, channel.getId());
                    embed.setDescription("Channel set.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                    return;
                }
            }
        }
    }

    private void runIPMenu(GuildMessageReceivedEvent msgEvent, boolean isSetup, String ip) throws IOException
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        embed.setDescription("Current IP: "+((ip == null) ? "None" : ip)+"\n\nPlease set a default IP for Minecraft server commands, use reset to remove, or quit to cancel.");
        msgEvent.getChannel().sendMessage(embed.build()).queue();

        while (true)
        {
            Message msg = messageManager.getNextMessage(msgEvent.getChannel(), msgEvent.getMember());
            String content = msg.getContentDisplay();
            if (content.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit") || content.equalsIgnoreCase("quit"))
            {
                if (!isSetup)
                {
                    embed.setDescription("Menu quit.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
                break;
            }
            else if (content.equalsIgnoreCase("reset"))
            {
                FileManager.resetIpForGuild(msgEvent.getGuild().getId());
                FileManager.save();
                embed.setDescription("IP reset.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                break;
            }
            boolean success = setNewIpAddress(msgEvent, content);
            if (success)
            {
                break;
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> parameters, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        run(msgEvent, guildSettings, textChannel, false);
    }

    /**
     * Sets the Minecraft server IP to negotiate with.
     * @param msgEvent the context
     * @param ip the ip
     * @return boolean on success.
     */
    private boolean setNewIpAddress(GuildMessageReceivedEvent msgEvent, String ip)
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        if (ip.contains("."))
        {
            FileManager.setIpForGuild(msgEvent.getGuild().getId(), ip);
            FileManager.save();
            embed.setDescription("Default Minecraft IP set to: "+ip);
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            return true;
        }
        else
        {
            embed.setDescription("Invalid IP: "+ip+"\nPlease try again.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            return false;
        }
    }

    /**
     * Sets the channel to send and receive Minecraft messages from.
     * @param msgEvent the context
     * @param channelId the channel to send/receive from
     */
    private void setChannel(GuildMessageReceivedEvent msgEvent, String channelId) throws IOException
    {
        GuildChatLink guildChatLink = ChatLinkData.getInstance().getGuild(msgEvent.getGuild().getId());
        guildChatLink.setTextChannelId(channelId);
        guildChatLink.save();
    }

    /**
     * Gets the channel to send and receive Minecraft messages from.
     * @param guild the guild to query
     * @return the channel
     */
    private TextChannel getSavedChannel(Guild guild) throws IOException
    {
        GuildChatLink guildChatLink = ChatLinkData.getInstance().getGuild(guild.getId());
        if (guildChatLink.getTextChannelId() == null)
            return null;
        else
            return guild.getTextChannelById(guildChatLink.getTextChannelId());
    }
}
