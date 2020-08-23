package com.Zazsona.GoogleTranslateQuiz;

import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class GoogleTranslateQuiz extends ModuleGameCommand
{

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        TextChannel channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Translation-Quiz");
        String[][] quotes = TranslatedQuotes.getQuotes();
        int quoteID = new Random().nextInt(quotes.length);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember())); //TODO: Add thumb
        embed.setDescription("You've got 3 tries to guess the franchise this wonkily translated quote is from!\n\n**"+quotes[quoteID][0]+"**");
        channel.sendMessage(embed.build()).queue();

        boolean win = takeAnswers(quotes[quoteID][1].toLowerCase(), channel);
        if (win)
        {
            embed.setDescription("Congratulations! You got it. :tada:");
        }
        else
        {
            embed.setDescription("Looks like you couldn't crack the dodgy translation. Better luck next time!");
        }
        channel.sendMessage(embed.build()).queue();
        deleteGameChannel();
    }

    private boolean takeAnswers(String answer, TextChannel channel)
    {
        MessageManager mm = new MessageManager();
        for (int i = 3; i>0; i--)
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getContentDisplay().toLowerCase().contains(answer))
            {
                return true;
            }
            else if (i > 0)
            {
                channel.sendMessage("Sorry, that's not it. **"+(i-1)+"** tries remaining.").queue();
            }
            else if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                return false;
            }
        }
        return false;
    }
}
