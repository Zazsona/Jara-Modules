package com.Zazsona.Quiz.quiz;

import com.Zazsona.Quiz.config.QuizBuilder;
import com.Zazsona.Quiz.stats.UserStatManager;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.Core;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Quiz
{
    private boolean isQuizStarted;
    private Trivia[] trivia;
    private JoinHandler joinHandler;
    private Category quizCategory;
    private ArrayList<QuizTeam> quizTeams;
    private TextChannel globalQuizChannel;
    private QuizBuilder quizSettings;

    public Quiz(Guild guild, Trivia[] trivia, QuizBuilder quizBuilder)
    {
        this.quizSettings = quizBuilder;
        this.trivia = trivia;
        this.quizTeams = new ArrayList<>();
        this.isQuizStarted = false;
        this.quizCategory = guild.createCategory("Quiz").complete();
        this.joinHandler = new JoinHandler(this, guild, quizCategory, quizTeams, quizBuilder.getRolesPermittedToJoin());
    }

    public void runQuiz()
    {
        Core.getShardManagerNotNull().addEventListener(joinHandler);
        runCountdown();
        for (int questionNo = 0; questionNo<trivia.length; questionNo++)
        {
            postQuestion(questionNo);
            getAnswers(trivia[questionNo], questionNo);
        }
        Core.getShardManagerNotNull().removeEventListener(joinHandler);
        if (quizTeams.size() > 0)
        {
            ArrayList<QuizTeam> leaderboard = getLeaderboard();
            postResults(leaderboard);
            recordStats(leaderboard.get(0));
        }
        dispose();
    }

    private void runCountdown()
    {
        Guild guild = quizCategory.getGuild();
        int countdownTime = quizSettings.getJoinTimeSeconds();
        if (countdownTime >= 60)
        {
            int minutes = Math.round(countdownTime/60);
            String alertMessage = "Quiz will start in "+minutes+" minutes!\nUse "+ SettingsUtil.getGuildCommandPrefix(guild.getId())+"join to play.";
            guild.getDefaultChannel().sendMessage(getQuizEmbedStyle(guild).setDescription(alertMessage).build()).queue();
            if (quizSettings.isPingOnCountdown())
                guild.getDefaultChannel().sendMessage(guild.getPublicRole().getAsMention()).queue();
        }
        while (countdownTime > 0)
        {
            try
            {
                if (countdownTime == 30)
                    guild.getDefaultChannel().sendMessage(getQuizEmbedStyle(guild).setDescription("Quiz will start in "+countdownTime+" seconds!\nUse "+ SettingsUtil.getGuildCommandPrefix(guild.getId())+"join to play.").build()).queue();
                Thread.sleep(1000);
                countdownTime--;
            }
            catch (InterruptedException e)
            {
                countdownTime--;
            }
        }
        guild.getDefaultChannel().sendMessage(getQuizEmbedStyle(guild).setDescription("The quiz has now begun!").build()).queue();
        isQuizStarted = true;
    }

    private void postQuestion(int questionNo)
    {
        synchronized (quizTeams)
        {
            if (quizTeams.size() > 6 && globalQuizChannel == null)
                createGlobalChannel((questionNo > 0));

            if (globalQuizChannel != null)
                globalQuizChannel.sendMessage(trivia[questionNo].getEmbed(questionNo)).queue();
            else
            {
                MessageEmbed embed = trivia[questionNo].getEmbed(questionNo);
                for (QuizTeam quizTeam : quizTeams)
                {
                    quizTeam.getTeamChannel().sendMessage(embed).queue();
                }
            }
        }
    }

    private void postResults(ArrayList<QuizTeam> leaderboard)
    {
        StringBuilder questionsListBuilder = new StringBuilder();
        for (int questionNo = 0; questionNo<trivia.length; questionNo++)
        {
            questionsListBuilder.append("Question ").append((questionNo+1)).append(": \n");
        }
        int leaderboardPositionsToDisplay = (leaderboard.size() > 3) ? 3 : leaderboard.size();
        StringBuilder leaderboardListBuilder = new StringBuilder();
        for (int position = 0; position<leaderboardPositionsToDisplay; position++)
        {
            if (position == 0)
                leaderboardListBuilder.append("1st. ").append(leaderboard.get(0).getTeamName()).append(" - ").append(leaderboard.get(0).getPoints()).append("pts \n");
            if (position == 1)
                leaderboardListBuilder.append("2nd. ").append(leaderboard.get(1).getTeamName()).append(" - ").append(leaderboard.get(1).getPoints()).append("pts \n");
            if (position == 2)
                leaderboardListBuilder.append("3rd. ").append(leaderboard.get(2).getTeamName()).append(" - ").append(leaderboard.get(2).getPoints()).append("pts \n");
        }
        for (QuizTeam quizTeam : quizTeams)
        {
            StringBuilder answersListBuilder = new StringBuilder();
            for (int questionNo = 0; questionNo<trivia.length; questionNo++)
            {
                answersListBuilder.append((quizTeam.isCorrectlyAnswered(questionNo) ? "O" : "X")).append("\n");
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("=== Results ===");
            embed.setThumbnail("https://i.imgur.com/FSLXDTj.png");
            embed.setColor(CmdUtil.getHighlightColour(quizCategory.getGuild().getSelfMember()));
            embed.addField("Questions", questionsListBuilder.toString()+("==============\n")+("Total Points: ")+(quizTeam.getPoints()), true);
            embed.addField("Answers", answersListBuilder.toString(), true);
            embed.getDescriptionBuilder().append(leaderboardListBuilder).append("\nYour Position: ").append((leaderboard.indexOf(quizTeam)+1));
            quizTeam.getTeamChannel().sendMessage(embed.build()).queue();
        }
    }

    private void getAnswers(Trivia trivia, int questionNo)
    {
        AnswerHandler answerHandler = new AnswerHandler(quizCategory, quizTeams, trivia, questionNo);
        Core.getShardManagerNotNull().addEventListener(answerHandler);
        int answerTime = 60;
        while (answerTime > 0)
        {
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            answerTime--;
            if (answerHandler.hasEveryoneAnswered())
                break;
        }
        Core.getShardManagerNotNull().removeEventListener(answerHandler);
    }

    private ArrayList<QuizTeam> getLeaderboard()
    {
        for (int i = 0; i<trivia.length; i++)
        {
            for (QuizTeam quizTeam : quizTeams)
            {
                if (quizTeam.isCorrectlyAnswered(i))
                    quizTeam.addPoints(trivia[i].getPoints());
            }
        }
        ArrayList<QuizTeam> leaderboard = (ArrayList<QuizTeam>) quizTeams.clone();
        leaderboard.sort(Comparator.comparingInt(QuizTeam::getPoints).reversed());
        return leaderboard;
    }

    private void recordStats(QuizTeam winningTeam)
    {
        UserStatManager.saveQuizStats(winningTeam, trivia, quizTeams);
    }

    private void dispose()
    {
        try
        {
            Thread.sleep(1000*60*10);
        }
        catch (InterruptedException e)
        {
            //Do nothing
        }
        finally
        {
            for (TextChannel channel : quizCategory.getTextChannels())
            {
                channel.delete().queue();
            }
            quizCategory.delete().queue();
        }
    }

    private void createGlobalChannel(boolean midGame)
    {
        globalQuizChannel = quizCategory.createTextChannel("Questions").complete();
        globalQuizChannel.putPermissionOverride(quizCategory.getGuild().getPublicRole()).setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
        synchronized (quizTeams)
        {
            for (QuizTeam quizTeam : quizTeams)
            {
                List<Member> memberList = quizTeam.getTeamChannel().getMembers(); //We're getting channel members in case team members have quit. They don't want to be added if this occurs mid-game.
                for (Member member : memberList)
                {
                    globalQuizChannel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).queue();
                }
            }
        }
        if (midGame)
            globalQuizChannel.sendMessage("Due to a high number of teams, questions will now be posted here.").queue();
    }

    public boolean isStarted()
    {
        return isQuizStarted;
    }

    public int getQuestionCount()
    {
        return trivia.length;
    }

    public static EmbedBuilder getQuizEmbedStyle(Guild guild)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Quiz");
        embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
        return embed;
    }

    @Nullable
    public TextChannel getGlobalQuizChannel()
    {
        return globalQuizChannel;
    }



}
