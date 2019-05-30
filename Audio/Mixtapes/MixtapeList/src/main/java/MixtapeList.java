import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class MixtapeList extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        HashMap<String, ArrayList<String>> mixtapes = SaveManager.getGuildMixtapes(msgEvent.getGuild().getIdLong());
        if (mixtapes.keySet().size() > 0)
        {
            StringBuilder descBuilder = new StringBuilder();
            for (String mixtapeName : mixtapes.keySet())
            {
                descBuilder.append(mixtapeName).append("\n");
            }
            embed.setDescription(descBuilder.toString());
        }
        else
        {
            embed.setDescription("You don't have any mixtapes!");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
