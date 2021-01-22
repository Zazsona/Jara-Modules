package com.zazsona.MinecraftChatLink;

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
     * Sets the ChatLink ID. Only the bot and Discord server with a matching ID can communicate with the Minecraft server.
     * @param newBotID the ChatLink ID to set
     */
    public static void setChatLinkID(String newBotID)
    {
        plugin.getConfig().set("ChatLinkID", newBotID);
        save();
    }

    /**
     * Returns the ChatLink ID that must match to communicate with a Discord Bot.
     * @return the ChatLink ID
     */
    public static String getChatLinkID()
    {
        return plugin.getConfig().getString("ChatLinkID");
    }
}
