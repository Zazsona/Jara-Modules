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
        if (parameters.length == 1 || parameters.length > 1 && parameters[1].matches("[0-9]+"))
        {
            listMixtapes(msgEvent, parameters);
        }
        else
        {
            listTracks(msgEvent, parameters);
        }

    }

    private void listMixtapes(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        int pageNo = getPageNo(parameters[parameters.length-1]);
        EmbedBuilder embed = new EmbedBuilder();
        ArrayList<String> mixtapes = new ArrayList<>();
        mixtapes.addAll(SaveManager.getGuildMixtapes(msgEvent.getGuild().getIdLong()).keySet());
        if (mixtapes.size() > 0)
        {
            embed = buildPageEmbed(msgEvent, mixtapes, 15, pageNo);
        }
        else
        {
            embed.setDescription("You don't have any mixtapes!");
        }
        embed.setTitle("Mixtapes");
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int getPageNo(String param)
    {
        if (param.matches("[0-9]+"))
        {
            return Integer.parseInt(param);
        }
        return 1;
    }

    private void listTracks(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        if (parameters.length > 1)
        {
            int pageNo = getPageNo(parameters[parameters.length-1]);
            ArrayList<String> tracks = SaveManager.getTracks(msgEvent.getGuild().getIdLong(), parameters[1]);
            if (tracks != null)
            {
                embed = buildPageEmbed(msgEvent, tracks, 15, pageNo);
                embed.setTitle("Tracks for "+parameters[1]);
            }
        }
        else
        {
            embed.setDescription("Mixtape not found.");
        }
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private EmbedBuilder buildPageEmbed(GuildMessageReceivedEvent msgEvent, ArrayList<String> elements, int listingsPerPage, int pageNo)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        double totalPages = Math.ceil((double) elements.size()/(double) listingsPerPage);
        int startingIndex = (listingsPerPage*pageNo)-listingsPerPage;
        int endIndex = (elements.size() < startingIndex+listingsPerPage) ? elements.size() : (startingIndex+listingsPerPage);

        StringBuilder descBuilder = new StringBuilder();
        for (int i = startingIndex; i<endIndex; i++)
        {
            descBuilder.append(elements.get(i)).append("\n");
        }
        embed.setDescription(descBuilder.toString());
        embed.setFooter("Page "+pageNo+" / "+(int) totalPages, null);
        return embed;
    }
}
