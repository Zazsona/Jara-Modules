import commands.CmdUtil;
import commands.Load;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageSender extends Load
{
    FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        GuildJoinListener gjl = new GuildJoinListener();
        CmdUtil.getJDA().addEventListener(gjl);
    }

    private class GuildJoinListener extends ListenerAdapter
    {
        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent event)
        {
            if (fm.isGuildEnabled(event.getGuild().getId()))
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(event.getGuild().getSelfMember()));
                embed.setDescription(fm.getWelcomeMessage(event.getGuild().getId()));
                event.getMember().getUser().openPrivateChannel().complete().sendMessage(embed.build()).queue();
            }
        }
    }
}
