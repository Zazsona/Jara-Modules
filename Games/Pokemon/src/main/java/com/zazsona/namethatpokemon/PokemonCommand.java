package com.zazsona.namethatpokemon;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class PokemonCommand extends ModuleGameCommand
{
    private static HashMap<String, Integer> userIdToCaptureStreak;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (userIdToCaptureStreak == null)
                userIdToCaptureStreak = new HashMap<>();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));

            String[] pokemon = new PokemonList().getPokemon();
            Random r = new Random();
            int dexNo = r.nextInt(pokemon.length);
            String dexNoStr = Integer.toString(dexNo + 1);  //Arrays start at 0, Pokemon do not
            while (dexNoStr.length() < 3)       //Padding with 0s as this is what the asset file does.
            {
                dexNoStr = "0" + dexNoStr;
            }
            String URL = "http://assets.pokemon.com/assets/cms2/img/pokedex/full/" + dexNoStr + ".png";
            embed.setImage(URL);
            embed.setAuthor("Who's That Pokemon?");
            msgEvent.getChannel().sendMessage(embed.build()).queue();

            embed.setTitle("== Pokedex ==");
            embed.setAuthor("");
            embed.setThumbnail("https://i.imgur.com/wXSYWpN.png");
            embed.setImage(null);
            Message msgAnswer = new MessageManager().getNextMessage(msgEvent.getChannel());

            String userId = msgEvent.getMember().getId();
            int captureStreak = (userIdToCaptureStreak.containsKey(userId)) ? userIdToCaptureStreak.get(userId) : 0;
            if (msgAnswer.getContentDisplay().toLowerCase().contains(pokemon[dexNo].toLowerCase()))
            {
                captureStreak++;
                embed.getDescriptionBuilder().append("Gotcha! It's #"+dexNoStr+", "+pokemon[dexNo]+"!");
                if (captureStreak > 1)
                    embed.getDescriptionBuilder().append("\n").append("_Capture Streak: ").append(captureStreak).append("_");
                userIdToCaptureStreak.put(userId, captureStreak);
            }
            else
            {
                embed.setDescription("Sorry, it's #"+dexNoStr+", "+pokemon[dexNo]+".");
                if (captureStreak > 1)
                    embed.getDescriptionBuilder().append("\n").append("_Lost Streak: ").append(captureStreak).append("_");
                userIdToCaptureStreak.remove(userId);
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        catch (IOException e)
        {
            msgEvent.getChannel().sendMessage("Could not get Pokemon.");
        }
    }
}
