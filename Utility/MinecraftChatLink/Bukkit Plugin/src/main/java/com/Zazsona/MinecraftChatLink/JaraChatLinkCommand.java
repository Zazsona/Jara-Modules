package com.Zazsona.MinecraftChatLink;

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
            sender.sendMessage("[Commands]\n" +
                                       "Enable - Enables the plugin\n" +
                                       "Disable - Disables the plugin\n" +
                                       "SetID - Sets the ChatLink ID of the Discord server to connect with");
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
            else if (args[0].equalsIgnoreCase("SetID"))
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
        sender.sendMessage(ChatColor.GREEN+"Jara Chat Link is now "+((newState) ? ChatColor.BLUE+"enabled." : ChatColor.RED+"disabled.")+ChatColor.WHITE+"\nRestarting connection... (This may take up to a minute)");
        new Thread(() -> MinecraftMessageManager.getInstance().startConnection()).start();
    }

    /**
     * Sets the Discord User ID for the Jara bot to negotiate with. This needs to be set for communication.
     * @param sender the command user
     * @param newID the bot ID
     */
    private void updateChatLinkID(CommandSender sender, String newID)
    {
        Settings.setChatLinkID(newID);
        sender.sendMessage(ChatColor.GREEN+"Jara ChatLink ID set to "+newID+ChatColor.WHITE+"\nRestarting connection... (This may take up to a minute)");
        new Thread(() -> MinecraftMessageManager.getInstance().startConnection()).start();
    }
}
