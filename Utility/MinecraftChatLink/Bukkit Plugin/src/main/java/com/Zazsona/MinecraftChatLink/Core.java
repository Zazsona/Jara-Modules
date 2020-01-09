package com.Zazsona.MinecraftChatLink;

import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin
{
    private static MinecraftMessageManager minecraftMessageManager;

    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new ChatEventListener(), this);
        this.getCommand("JaraChatLink").setExecutor(new JaraChatLinkCommand());
        new Thread(() ->
                   {
                       minecraftMessageManager = MinecraftMessageManager.getInstance();
                       minecraftMessageManager.startConnection();
                   }).start();

    }

    @Override
    public void onDisable()
    {
    }
}
