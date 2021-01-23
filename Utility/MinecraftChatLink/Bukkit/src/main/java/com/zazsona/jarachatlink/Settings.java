package com.zazsona.jarachatlink;

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
        return (boolean) plugin.getConfig().get("Enabled");
    }

    public static void setLinkId(String linkId)
    {
        plugin.getConfig().set("ChatLinkId", linkId);
        save();
    }

    public static String getLinkId()
    {
        String id = (String) plugin.getConfig().get("ChatLinkId");
        return id;
    }
}
