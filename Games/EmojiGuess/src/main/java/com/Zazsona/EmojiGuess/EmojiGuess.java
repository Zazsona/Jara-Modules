package com.Zazsona.EmojiGuess;

import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class EmojiGuess extends ModuleGameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        final int startingGuesses = 3;
        int guesses = startingGuesses;
        MessageManager mm = new MessageManager();
        Random r = new Random();
        boolean victory = false;

        TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Emoji-Deciphering-Club");

        String[][] puzzles = new String[][] {
                {":gem: :two_women_holding_hands: ", "diamonds are a girl's best friend", "diamonds are a girls best friend"},
                {":slight_smile: :cake:", "happy birthday"},
                {":sunny: :eyeglasses:", "sunglasses", "sun glasses"},
                {":pizza: :circus_tent:", "pizzahut", "pizza hut"},
                {":crossed_swords: :ship:", "battleship"},
                {":spy:  :house: ", "sherlock"},
                {":snowflake: :sunglasses:", "cool"},
                {":snowflake: :red_car:", "snowmobile"},
                {":basketball:  :blue_car: ", "rocket", "league"},
                {":bee: :arrow_right: :back: ", "brb"},
                {":fire: :basketball:", "fireball"},
                {":dog: :house: ", "doghouse"},
                {":laughing: :clubs: :hearts: :spades: :diamonds:", "joker"},
                {":slight_smile: :hourglass: ", "happyhour", "happy hour"},
                {":bee: :leaves: ", "believe"},
                {":rice: :baseball: ", "riceball", "rice ball"},
                {":boom: :two_men_holding_hands: ", "smash bros", "smash", "smash brothers"},
                {":snowflake: :telephone_receiver: ", "coldcaller", "cold caller"},
                {":ant: :person_with_blond_hair: ", "antman", "ant man"},
                {":musical_score: :seat: ", "musicalchairs", "musical chairs"},
                {":right_facing_fist: :hamburger: ", "knuckle sandwich", "knucklesandwich"},
                {":drum: :boy: ", "drummer boy", "drummerboy"},
                {":ghost: :spy: ", "phantom thief", "phantomthief"},
                {":arrow_double_up:  :gear: ", "top gear", "topgear"},
                {":full_moon: :wolf: ", "werewolf"},
                {":video_game: :x: ", "game over", "gameover"}
        };

        int puzzleID = r.nextInt(puzzles.length);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setDescription(puzzles[puzzleID][0]);
        channel.sendMessage(embed.build()).queue();


        while (guesses > 0 && !victory)
        {
            if (guesses < startingGuesses)
            {
                embed.setDescription("Sorry, that's not it.\n"+(guesses)+" guess(es) left!");
                channel.sendMessage(embed.build()).queue();
            }
            Message message = mm.getNextMessage(channel);
            guesses--;
            String msgContent = message.getContentDisplay().toLowerCase();
            for (int i = 1; i<puzzles[puzzleID].length; i++)
            {
                if (msgContent.contains(puzzles[puzzleID][i]))
                {
                    victory = true;
                    break;
                }
            }
            if (msgContent.equals(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId())+"quit"))
            {
                break;
            }
        }

        if (victory)
        {
            embed.setDescription("Bingo, you got it! It took you "+(startingGuesses-guesses)+" guess(es)!");
        }
        else
        {
            embed.setDescription("Looks like you're just not as hip as me. You're all out of guesses.");
        }
        channel.sendMessage(embed.build()).queue();
        super.deleteGameChannel();
    }
}
