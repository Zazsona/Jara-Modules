package com.Zazsona.JaraChatLink;

import org.bukkit.plugin.Plugin;

public class Settings
{
    private static Plugin plugin = Core.getPlugin(Core.class);

    public static void save()
    {
        plugin.saveConfig();
    }

    public static void setEnabled(boolean newEnabled)
    {
        plugin.getConfig().set("Enabled", newEnabled);
        save();
    }

    public static boolean isEnabled()
    {
        return plugin.getConfig().getBoolean("Enabled");
    }

    public static void setBotID(String newBotID)
    {
        plugin.getConfig().set("BotDiscordID", newBotID);
        save();
    }

    public static String getBotID()
    {
        return plugin.getConfig().getString("BotDiscordID");
    }
}
