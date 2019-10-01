package com.Zazsona.QuizNightStats;

import com.Zazsona.QuizNightStats.json.UserStats;
import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class QuizNightStats extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Member member = msgEvent.getMember();
        if (parameters.length > 1)
        {
            List<Member> members = msgEvent.getGuild().getMembersByEffectiveName(parameters[1], true);
            if (members.size() > 0)
            {
                member = members.get(0);
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));

        HashMap<String, UserStats> statsMap = UserStatManager.restore();
        if (statsMap != null)
        {
            UserStats stats = statsMap.get(member.getUser().getId());
            if (stats != null)
            {
                embed.addField("============ Quiz Stats =============", member.getEffectiveName()+"'s Stats", false);
                embed.addField("Quizzes", String.valueOf(stats.getQuizNightTotal()), true);
                embed.addField("Wins", String.valueOf(stats.getWins()), true);
                embed.addField("Win %", String.format("%,.2f", (double) (stats.getWins()*100.0f/stats.getQuizNightTotal()))+"%", true);

                int questionTotal = (stats.getEasyQuestionsTotal()+stats.getMediumQuestionsTotal()+stats.getHardQuestionsTotal());
                int questionsCorrect = (stats.getEasyQuestionsCorrect()+stats.getMediumQuestionsCorrect()+stats.getHardQuestionsCorrect());
                embed.addField("Questions", String.valueOf(questionTotal), true);
                embed.addField("Correct", String.valueOf(questionsCorrect), true);
                embed.addField("Correct %", String.format("%,.2f", (double) (questionsCorrect*100.0f/questionTotal))+"%", true);

                embed.addField("=========== Question Stats ===========", "", false);
                embed.addField("Easy Questions", String.valueOf(stats.getEasyQuestionsTotal()), true);
                embed.addField("Correct", String.valueOf(stats.getEasyQuestionsCorrect()), true);
                embed.addField("Correct %", String.format("%,.2f", (double) (stats.getEasyQuestionsCorrect()*100.0f/stats.getEasyQuestionsTotal()))+"%", true);

                embed.addField("Medium Questions", String.valueOf(stats.getMediumQuestionsTotal()), true);
                embed.addField("Correct", String.valueOf(stats.getMediumQuestionsCorrect()), true);
                embed.addField("Correct %", String.format("%,.2f", (double) (stats.getMediumQuestionsCorrect()*100.0f/stats.getMediumQuestionsTotal()))+"%", true);

                embed.addField("Hard Questions", String.valueOf(stats.getHardQuestionsTotal()), true);
                embed.addField("Correct", String.valueOf(stats.getHardQuestionsCorrect()), true);
                embed.addField("Correct %", String.format("%,.2f", (double) (stats.getHardQuestionsCorrect()*100.0f/stats.getHardQuestionsTotal()))+"%", true);
            }
            else
            {
                embed.setDescription("There aren't any quizzes to know about!");
            }
        }
        else
        {
            embed.setDescription("An error occurred.");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
