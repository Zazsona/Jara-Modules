package com.Zazsona.JaraChatLink;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.Random;

public class ChatEventListener implements Listener
{
    private Random r = new Random();
    private static Plugin plugin = Core.getPlugin(Core.class);

    @EventHandler (priority = EventPriority.LOWEST)
    public void onChatEvent(AsyncPlayerChatEvent e)
    {
        e.getPlayer().getServer().broadcastMessage("Received Message:\n"+e.getMessage());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerLeaveEvent(PlayerQuitEvent event)
    {

    }

}
