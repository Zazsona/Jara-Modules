import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class CalendarEventCommand extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 1)
            {
                if (parameters[1].equalsIgnoreCase("list"))
                    list(msgEvent);
                else if (parameters[1].equalsIgnoreCase("add"))
                    addEvent(msgEvent, parameters);
                else if (parameters[1].equalsIgnoreCase("remove"))
                    remove(msgEvent, parameters);
                else if (parameters[1].equalsIgnoreCase("Time"))
                    setTime(msgEvent, parameters);
                else
                    throw new InvalidParameterException();
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        catch (InvalidParameterException e)
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }

    private void list(GuildMessageReceivedEvent msgEvent)
    {
        HashMap<Integer, ArrayList<CalendarEvent>> events = CalendarEventScheduler.getCalendarEventManager().getGuildEvents(msgEvent.getGuild().getIdLong());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        if (events != null)
        {
            for (Integer dayOfYear : events.keySet())
            {
                StringBuilder eventBuilder = new StringBuilder();
                for (CalendarEvent ce : events.get(dayOfYear))
                {
                    int snippetLength = (ce.getMessage().length() < 15) ? ce.getMessage().length() : 15;
                    eventBuilder.append(ce.getName()).append(" - *").append(ce.getMessage().replace("\n", "").subSequence(0, snippetLength)).append("*...").append("\n");
                }
                embed.addField(convertLeapDayToDate(dayOfYear), eventBuilder.toString(), true);
            }
        }
        else
        {
            embed.setDescription("You have no events.");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();

    }

    private void remove(GuildMessageReceivedEvent msgEvent, String[] parameters) throws InvalidParameterException
    {
        if (parameters.length == 3)
        {
            boolean success = CalendarEventScheduler.getCalendarEventManager().removeEvent(msgEvent.getGuild().getIdLong(), parameters[2]);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (success)
            {
                embed.setDescription("Event removed successfully.");
            }
            else
            {
                embed.setDescription("That event does not exist.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            throw new InvalidParameterException();
        }
    }

    private void setTime(GuildMessageReceivedEvent msgEvent, String[] parameters) throws InvalidParameterException
    {
        boolean changeOccured = false;
        int hour = 0;
        int minute = 0;
        for (String param : parameters)
        {
            if (param.contains(":"))
            {
                String time = param.toLowerCase().replace("am", "").replace("pm", "");
                String[] clock = time.split(":");
                hour = Integer.parseInt(clock[0]);
                if (clock.length > 1)
                    minute = Integer.parseInt(clock[1]);

                if (param.toLowerCase().contains("pm"))
                {
                    hour += 12;
                    hour = (hour >= 24) ? hour-24 : hour;
                }
                changeOccured = true;
            }
            else if (param.toLowerCase().matches("[0-9]+") || param.toLowerCase().matches("[0-9]+am") || param.toLowerCase().matches("[0-9]+pm"))
            {
                hour = Integer.parseInt(param.toLowerCase().replace("pm", "").replace("am", ""));
                minute = 0;
                if (param.toLowerCase().contains("pm"))
                {
                    hour += 12;
                    hour = (hour >= 24) ? hour-24 : hour;
                }
                changeOccured = true;
            }
            else if (param.equalsIgnoreCase("pm")) //This allows for both 12:00PM and 12:00 PM.
            {
                hour += 12;
                hour = (hour >= 24) ? hour-24 : hour;
            }
        }
        if (changeOccured)
        {
            int startMinute = (hour*60)+minute;
            CalendarEventScheduler.getCalendarEventManager().setGuildEventTime(msgEvent.getGuild().getIdLong(), startMinute);
            String strHour = (hour < 10) ? "0"+hour : ""+hour;
            String strMinute = (minute < 10) ? "0"+minute : ""+minute;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("Event time set to "+strHour+":"+strMinute+" (UTC)");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            throw new InvalidParameterException();
        }
    }

    private void addEvent(GuildMessageReceivedEvent msgEvent, String[] parameters) throws InvalidParameterException
    {
        if (parameters.length >= 5)
        {
            String name = parameters[2];
            int day = convertDateToLeapDay(parameters[3]);
            StringBuilder msgBuilder = new StringBuilder();
            for (int i = 4; i<parameters.length; i++)
            {
                msgBuilder.append(parameters[i]).append(" ");
            }
            CalendarEvent ce = new CalendarEvent(msgEvent.getGuild().getIdLong(), name, msgBuilder.toString().trim());
            boolean success = CalendarEventScheduler.getCalendarEventManager().addEvent(ce, day);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (success)
            {
                StringBuilder descBuilder = new StringBuilder();
                descBuilder.append("**Successfully added event.**\n\n");
                descBuilder.append("Name: ").append(ce.getName()).append("\n");
                descBuilder.append("Date: ").append(parameters[3]).append("\n");
                descBuilder.append("Message: ").append(ce.getMessage());
                embed.setDescription(descBuilder.toString());
            }
            else
            {
                embed.setDescription("That name is already taken.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            throw new InvalidParameterException();
        }
    }



    private int convertDateToLeapDay(String date) throws InvalidParameterException
    {
        if (date.matches("[0-9]+/[0-9]+"))
        {
            String[] dateValues = date.split("/");
            int day = Integer.parseInt(dateValues[1]);
            int month = Integer.parseInt(dateValues[0]);

            int[] leapMonthDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            int totalLeapDays = 0;
            for (int i = 0; i<month-1; i++) //-1 from month as Jan is 1, but array position 0.
            {
                totalLeapDays += leapMonthDays[i];
            }
            totalLeapDays += day;
            return totalLeapDays;
        }
        else
        {
            throw new InvalidParameterException("Invalid date format.");
        }
    }

    private String convertLeapDayToDate(int leapDay)
    {
        int[] leapMonthDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int day = leapDay;
        int month = 0;
        while (day > leapMonthDays[month])
        {
            day -= leapMonthDays[month];
            month++;
        }
        month++; //Convert from array index to human index
        String dayString = ""+day;
        while (dayString.length() < 2)
        {
            dayString = "0"+dayString;
        }
        String monthString = ""+month;
        while (monthString.length() < 2)
        {
            monthString = "0"+monthString;
        }
        return monthString+"/"+dayString;
    }
}
