import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ListReminders extends Command
{

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        int pageNo = 1;
        if (parameters.length > 1)
        {
            if (parameters[1].matches("[0-9]+"))
            {
                pageNo = Integer.parseInt(parameters[1]);
            }
        }
        RemindersList remindersList = new RemindersList(msgEvent.getMember().getUser().getId());
        EmbedBuilder embed = remindersList.getFormattedEmbed(pageNo);
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle("Your Reminders");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
