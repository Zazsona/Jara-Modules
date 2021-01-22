package com.zazsona.MinecraftChatLink;

import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin
{
    private static MinecraftMessageManager minecraftMessageManager;
    private static Thread connectionThread;

    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new ChatEventListener(), this);
        this.getCommand("JaraChatLink").setExecutor(new JaraChatLinkCommand());
        connectionThread = new Thread(() ->
                   {
                       minecraftMessageManager = MinecraftMessageManager.getInstance();
                       minecraftMessageManager.startConnection();
                   });
        connectionThread.start();

    }

    @Override
    public void onDisable()
    {
        connectionThread.interrupt();
        MinecraftMessageManager.getInstance().stopConnection();
    }
}
