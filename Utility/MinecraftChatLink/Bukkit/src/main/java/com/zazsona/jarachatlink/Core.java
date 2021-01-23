package com.zazsona.jarachatlink;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public class Core extends JavaPlugin
{
    private static Thread chatLinkThread;
    private static ChatLinkServer chatLinkServer;

    @Override
    public void onEnable()
    {
        try
        {
            getConfig().options().copyDefaults(true);
            saveConfig();
            this.getServer().getPluginManager().registerEvents(new ChatEventListener(), this);
            this.getCommand("JaraChatLink").setExecutor(new JaraChatLinkCommand());
            if (Settings.isEnabled())
            {
                chatLinkServer = new ChatLinkServer();
                chatLinkThread = new Thread(() -> chatLinkServer.startServer());
                chatLinkThread.start();
            }
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to create chat link server - "+e.getMessage());
        }
    }

    @Override
    public void onDisable()
    {
        chatLinkServer.stopServer();
        chatLinkThread.interrupt();
    }

    /**
     * Gets chatLinkServer
     * @return chatLinkServer
     */
    public static ChatLinkServer getChatLinkServer()
    {
        return chatLinkServer;
    }
}
