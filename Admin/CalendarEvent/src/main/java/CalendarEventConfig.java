import commands.CmdUtil;
import configuration.GuildSettings;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;

public class CalendarEventConfig extends ModuleConfig
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        EmbedBuilder embed = this.getDefaultEmbedStyle(msgEvent);
        embed.setDescription("Please set a time (UTC) for calendar event messages to trigger.\nSupported Formats:\n00:00 (24 Hour)\n12:00AM (12 Hour)");
        textChannel.sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        Message message = null;
        while (true)
        {
            try
            {
                while (true)
                {
                    message = mm.getNextMessage(textChannel);
                    if (message.getMember().equals(msgEvent.getMember()))
                    {
                        break;
                    }
                }
                setTime(msgEvent, message.getContentDisplay());
                return;
            }
            catch (InvalidParameterException e)
            {
                embed.setDescription("Invalid time format. Please try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }


    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        if (collection.size() > 0)
        {
            try
            {
                StringBuilder sb = new StringBuilder();
                for (String parameter : collection)
                {
                    sb.append(parameter).append(" ");
                }
                setTime(msgEvent, sb.toString().trim());
            }
            catch (InvalidParameterException e)
            {
                EmbedBuilder embed = this.getDefaultEmbedStyle(msgEvent);
                embed.setDescription("Invalid time format. Please try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
        else
        {
            run(msgEvent, guildSettings, textChannel);
        }
    }


    public static void setTime(GuildMessageReceivedEvent msgEvent, String timeInput) throws InvalidParameterException
    {
        String[] inputElements = timeInput.split(" ");
        boolean changeOccured = false;
        int hour = 0;
        int minute = 0;
        for (String element : inputElements)
        {
            if (element.contains(":"))
            {
                String time = element.toLowerCase().replace("am", "").replace("pm", "");
                String[] clock = time.split(":");
                hour = Integer.parseInt(clock[0]);
                if (clock.length > 1)
                    minute = Integer.parseInt(clock[1]);

                if (element.toLowerCase().contains("pm"))
                {
                    hour += 12;
                    hour = (hour >= 24) ? hour-24 : hour;
                }
                changeOccured = true;
            }
            else if (element.toLowerCase().matches("[0-9]+") || element.toLowerCase().matches("[0-9]+am") || element.toLowerCase().matches("[0-9]+pm"))
            {
                hour = Integer.parseInt(element.toLowerCase().replace("pm", "").replace("am", ""));
                minute = 0;
                if (element.toLowerCase().contains("pm"))
                {
                    hour += 12;
                    hour = (hour >= 24) ? hour-24 : hour;
                }
                changeOccured = true;
            }
            else if (element.equalsIgnoreCase("pm")) //This allows for both 12:00PM and 12:00 PM.
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
}
