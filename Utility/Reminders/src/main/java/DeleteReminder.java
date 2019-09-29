import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;
import reminderCore.Reminder;
import reminderCore.ReminderManager;

import java.io.IOException;

public class DeleteReminder extends Command
{

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].matches("[0-9]+"))
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                embed.setTitle("Delete Reminder");
                try
                {
                    RemindersList remindersList = new RemindersList(msgEvent.getMember().getUser().getId());
                    Reminder reminder = remindersList.getReminderByListID(Integer.parseInt(parameters[1]));
                    if (reminder != null)
                    {
                        ReminderManager.deleteReminder(reminder);
                        embed.setDescription("Successfully deleted reminder:\n"+reminder.getMessage());
                    }
                    else
                    {
                        embed.setDescription("There is no reminder with that ID.");
                    }
                }
                catch (IOException e)
                {
                    embed.setDescription("An error occurred when removing the reminder.");
                    LoggerFactory.getLogger(getClass()).error(e.toString());
                }
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }
}
