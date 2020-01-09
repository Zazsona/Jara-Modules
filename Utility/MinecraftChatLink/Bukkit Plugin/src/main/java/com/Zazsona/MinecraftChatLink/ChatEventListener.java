package com.Zazsona.MinecraftChatLink;

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
        MinecraftMessageManager.getInstance().sendMessageToDiscord(e.getPlayer().getDisplayName(), e.getMessage());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        MinecraftMessageManager.getInstance().sendMessageToDiscord("", event.getPlayer().getDisplayName()+" joined the game.");
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerLeaveEvent(PlayerQuitEvent event)
    {
        MinecraftMessageManager.getInstance().sendMessageToDiscord("", event.getPlayer().getDisplayName()+" left the game.");
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event)
    {
        MinecraftMessageManager.getInstance().sendMessageToDiscord("", event.getDeathMessage());
    }

}
