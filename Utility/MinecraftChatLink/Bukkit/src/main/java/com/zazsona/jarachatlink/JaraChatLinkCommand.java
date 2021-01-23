package com.zazsona.jarachatlink;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JaraChatLinkCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 0)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.YELLOW + "---------" + ChatColor.WHITE + " Jara Chat Link ").append(ChatColor.YELLOW).append("--------------------").append(ChatColor.WHITE).append("\n");
            sb.append(ChatColor.GRAY+"Chat link between Minecraft and Discord!\n"+ChatColor.WHITE);
            sb.append(ChatColor.GOLD+"/JaraChatLink Enable - ").append(ChatColor.WHITE+"Enables the chat link.\n");
            sb.append(ChatColor.GOLD+"/JaraChatLink Disable - ").append(ChatColor.WHITE+"Disables the chat link.\n");
            sb.append(ChatColor.GOLD+"/JaraChatLink SetId [Id] - ").append(ChatColor.WHITE+"Set the link id.\n");
            sender.sendMessage(sb.toString());
        }
        else
        {
            if (args[0].equalsIgnoreCase("Enable"))
            {
                if (sender.hasPermission("JaraChatLink.Config"))
                    updateEnabledState(sender, true);
            }
            else if (args[0].equalsIgnoreCase("Disable"))
            {
                if (sender.hasPermission("JaraChatLink.Config"))
                    updateEnabledState(sender, false);
            }
            else if (args[0].equalsIgnoreCase("SetId"))
            {
                if (sender.hasPermission("JaraChatLink.Config"))
                    updateChatLinkID(sender, args[args.length-1]);
            }
        }
        return true;
    }

    /**
     * Sets the enabled state and restarts the Jara link
     * @param sender the command user
     * @param newState the new state
     */
    private void updateEnabledState(CommandSender sender, boolean newState)
    {
        Settings.setEnabled(newState);
        sender.sendMessage(ChatColor.GREEN+"Jara Chat Link is now "+((newState) ? ChatColor.BLUE+"enabled." : ChatColor.RED+"disabled.")+ChatColor.WHITE+"\nRestarting connection...");
    }

    /**
     * Sets the Discord User ID for the Jara bot to negotiate with. This needs to be set for communication.
     * @param sender the command user
     * @param newID the bot ID
     */
    private void updateChatLinkID(CommandSender sender, String newID)
    {
        Settings.setLinkId(newID);
        sender.sendMessage(ChatColor.GREEN+"Jara ChatLink ID set to "+newID+ChatColor.WHITE+"\nRestarting connection...");
    }
}
