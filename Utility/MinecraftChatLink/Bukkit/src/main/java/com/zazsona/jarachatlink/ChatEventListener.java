package com.zazsona.jarachatlink;

import com.zazsona.jarachatlink.ChatLinkServer;
import com.zazsona.jarachatlink.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class ChatEventListener implements Listener
{
    private Random r = new Random();
    private static Plugin plugin = Core.getPlugin(Core.class);

    @EventHandler (priority = EventPriority.LOWEST)
    public void onChatEvent(AsyncPlayerChatEvent e)
    {
        Core.getChatLinkServer().sendMessageToDiscord(e.getPlayer().getUniqueId().toString(), e.getPlayer().getDisplayName(), e.getMessage());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Core.getChatLinkServer().sendMessageToDiscord(event.getPlayer().getUniqueId().toString(), "", event.getPlayer().getDisplayName()+" joined the game.");
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerLeaveEvent(PlayerQuitEvent event)
    {
        Core.getChatLinkServer().sendMessageToDiscord(event.getPlayer().getUniqueId().toString(), "",event.getPlayer().getDisplayName()+" left the game.");
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event)
    {
        Core.getChatLinkServer().sendMessageToDiscord(event.getEntity().getUniqueId().toString(), "", event.getDeathMessage());
    }

}
