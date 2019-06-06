import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import java.util.ArrayList;

public class ListQuotes extends Command
{

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        int pageNo = getPageNo(parameters);
        int listingsPerPage = 15;
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        FileManager fm = new FileManager(msgEvent.getGuild().getId());
        ArrayList<Quote> quotes = fm.getQuotes();

        if (quotes.size() > 0)
        {
            double totalPages = Math.ceil((double) quotes.size()/(double) listingsPerPage);
            int startingIndex = (listingsPerPage*pageNo)-listingsPerPage;
            int endIndex = (quotes.size() < startingIndex+listingsPerPage) ? quotes.size() : (startingIndex+listingsPerPage);

            StringBuilder listBuilder = new StringBuilder();
            for (int i = startingIndex; i<endIndex; i++)
            {
                listBuilder.append("**").append(quotes.get(i).name).append("** - ").append(quotes.get(i).date).append("\n");
            }
            embed.setDescription(listBuilder.toString());
            embed.setFooter("Page "+pageNo+" / "+(int) totalPages, null);
        }
        else
        {
            embed.setDescription("You have no quotes!");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int getPageNo(String[] parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].matches("[0-9]+"))
            {
                return Integer.parseInt(parameters[1]);
            }
        }
        return 1;
    }
}
