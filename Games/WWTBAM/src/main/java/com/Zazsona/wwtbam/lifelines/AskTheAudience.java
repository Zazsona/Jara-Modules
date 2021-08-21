package com.zazsona.wwtbam.lifelines;

import com.zazsona.wwtbam.Trivia;
import com.zazsona.wwtbam.TriviaDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Random;

public class AskTheAudience
{
    public static void useAskTheAudience(TextChannel channel, TriviaDatabase tdb, Trivia trivia)
    {
        channel.sendMessage("Polling the non-existent audience...").queue();
        try {Thread.sleep(2500);} catch (InterruptedException e) {};
        EmbedBuilder embed = buildEmbed(trivia);
        channel.sendMessage(embed.build()).queue();
    }

    private static EmbedBuilder buildEmbed(Trivia trivia)
    {
        int correctAnswerIndex = trivia.getCorrectAnswerIndex();
        int answerCount = trivia.getAnswers().length;
        int[] percentages = calculatePercentages(answerCount, correctAnswerIndex);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<answerCount; i++)
        {
            stringBuilder.append("**Option ").append((i+1)).append("**: ").append(percentages[i]).append("%\n");
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(SystemColor.GRAY);
        embed.setDescription(stringBuilder.toString());
        embed.setTitle("Ask the Audience");
        return embed;
    }

    private static int[] calculatePercentages(int amount, int correctAnswerIndex)
    {
        Random r = new Random();
        int[] percentages = new int[amount];
        int total = 100;
        for (int i = 0; i<amount; i++)
        {
            percentages[i] = r.nextInt(25);
            total -= percentages[i];
        }
        percentages[correctAnswerIndex] += total;
        return percentages;
    }
}
