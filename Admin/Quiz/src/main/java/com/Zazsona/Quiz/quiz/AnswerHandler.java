package com.Zazsona.Quiz.quiz;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class AnswerHandler extends ListenerAdapter
{
    private Category channelCategory;
    private ArrayList<QuizTeam> quizTeams;
    private int questionNo;
    private Trivia trivia;
    private HashSet<QuizTeam> teamsWithAnswerSet;
    private int startingTeamCount;

    public AnswerHandler(Category channelCategory, ArrayList<QuizTeam> quizTeams, Trivia trivia, int questionNo)
    {
        this.channelCategory = channelCategory;
        this.quizTeams = quizTeams;
        this.questionNo = questionNo;
        this.trivia = trivia;
        this.teamsWithAnswerSet = new HashSet<>();
        this.startingTeamCount = quizTeams.size();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        try
        {
            if (channelCategory.getTextChannels().contains(event.getChannel()))
            {
                synchronized (quizTeams)
                {
                    QuizTeam quizTeam = getTeamByChannel(event.getChannel().getId());
                    if (quizTeam != null)
                    {
                        String messageContent = event.getMessage().getContentDisplay();
                        if (trivia.isAnswerValid(messageContent))
                        {
                            quizTeam.getTeamChannel().sendMessage(Quiz.getQuizEmbedStyle(event.getGuild()).setAuthor(null).setDescription("Your Answer: **"+trivia.getAnswers()[trivia.getAnswerIndex(messageContent)]+"**").build()).queue();
                            teamsWithAnswerSet.add(quizTeam);
                            if (trivia.isAnswerCorrect(messageContent))
                                quizTeam.setAnswerResult(questionNo, true);
                            else
                                quizTeam.setAnswerResult(questionNo, false); //This handles if they change their answer from a correct one to a wrong one
                        }
                    }
                }
            }
        }
        catch (NoSuchElementException e)
        {
            //Should never fire, but do nothing as the answer is invalid
        }
    }

    private QuizTeam getTeamByChannel(String channelID)
    {
        for (QuizTeam quizTeam : quizTeams)
        {
            if (quizTeam.getTeamChannel().getId().equalsIgnoreCase(channelID))
                return quizTeam;
        }
        return null;
    }

    public boolean hasEveryoneAnswered()
    {
        return (startingTeamCount > 0 && teamsWithAnswerSet.size() == startingTeamCount); //0 check is so that, if no one joins during the countdown period, the quiz doesn't just speed through.
    }

}
