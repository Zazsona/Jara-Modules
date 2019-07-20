import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Report extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        StringBuilder reportSB = new StringBuilder();
        reportSB.append("Bot User: "+msgEvent.getJDA().getSelfUser().getName()+"#"+msgEvent.getJDA().getSelfUser().getDiscriminator()+"\n");
        reportSB.append("Status: Online\n");
        reportSB.append("Uptime: "+(ManagementFactory.getRuntimeMXBean().getUptime()/1000/60/60)+" Hours\n");
        reportSB.append("DateTime: " + OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss")) + "(UTC) \n");
        reportSB.append("Shard: "+msgEvent.getJDA().getShardInfo().getShardId()+"\n");
        reportSB.append("Shard Total: "+msgEvent.getJDA().getShardInfo().getShardTotal()+"\n");
        reportSB.append("Server: "+msgEvent.getGuild().getName()+"\n");
        reportSB.append("Channel: #"+msgEvent.getChannel().getName()+"\n");
        reportSB.append("Ping: "+msgEvent.getJDA().getPing()+"ms\n");
        reportSB.append("Command Author: "  + msgEvent.getAuthor().getName()+"#"+msgEvent.getAuthor().getDiscriminator());
        if (msgEvent.getMember().getNickname() != null)
        {
            reportSB.append(" (" + msgEvent.getMember().getNickname() + ")");   //Display guild specific nickname, too.
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(msgEvent.getJDA().getSelfUser().getName()+" Report:");
        embed.setDescription(reportSB.toString());
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));

        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int getUptimeHours()
    {
        return (int)
    }
}
