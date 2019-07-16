import cards.Card;
import cards.Deck;
import com.sun.security.auth.callback.TextCallbackHandler;
import commands.CmdUtil;
import commands.GameCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TopTrumpsBuilder extends GameCommand
{
    private static HashMap<String, LinkedList<String>> guildToEditingDecksMap = new HashMap<>();

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 2)
            {
                FileManager fm = new FileManager(msgEvent.getGuild().getId());
                if (parameters[1].equalsIgnoreCase("add"))
                {
                    handleConcurrentModification(msgEvent.getChannel(), parameters[2]);
                    addDeck(msgEvent.getChannel(), fm, parameters[2]);
                    guildToEditingDecksMap.get(msgEvent.getGuild().getId()).remove(parameters[2].toLowerCase());
                }
                else if (parameters[1].equalsIgnoreCase("edit"))
                {
                    handleConcurrentModification(msgEvent.getChannel(), parameters[2]);
                    editDeck(msgEvent.getChannel(), msgEvent.getMember(), fm, fm.getDeck(parameters[2]));
                    guildToEditingDecksMap.get(msgEvent.getGuild().getId()).remove(parameters[2].toLowerCase());
                }
                else if (parameters[1].equalsIgnoreCase("delete"))
                {
                    handleConcurrentModification(msgEvent.getChannel(), parameters[2]);
                    deleteDeck(msgEvent.getChannel(), fm, parameters[2]);
                    guildToEditingDecksMap.get(msgEvent.getGuild().getId()).remove(parameters[2].toLowerCase());
                }
                else if (parameters[1].equalsIgnoreCase("list"))
                {
                    listDecks(msgEvent.getChannel(), fm);
                }
                else
                {
                    CmdUtil.sendHelpInfo(msgEvent, getClass());
                }
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        catch (IOException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("\n**ERROR: Unable to access custom decks.**");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            LoggerFactory.getLogger("TopTrumps Deck Builder").error(e.getLocalizedMessage());
        }
    }

    private void editDeck(TextChannel channel, Member user, FileManager fm, Deck deck) throws IOException
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        if (deck != null)
        {
            DeckEditor de = new DeckEditor(deck, user, channel);
            deck = de.run();
            fm.saveDeck(deck);
            embed.setDescription("Saved edits successfully.");
        }
        else
        {
            embed.setDescription("No deck with that name exists.");
        }
        channel.sendMessage(embed.build()).queue();
    }

    private void handleConcurrentModification(TextChannel channel, String deckName)
    {
        if (guildToEditingDecksMap.containsKey(channel.getGuild().getId()))
        {
            if (guildToEditingDecksMap.containsKey(deckName.toLowerCase()))
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
                embed.setDescription("This deck is currently being modified.");
                channel.sendMessage(embed.build()).queue();
            }
            else
            {
                guildToEditingDecksMap.get(channel.getGuild().getId()).add(deckName.toLowerCase());
            }
        }
        else
        {
            LinkedList<String> deckNameList = new LinkedList<>();
            deckNameList.add(deckName.toLowerCase());
            guildToEditingDecksMap.put(channel.getGuild().getId(), deckNameList);
        }
    }

    private void addDeck(TextChannel channel, FileManager fm, String deckName) throws IOException
    {
        if (fm.getDeckNames().contains(deckName.toLowerCase()))
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
            embed.setDescription("A deck with that name already exists.");
            channel.sendMessage(embed.build()).queue();
        }
        else
        {
            Deck deck = new Deck(deckName);
            //TODO: DECKMANAGER CLASS
        }
    }

    private void editDeck(TextChannel channel, FileManager fm, String deckName) throws IOException
    {
        if (!fm.getDeckNames().contains(deckName.toLowerCase()))
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
            embed.setDescription("No deck with name \""+deckName+"\" exists.");
            channel.sendMessage(embed.build()).queue();
        }
        else
        {
            Deck deck = fm.getDeck(deckName);
            //TODO: DECKMANAGER CLASS
        }
    }

    private void deleteDeck(TextChannel channel, FileManager fm, String deckName)
    {
        boolean success = fm.deleteDeck(deckName);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.setDescription((success) ? "Deck \""+deckName+"\" successfully deleted." : "No deck with name \""+deckName+"\" exists.");
        channel.sendMessage(embed.build()).queue();
    }

    private void listDecks(TextChannel channel, FileManager fm) throws IOException
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        StringBuilder descBuilder = new StringBuilder();
        for (String deckName : fm.getDeckNames())
        {
            descBuilder.append("- ").append(deckName).append("\n");
        }
        embed.setDescription(descBuilder.toString());
        channel.sendMessage(embed.build()).queue();
    }





}
