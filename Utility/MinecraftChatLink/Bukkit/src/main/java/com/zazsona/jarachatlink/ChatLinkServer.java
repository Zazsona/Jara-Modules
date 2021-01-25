package com.zazsona.jarachatlink;

import com.zazsona.chatlink.DiscordMessagePacket;
import com.zazsona.chatlink.MessagePacket;
import com.zazsona.chatlink.MinecraftMessagePacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class ChatLinkServer
{
    private final static int PORT = 25599;

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ChatLinkServer() throws IOException
    {
        serverSocket = new ServerSocket(PORT);
    }

    public void startServer()
    {
        try
        {
            while (Settings.isEnabled() && Settings.getLinkId() != null && !serverSocket.isClosed())
            {
                Bukkit.getLogger().log(Level.INFO, "Waiting for Jara...");
                socket = serverSocket.accept();
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                input = new ObjectInputStream(socket.getInputStream());
                Bukkit.getLogger().log(Level.INFO, "Jara connected. Checking Ids...");
                boolean isRegisteredJaraInstance = isAssociatedJaraInstance();
                if (isRegisteredJaraInstance)
                {
                    Bukkit.getLogger().log(Level.INFO, "Id match. Listening for messages...");
                    linkDiscordMessages();
                }
                else
                    Bukkit.getLogger().log(Level.INFO, "Jara client attempted to connect, but gave incorrect id.");
                stopClientConnection();

            }
            throw new IOException("Server stopped or disabled.");
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.INFO, "Chat link server stopped.");
        }
    }

    public void stopServer()
    {
        try
        {
            if (serverSocket != null)
                serverSocket.close();
            serverSocket = null;
            stopClientConnection();
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to gracefully close chat link server.");
        }
    }

    public void stopClientConnection()
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
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to gracefully close chat link client connection.");
        }
    }

    private boolean isAssociatedJaraInstance()
    {
        try
        {
            boolean isMatchingId = false;
            Object response = input.readObject();
            if (response instanceof MessagePacket)
            {
                MessagePacket responseMessagePacket = (MessagePacket) response;
                isMatchingId = Settings.getLinkId().equals(responseMessagePacket.getChatLinkUUID());
            }
            output.writeObject(new MessagePacket(Settings.getLinkId()));
            output.flush();
            return isMatchingId;
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void linkDiscordMessages()
    {
        Server minecraftServer = Core.getPlugin(Core.class).getServer();
        try
        {
            minecraftServer.broadcastMessage(ChatColor.BLUE+"[JaraChatLink] Connected to Discord!");
            while (!socket.isClosed())
            {
                Object receivedObject = input.readObject();
                if (receivedObject instanceof DiscordMessagePacket)
                {
                    DiscordMessagePacket discordMessagePacket = (DiscordMessagePacket) receivedObject;
                    if (discordMessagePacket.getChatLinkUUID() != null && discordMessagePacket.getChatLinkUUID().equals(Settings.getLinkId()))
                    {
                        minecraftServer.broadcastMessage(ChatColor.BLUE+(discordMessagePacket.getMessageDisplay().replace("§", ""))); //TODO: Handle formatting properly
                    }
                }
                else if (receivedObject instanceof MessagePacket)
                {
                    //Do nothing, simple ping.
                }
            }
        }
        catch (Exception e)
        {
            Bukkit.getLogger().log(Level.WARNING, "Could not communicate with Discord. Closing the connection. - "+e.getMessage());
        }
        finally
        {
            minecraftServer.broadcastMessage(ChatColor.BLUE+"[JaraChatLink] Lost connection to Discord.");
        }
    }

    public boolean sendMessageToDiscord(String uuid, String username, String messageContent)
    {
        if (socket != null && !socket.isClosed() && output != null)
        {
            try
            {
                output.writeObject(new MinecraftMessagePacket(Settings.getLinkId(), uuid, username, messageContent));
                output.flush();
                return true;
            }
            catch (IOException e)
            {
                Bukkit.getLogger().log(Level.WARNING, "Unable to send message: "+messageContent+"\nReason: "+e.getMessage());
            }
        }
        return false;
    }
}
