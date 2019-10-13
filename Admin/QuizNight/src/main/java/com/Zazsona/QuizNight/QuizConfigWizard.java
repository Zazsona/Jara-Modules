package com.Zazsona.QuizNight;

import com.Zazsona.QuizNight.json.GuildQuizConfig;
import com.Zazsona.QuizNight.system.SettingsManager;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;

public class QuizConfigWizard extends ModuleConfig
{
    private MessageManager mm = new MessageManager();
    private GuildQuizConfig gqc;


    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        gqc = SettingsManager.getInstance().getGuildQuizSettings(textChannel.getGuild().getId());
        while (true)
        {
            EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
            embed.setDescription("Configure quiz scheduling. Use this to run quizzes regularly in your server.\n\n**Schedule** - Add a quiz time\n**Roles** - Set who can join quizzes\n**Ping** - Ping everyone on quiz start\n**Quit** - Close config");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            Message input = getInput(msgEvent, textChannel);
            if (input == null)
            {
                if (!isSetup)
                {
                    embed.setDescription("Quiz config closed.");
                    textChannel.sendMessage(embed.build()).queue();
                }
                return;
            }
            else if (input.getContentDisplay().equalsIgnoreCase("schedule"))
            {
                QuizConfigScheduleWizard qcsw = new QuizConfigScheduleWizard();
                qcsw.run(msgEvent, textChannel, guildSettings, gqc, embed);
            }
            else if (input.getContentDisplay().equalsIgnoreCase("roles"))
            {
                modifyRoles(msgEvent, textChannel, embed);
            }
            else if (input.getContentDisplay().equalsIgnoreCase("ping"))
            {
                togglePing(msgEvent, textChannel, embed);
            }
            else
            {
                embed.setDescription("Unknown submenu. Please try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {

    }


    private Message getInput(GuildMessageReceivedEvent msgEvent, TextChannel textChannel) throws InvalidParameterException
    {
        Message message = null;
        while (true)
        {
            message = mm.getNextMessage(textChannel);
            if (message.getMember().equals(msgEvent.getMember()))
            {
                if (message.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId())+"quit") || message.getContentDisplay().equalsIgnoreCase("quit"))
                {
                    return null;
                }
                else if (!message.getContentDisplay().startsWith(String.valueOf(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId()))))
                {
                    return message;
                }
            }
        }
    }

    private void togglePing(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed)
    {
        embed.setDescription("Would you like an everyone ping to occur when a scheduled quiz is about to begin? (Y/N)\nCurrent status: "+((gqc.isPingQuizAnnouncement()) ? "Enabled" : "Disabled"));
        textChannel.sendMessage(embed.build()).queue();
        Message input = getInput(msgEvent, textChannel);
        if (input != null)
        {
            String response = input.getContentDisplay().toLowerCase();
            response = response.toLowerCase();
            if (response.equals("y") || response.equals("enable") || response.equals("yes"))
            {
                gqc.setPingQuizAnnouncement(true);
                embed.setDescription("Ping enabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else if (response.equals("n") || response.equals("disable") || response.equals("no"))
            {
                gqc.setPingQuizAnnouncement(false);
                embed.setDescription("Ping disabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        else
        {
            return;
        }
    }

    private String getCurrentPermittedRolesList(Guild guild)
    {
           StringBuilder sb = new StringBuilder();
           if (gqc.getAllowedRoles().size() > 0)
           {
               for (String roleID : gqc.getAllowedRoles())
               {
                   sb.append(guild.getRoleById(roleID).getName()+", ");
               }
               sb.setLength(sb.length()-2);
           }
           else
           {
               sb.append("everyone");
           }
           return sb.toString();
    }

    private void modifyRoles(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed) throws IOException
    {
        embed.setDescription("Please enter whether you would like to add or remove roles, and then mention the roles to modify.\nE.g: `add @admins @moderators` or `remove @members`\n\nCurrently permitted: "+getCurrentPermittedRolesList(msgEvent.getGuild()));
        textChannel.sendMessage(embed.build()).queue();
        Message input = getInput(msgEvent, textChannel);
        if (input != null)
        {
            String inputContent = input.getContentDisplay();
            boolean addRoles = inputContent.toLowerCase().startsWith("add");
            for (Role role : input.getMentionedRoles())
            {
                if (addRoles)
                    gqc.addAllowedRole(role.getId());
                else
                    gqc.removeAllowedRole(role.getId());
            }
            embed.setDescription("Roles "+((addRoles) ? "added." : "removed."));
            textChannel.sendMessage(embed.build()).queue();
        }
    }
}
