package com.Zazsona.wwtbam;

import com.Zazsona.wwtbam.lifelines.AskTheAudience;
import com.Zazsona.wwtbam.lifelines.AskTheHost;
import com.Zazsona.wwtbam.lifelines.FiftyFifty;
import com.Zazsona.wwtbam.lifelines.PhoneAFriend;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class GameDriver
{
    public static final String POUND_SIGN = "\u00A3";

    private TextChannel channel;
    private Member selfMember;
    private Member player;

    private MessageManager mm;
    private TriviaDatabase triviaDatabase;
    private boolean fiftyFifty;
    private boolean phoneAFriend;
    private boolean askTheAudience;
    private boolean askTheHost;
    private boolean netSet;
    private boolean netAchieved;
    private int netValue;
    private boolean gameOver;

    public GameDriver(TextChannel channel, Member selfMember, Member player)
    {
        mm = new MessageManager();
        triviaDatabase = new TriviaDatabase();
        this.channel = channel;
        this.selfMember = selfMember;
        this.player = player;
    }

    public void Start()
    {
        channel.sendMessage(getWelcomeEmbed()).queue();
        try {Thread.sleep(1500);} catch (InterruptedException e) {};
        runGameplayLoop();
    }

    private void runGameplayLoop()
    {
        for (int i = 1; i<16; i++)
        {
            Trivia trivia = triviaDatabase.getTrivia(i);
            EmbedBuilder triviaEmbed = trivia.getEmbed();
            triviaEmbed = recordLifelines(triviaEmbed);
            channel.sendMessage(triviaEmbed.build()).queue();
            boolean answeredQuestion = false;
            while (!answeredQuestion)
            {
                Message msg = mm.getNextMessage(channel, player);
                answeredQuestion = parseAnswer(msg.getContentDisplay(), trivia);
            }
            if (gameOver)
                return;
        }
        EmbedBuilder embed = getEmbedStyle();
        embed.setDescription("Congratulations on becoming a **millionaire!**\nBragging rights to be sure. Thanks for playing!");
        channel.sendMessage(embed.build()).queue();
    }

    private boolean parseAnswer(String answer, Trivia trivia)
    {
        if (!trivia.isAnswerValid(answer))
        {
            if (answer.equalsIgnoreCase("50/50") || answer.equalsIgnoreCase("fiftyfifty") || answer.equalsIgnoreCase("fifty/fifty") || answer.equalsIgnoreCase("50 50") || answer.equalsIgnoreCase("fifty fifty"))
            {
                fiftyFifty = true;
                FiftyFifty.useFiftyFifty(channel, triviaDatabase, trivia);
            }
            else if (answer.equalsIgnoreCase("ask the host") || answer.equalsIgnoreCase("host"))
            {
                askTheHost = true;
                AskTheHost.useAskTheHost(channel, triviaDatabase, trivia);
            }
            else if (answer.equalsIgnoreCase("ask the audience") || answer.equalsIgnoreCase("audience"))
            {
                askTheAudience = true;
                AskTheAudience.useAskTheAudience(channel, triviaDatabase, trivia);
            }
            else if (answer.equalsIgnoreCase("phone a friend") || answer.equalsIgnoreCase("phone") || answer.equalsIgnoreCase("friend"))
            {
                phoneAFriend = true;
                PhoneAFriend.usePhoneAFriend(channel, triviaDatabase, trivia);
            }
            else if (answer.equalsIgnoreCase("quit") || answer.equalsIgnoreCase("walk") || answer.equalsIgnoreCase("leave") || answer.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                endGame(trivia, true);
            }
            return false;
        }
        else
        {
            channel.sendMessage("Hmm...").queue();
            try {Thread.sleep(1000);} catch (InterruptedException e) {};
            boolean correct = trivia.isAnswerCorrect(answer);
            if (correct)
                advanceGameState(trivia);
            else
                endGame(trivia, false);
            return true;
        }
    }

    private boolean parseYesNoAnswer(String answer)
    {
        return (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"));
    }

    private void advanceGameState(Trivia trivia)
    {
        channel.sendMessage("That's the correct answer! You've just won **"+POUND_SIGN+trivia.getQuestionValue()+"!**").queue();
        if (trivia.getQuestionValue() == 1000)
            netValue = 1000;

        if (netSet && !netAchieved)
        {
            netAchieved = true;
            netValue = trivia.getQuestionValue();
        }
        else if (!netSet && netValue >= 1000)
        {
            channel.sendMessage("\nNow, would you like to set your safety net at "+POUND_SIGN+(trivia.getQuestionValue()*2)+"? (Yes/No)").queue();
            Message message = mm.getNextMessage(channel, player);
            netSet = parseYesNoAnswer(message.getContentDisplay());
            if (netSet)
                channel.sendMessage("Alright then, let's get you there. Computer, what's the next question?").queue();
        }
    }

    private void endGame(Trivia trivia, boolean voluntary)
    {
        int loss = (trivia.getQuestionValue()/2)-netValue;
        if (voluntary)
            channel.sendMessage("Not going to gamble for it? Boo-ring! Well, you played well, and get to go home with "+POUND_SIGN+trivia.getQuestionValue()/2+"!").queue(); //TODO: Sub-1000 is not doubled
        else if (netValue > 0 && loss > 0)
            channel.sendMessage("I'm sorry, you've just *lost* "+POUND_SIGN+loss+". But you've still got your "+POUND_SIGN+netValue+" from your safety net. So it's not all bad! Thanks for playing.").queue();
        else if (netValue > 0 && loss == 0)
            channel.sendMessage("Sorry! That's the wrong answer! However, due to your safety net nothing's lost, and you're still going home with "+POUND_SIGN+netValue+"!").queue();
        else if (netValue == 0)
            channel.sendMessage("Sorry, that's the wrong answer. You've done a bit naff haven't you? I'm afraid you'll be going home empty handed. Better luck next time!").queue();
        gameOver = true;
    }

    private EmbedBuilder recordLifelines(EmbedBuilder embed)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Lifelines: ");
        if (!fiftyFifty)
            stringBuilder.append("50/50, ");
        if (!phoneAFriend)
            stringBuilder.append("Phone a Friend, ");
        if (!askTheAudience)
            stringBuilder.append("Ask the Audience, ");
        if (!askTheHost)
            stringBuilder.append("Ask the Host, ");
        stringBuilder.setLength(stringBuilder.length()-2);
        embed.setFooter(stringBuilder.toString());
        return embed;
    }

    private MessageEmbed getWelcomeEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Who wants to be a Millionaire?");
        embed.setDescription("Welcome to Who wants to be a Millionaire? I'm your host "+selfMember.getEffectiveName()+", and today "+player.getEffectiveName()+" is here hoping to win "+POUND_SIGN+"1,000,000!\n\nYou know the game, get the questions correct to increase your winnings. Get a question wrong and you lose everything after your safety net. There are four lifelines you can use at any point should you get stuck, or you can safely leave at any time and keep the money.\n\n*With that said, let's play Who wants to be a Millionaire?*");
        embed.setColor(Color.GRAY);
        return embed.build();
    }

    private EmbedBuilder getEmbedStyle()
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Who wants to be a Millionaire?");
        embed.setColor(Color.GRAY);
        return embed;
    }
}
