package com.Zazsona.MinecraftChatLink;

import org.bukkit.plugin.Plugin;

public class Settings
{
    private static Plugin plugin = Core.getPlugin(Core.class);

    public static void save()
    {
        plugin.saveConfig();
    }

    /**
     * Sets the enabled state of the plugin
     * @param newEnabled the state to set
     */
    public static void setEnabled(boolean newEnabled)
    {
        plugin.getConfig().set("Enabled", newEnabled);
        save();
    }

    /**
     * Returns if the plugin is enabled
     * @return boolean on enabled
     */
    public static boolean isEnabled()
    {
        return plugin.getConfig().getBoolean("Enabled");
    }

    /**
     * Sets the Bot's Discord User ID. Only the bot with a matching ID can communicate with the server.
     * @param newBotID the bot ID to set
     */
    public static void setBotID(String newBotID)
    {
        plugin.getConfig().set("BotDiscordID", newBotID);
        save();
    }

    /**
     * Returns the Discord User ID that must match to communicate with a Discord Bot.
     * @return the bot's user ID
     */
    public static String getBotID()
    {
        return plugin.getConfig().getString("BotDiscordID");
    }
}
