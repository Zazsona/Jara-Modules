package com.Zazsona.RemoveReminder;

import com.Zazsona.ReminderCore.Reminder;
import com.Zazsona.ReminderCore.ReminderManager;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.ArrayList;
import java.util.Comparator;

public class RemindersList
{
    private ArrayList<Reminder> reminders;

    public RemindersList(String userID)
    {
        reminders = ReminderManager.getRemindersForUser(userID);
        reminders.sort(Comparator.comparing((v) -> v.getFirstExecutionTime().toEpochSecond()));
    }

    public ArrayList<Reminder> getReminders()
    {
        return reminders;
    }

    public String getFormattedList()
    {
        if (reminders.size() > 0)
        {
            int id = 1;
            StringBuilder sb = new StringBuilder();
            for (Reminder reminder : reminders)
            {
                sb.append(id).append(". [").append(reminder.getFirstExecutionTime().getYear()).append("/").append(reminder.getFirstExecutionTime().getMonthValue()).append("/").append(reminder.getFirstExecutionTime().getDayOfMonth()).append("]\n");
                sb.append("*").append((reminder.getMessage().length() >= 40) ? reminder.getMessage().substring(0, 37)+"..." : reminder.getMessage()).append("*\n\n");
                id++;
            }
            return sb.toString();
        }
        else
        {
            return "You have no reminders.";
        }

    }

    public EmbedBuilder getFormattedEmbed(int pageNo)
    {
        if (reminders.size() > 0)
        {
            EmbedBuilder embed = new EmbedBuilder();
            int maxPage = (reminders.size()/24)+1;
            if (pageNo <= maxPage)
            {
                int startingID = (24*(pageNo-1));
                int id = startingID;
                for (int i = startingID; i<reminders.size(); i++)
                {
                    Reminder reminder = reminders.get(i);
                    id++;
                    if (id == startingID+25)
                    {
                        embed.addField("Etc.", "Please see the next page for more.", true);
                        break;
                    }
                    else
                    {
                        embed.addField(id+". ["+reminder.getFirstExecutionTime().getYear()+"/"+reminder.getFirstExecutionTime().getMonthValue()+"/"+reminder.getFirstExecutionTime().getDayOfMonth()+"]\n",
                                       "*"+ ((reminder.getMessage().length() >= 40) ? reminder.getMessage().substring(0, 37)+"..." : reminder.getMessage())+"*",
                                       true);
                    }

                }
                embed.setFooter("Page "+pageNo+"/"+((reminders.size()/24)+1), null);
            }
            else
            {
                embed.setDescription("This page is empty.");
            }
            return embed;
        }
        else
        {
            return new EmbedBuilder().setDescription("You have no reminders.");
        }

    }

    public Reminder getReminderByListID(int listID)
    {
        if (listID-1 < reminders.size())
        {
            return reminders.get(listID-1);
        }
        else
        {
            return null;
        }

    }
}
