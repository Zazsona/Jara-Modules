package com.Zazsona.RemoveReminder;

import com.Zazsona.ReminderCore.Reminder;
import com.Zazsona.ReminderCore.ReminderManager;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DeleteReminder extends ModuleCommand
{

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].matches("[0-9]+"))
            {
                int reminderID = Integer.parseInt(parameters[1]);
                deleteReminder(msgEvent, reminderID);
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
            }
        }
        else
        {
            runWizard(msgEvent);
            return;
        }
    }

    private void runWizard(GuildMessageReceivedEvent msgEvent)
    {
        int pageNo = 1;
        MessageManager msgManager = new MessageManager();
        EmbedBuilder noticeEmbed = new EmbedBuilder().setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        while (true)
        {
            RemindersList remindersList = new RemindersList(msgEvent.getMember().getUser().getId());
            EmbedBuilder embed = remindersList.getFormattedEmbed(pageNo);
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setTitle("Your Reminders");
            embed.setAuthor("Say `Page #` to select a page, or `quit` to quit.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();

            Message msg = msgManager.getNextMessage(msgEvent.getChannel(), msgEvent.getMember());
            String msgContent = msg.getContentDisplay();
            if (msgContent.matches("[0-9]+"))
            {
                int reminderID = Integer.parseInt(msgContent);
                deleteReminder(msgEvent, reminderID);
            }
            else if (msgContent.matches("Page [0-9]+") || msgContent.matches("page [0-9]+"))
            {
                try
                {
                    String strPageNo = msgContent.replace("Page ", "").replace("page ", "");
                    pageNo = Integer.parseInt(strPageNo);
                }
                catch (NumberFormatException e)
                {
                    msg.getChannel().sendMessage(noticeEmbed.setDescription("Invalid page number.").build()).queue();
                }
            }
            else if (msgContent.equalsIgnoreCase("quit") || msgContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId())+"quit"))
            {
                msg.getChannel().sendMessage(noticeEmbed.setDescription("Reminders closed.").build()).queue();
                return;
            }
            else
            {
                noticeEmbed.setDescription("Unknown option. Please use "+SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId())+"quit to stop removing reminders.");
                msg.getChannel().sendMessage(noticeEmbed.build()).queue();
            }
        }
    }

    private void deleteReminder(GuildMessageReceivedEvent msgEvent, int reminderID)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle("Delete Reminder");
        try
        {
            RemindersList remindersList = new RemindersList(msgEvent.getMember().getUser().getId());
            Reminder reminder = remindersList.getReminderByListID(reminderID);
            if (reminder != null)
            {
                ReminderManager.deleteReminder(reminder);
                embed.setDescription("Successfully deleted reminder:\n"+reminder.getMessage());
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
            else
            {
                embed.setDescription("There is no reminder with that ID.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        catch (IOException e)
        {
            embed.setDescription("An error occurred when removing the reminder.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            LoggerFactory.getLogger(getClass()).error(e.toString());
        }
    }
}
