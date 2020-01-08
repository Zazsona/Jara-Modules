package com.Zazsona.JaraChatLink;

import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new ChatEventListener(), this);
        this.getCommand("JaraChatLink").setExecutor(new JaraChatLinkCommand());
    }

    @Override
    public void onDisable()
    {
    }
}
