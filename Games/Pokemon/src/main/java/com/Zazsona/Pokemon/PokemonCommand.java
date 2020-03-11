package com.Zazsona.Pokemon;

import commands.CmdUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.Random;

public class PokemonCommand extends ModuleGameCommand
{

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));

            String[] pokemon = new PokemonList().getPokemon();
            Random r = new Random();
            int DexNo = r.nextInt(pokemon.length);
            String StrDexNo = Integer.toString(DexNo + 1);  //Arrays start a 0, Pokemon do not
            while (StrDexNo.length() < 3)       //Padding with 0s as this is what the asset file does.
            {
                StrDexNo = "0" + StrDexNo;
            }
            String URL = "http://assets.pokemon.com/assets/cms2/img/pokedex/full/" + StrDexNo + ".png";
            embed.setImage(URL);
            embed.setAuthor("Who's That Pokemon?");
            msgEvent.getChannel().sendMessage(embed.build()).queue();

            embed.setTitle("== Pokedex ==");
            embed.setAuthor("");
            embed.setThumbnail("https://i.imgur.com/wXSYWpN.png");
            embed.setImage(null);
            Message msgAnswer = new MessageManager().getNextMessage(msgEvent.getChannel());
            if (msgAnswer.getContentDisplay().toLowerCase().contains(pokemon[DexNo].toLowerCase()))
            {
                embed.setDescription("Gotcha! It's "+pokemon[DexNo]+"!");
            }
            else
            {
                embed.setDescription("Sorry, it's "+pokemon[DexNo]+".");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        catch (IOException e)
        {
            msgEvent.getChannel().sendMessage("Could not get Pokemon.");
        }
    }
}
