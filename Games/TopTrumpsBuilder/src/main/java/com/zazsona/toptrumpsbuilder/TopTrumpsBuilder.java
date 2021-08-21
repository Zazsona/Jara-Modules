package com.zazsona.toptrumpsbuilder;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import com.zazsona.toptrumpsbuilder.cards.Deck;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class TopTrumpsBuilder extends ModuleGameCommand
{
    private static final HashMap<String, LinkedList<String>> guildToEditingDecksMap = new HashMap<>();

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 2)
            {
                boolean isDeckCurrentlyOpen = handleConcurrentModification(msgEvent.getChannel(), parameters[2]);
                if (!isDeckCurrentlyOpen)
                {
                    FileManager fm = new FileManager(msgEvent.getGuild().getId());
                    TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-TopTrumpsDeckBuild");
                    if (parameters[1].equalsIgnoreCase("add"))
                    {
                        addDeck(channel, msgEvent.getMember(), fm, parameters[2]);
                        guildToEditingDecksMap.get(msgEvent.getGuild().getId()).remove(parameters[2].toLowerCase());
                    }
                    else if (parameters[1].equalsIgnoreCase("edit"))
                    {
                        editDeck(channel, msgEvent.getMember(), fm, fm.getDeck(parameters[2]));
                        guildToEditingDecksMap.get(msgEvent.getGuild().getId()).remove(parameters[2].toLowerCase());
                    }
                    else if (parameters[1].equalsIgnoreCase("delete"))
                    {
                        deleteDeck(channel, fm, parameters[2]);
                        guildToEditingDecksMap.get(msgEvent.getGuild().getId()).remove(parameters[2].toLowerCase());
                    }
                    else
                    {
                        CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
                    }
                }
            }
            else if (parameters.length > 1 && parameters[1].equalsIgnoreCase("list"))
            {
                FileManager fm = new FileManager(msgEvent.getGuild().getId());
                listDecks(msgEvent.getChannel(), fm);
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
            }
        }
        catch (IOException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("\n**ERROR: Unable to access custom decks.**");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            LoggerFactory.getLogger("TopTrumps Deck Builder").error(e.toString());
            e.printStackTrace();
        }
    }

    private void editDeck(TextChannel channel, Member user, FileManager fm, Deck deck)
    {
        if (deck != null)
        {
            DeckEditor de = new DeckEditor(deck, user, channel);
            de.run(fm);
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
            embed.setDescription("No deck with that name exists.");
            channel.sendMessage(embed.build()).queue();
        }
    }

    private boolean handleConcurrentModification(TextChannel channel, String deckName)
    {
        if (guildToEditingDecksMap.containsKey(channel.getGuild().getId()))
        {
            if (guildToEditingDecksMap.containsKey(deckName.toLowerCase()))
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
                embed.setDescription("This deck is currently being modified.");
                channel.sendMessage(embed.build()).queue();
                return true;
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
        return false;
    }

    private void addDeck(TextChannel channel, Member user, FileManager fm, String deckName) throws IOException
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
            editDeck(channel, user, fm, deck);
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

    private void listDecks(TextChannel channel, FileManager fm)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        try
        {
            if (fm.getDeckNames().size() > 0)
            {
                StringBuilder descBuilder = new StringBuilder();
                for (String deckName : fm.getDeckNames())
                {
                    descBuilder.append("- ").append(deckName).append("\n");
                }
                embed.setDescription(descBuilder.toString());
            }
            else
            {
                throw new NullPointerException("There are no decks.");
            }
        }
        catch (NullPointerException e)
        {
            embed.setDescription("You have no custom decks!");
        }
        finally
        {
            channel.sendMessage(embed.build()).queue();
        }
    }





}
