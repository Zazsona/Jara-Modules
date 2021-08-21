package com.zazsona.mixtapemanager;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MixtapeManager extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length >= 2)
        {
            if (parameters[1].equalsIgnoreCase("Create"))
            {
                createMixtape(msgEvent, parameters);
            }
            else if (parameters[1].equalsIgnoreCase("AddTracks"))
            {
                addTracks(msgEvent, parameters);
            }
            else if (parameters[1].equalsIgnoreCase("RemoveTracks"))
            {
                removeTracks(msgEvent, parameters);
            }
            else if (parameters[1].equalsIgnoreCase("Remove"))
            {
                removeMixtape(msgEvent, parameters);
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
            }
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }

    private void createMixtape(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        if (parameters.length >= 4)
        {
            String name = parameters[2];
            String[] tracks = new String[parameters.length-3];
            for (int i = 3; i<parameters.length; i++)
            {
                tracks[i-3] = parameters[i];
            }
            boolean success = SaveManager.createMixtape(msgEvent.getGuild().getIdLong(), name, tracks);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (success)
            {
                embed.setDescription("Created mixtape: "+name+".");
            }
            else
            {
                embed.setDescription("Mixtape with that name already exists.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }

    private void addTracks(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        if (parameters.length >= 4)
        {
            String name = parameters[2];
            String[] tracks = new String[parameters.length-3];
            for (int i = 3; i<parameters.length; i++)
            {
                tracks[i-3] = parameters[i];
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (!SaveManager.isMixtapeNameTaken(msgEvent.getGuild().getIdLong(), name))
            {
                SaveManager.addTracks(msgEvent.getGuild().getIdLong(), name, tracks);
                embed.setDescription("Tracks added successfully.");
            }
            else
            {
                embed.setDescription("Unable to find mixtape.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }

    private void removeTracks(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        if (parameters.length >= 4)
        {
            String name = parameters[2];
            String[] tracks = new String[parameters.length-3];
            for (int i = 3; i<parameters.length; i++)
            {
                tracks[i-3] = parameters[i];
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (!SaveManager.isMixtapeNameTaken(msgEvent.getGuild().getIdLong(), name))
            {
                SaveManager.removeTracks(msgEvent.getGuild().getIdLong(), name, tracks);
                embed.setDescription("Tracks removed successfully.");
            }
            else
            {
                embed.setDescription("Unable to find mixtape.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }

    private void removeMixtape(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        if (parameters.length == 3)
        {
            String name = parameters[2];
            SaveManager.removeMixtape(msgEvent.getGuild().getIdLong(), name);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("Mixtape removed.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }
}
