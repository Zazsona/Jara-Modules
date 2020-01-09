package com.Zazsona.MinecraftChatLink;

import com.Zazsona.ChatLinkCommon.DiscordMessagePacket;
import com.Zazsona.ChatLinkCommon.MinecraftMessagePacket;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MinecraftMessageManager
{
    private final static int PORT = 25599;
    private static MinecraftMessageManager instance;


    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Thread listeningThread;

    /**
     * Gets the instance for the server.
     * @return the instance
     */
    public static MinecraftMessageManager getInstance()
    {
        if (instance == null)
        {
            instance = new MinecraftMessageManager();
        }
        return instance;
    }

    private MinecraftMessageManager()
    {
    }

    /**
     * Sends the provided Discord message to the Minecraft server
     * @param username the Minecraft player's username
     * @param messageContent the Minecraft player's message
     */
    public void sendMessageToDiscord(String username, String messageContent)
    {
        if (Settings.isEnabled() && output != null)
        {
            try
            {
                output.writeObject(new MinecraftMessagePacket(username, messageContent));
                output.flush();
            }
            catch (IOException e)
            {
                System.err.println("Connection Lost. Restarting...\n"+e.toString());
                startConnection();
            }
        }
    }

    /**
     * Sends the provided Discord message to the server's broadcast channel
     * @param discordMessagePacket the message to send
     */
    private void sendMessageToMinecraft(DiscordMessagePacket discordMessagePacket)
    {
        Server server = Core.getPlugin(Core.class).getServer();
        server.broadcastMessage(ChatColor.BLUE+(discordMessagePacket.getMessageDisplay().replace("§", ""))); //TODO: Handle formatting properly
    }

    /**
     * Waits for incoming messages from the Discord server, and sends them to Minecraft<br>
     * In the case that the connection dies, this will attempt to reestablish it automatically.
     * Similarly, if the connected bot is not the one specified in the config, the connection will be closed.
     */
    private void listenForDiscordMessages()
    {
        listeningThread = Thread.currentThread();
        while (Settings.isEnabled())
        {
            try
            {
                Object receivedObject = input.readObject();
                if (receivedObject instanceof DiscordMessagePacket)
                {
                    DiscordMessagePacket discordMessagePacket = (DiscordMessagePacket) receivedObject;
                    if (discordMessagePacket.getBotID().equals(Settings.getBotID()))
                    {
                        sendMessageToMinecraft(discordMessagePacket);
                    }
                    else
                    {
                        stopConnection();
                    }
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.err.println("Connection Lost. Restarting...\n"+e.toString());
                startConnection();
            }
        }
        startConnection();
        Thread.currentThread().interrupt();
    }

    /**
     * Starts a new connection.<br>
     * This will reserve the thread, and will only terminate the thread when the ChatLink is disabled in user settings, or stopConnection() is called. As such, any statements after this will never run.
     */
    public void startConnection()
    {
        try
        {
            stopConnection();
            if (Settings.isEnabled() && Settings.getBotID() != null)
            {
                serverSocket = new ServerSocket(PORT);
                socket = serverSocket.accept();
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                listenForDiscordMessages();
            }
        }
        catch (IOException e)
        {
            System.err.println("Connection could not be established.\n"+e.toString());
        }
    }

    /**
     * Kills the connection and terminates the connection thread.
     */
    public void stopConnection()
    {
        try
        {
            if (listeningThread != null && listeningThread != Thread.currentThread())
            {
                listeningThread.interrupt();
                listeningThread = null;
            }

            if (serverSocket != null)
                serverSocket.close();
            if (socket != null)
                socket.close();
            if (input != null)
                input.close();
            if (output != null)
                output.close();

            serverSocket = null;
            socket = null;
            input = null;
            output = null;
        }
        catch (IOException e)
        {
            System.err.println(e.toString());
        }
    }

}
