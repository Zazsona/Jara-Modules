package com.Zazsona.JaraChatLink;

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
                                       "SetID - Sets the Discord ID of the bot to connect with");
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
                    updateEnabledState(sender, true);
            }
        }
        return true;
    }

    private void updateEnabledState(CommandSender sender, boolean newState)
    {
        Settings.setEnabled(newState);
        sender.sendMessage(ChatColor.GREEN+"Jara Chat Link is now "+((newState) ? ChatColor.BLUE+"enabled." : ChatColor.RED+"disabled."));
    }

    private void updateBotID(CommandSender sender, String newID)
    {
        Settings.setBotID(newID);
        sender.sendMessage(ChatColor.GREEN+"Discord Bot ID set to "+newID);
    }
}
