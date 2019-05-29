import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class UTC extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle("=== UTC Timezone ===");
        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append("**Time:** ").append(getTime(utc)).append("\n\n");
        descBuilder.append("**Date:** ").append(utc.getYear()).append("/").append(utc.getMonthValue()).append("/").append(utc.getDayOfMonth()).append("\n\n");
        descBuilder.append("**Weekday:** ").append(getDayOfWeekPretty(utc.getDayOfWeek().getValue()));
        embed.setDescription(descBuilder.toString());
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private String getTime(OffsetDateTime utc)
    {
        StringBuilder clockBuilder = new StringBuilder();
        String hour = (utc.getHour() < 10) ? "0"+utc.getHour() : String.valueOf(utc.getHour());
        String minute = (utc.getMinute() < 10) ? "0"+utc.getMinute() : String.valueOf(utc.getMinute());

        int ampmHour = (utc.getHour() < 12) ? utc.getHour() : utc.getHour()-12;
        ampmHour = (ampmHour == 0) ? 12 : ampmHour;
        String ampm = (utc.getHour() < 12) ? "AM" : "PM";

        clockBuilder.append(hour).append(":").append(minute).append(" (").append(ampmHour).append(":").append(minute).append(ampm).append(")");
        return clockBuilder.toString();
    }

    private String getDayOfWeekPretty(int dayOfWeek)
    {
        switch (dayOfWeek)
        {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
        }
        return "";
    }

}
