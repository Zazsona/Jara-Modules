package com.Zazsona.QuizNight.quiz;

import com.Zazsona.QuizNight.json.QuizSettings;
import com.Zazsona.QuizNight.json.TriviaJson;
import com.Zazsona.QuizNight.system.SettingsManager;
import com.Zazsona.QuizNight.system.UserStatManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import commands.CmdUtil;
import configuration.SettingsUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.text.StringEscapeUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Quiz
{
    private HashMap<String, QuizTeam> quizTeams;
    private Category quizCategory;
    private TextChannel questionsChannel;

    public void startQuiz(Guild guild, boolean quickStart)
    {
        try
        {
            quizTeams = new HashMap<>();
            Gson gson = new Gson();
            QuizSettings.GuildQuizConfig gqc = SettingsManager.getGuildQuizSettings(guild.getIdLong());
            String json = CmdUtil.sendHTTPRequest("https://opentdb.com/api.php?amount=10");
            TriviaJson tj = gson.fromJson(json, TriviaJson.class);
            initialiseChannels(guild);
            JoinHandler joinHandler = new JoinHandler(quizCategory, questionsChannel, quizTeams);
            Thread joinHandlerThread = new Thread(() -> joinHandler.acceptJoins(guild));
            joinHandlerThread.start();

            Message quizNoticeMessage = null;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(quizCategory.getGuild().getSelfMember()));
            embed.setTitle("=== Quiz ===");
            if (!quickStart)
            {
                embed.getDescriptionBuilder().append("Quiz will start in 5 minutes. Join with "+ SettingsUtil.getGuildCommandPrefix(guild.getId())+"join (Team name)!");
                if (gqc.PingQuizAnnouncement)
                {
                    quizNoticeMessage = guild.getDefaultChannel().sendMessage(guild.getPublicRole().getAsMention()).embed(embed.build()).complete();
                }
                else
                {
                    quizNoticeMessage = guild.getDefaultChannel().sendMessage(embed.build()).complete();
                }
                Thread.sleep((5*60*1000)-30*1000); //Minus 30 for the 30 second notice.
            }
            embed.setDescription("**Quiz will start in 30 seconds!**\nJoin with "+ SettingsUtil.getGuildCommandPrefix(guild.getId())+"join (Team name)!");
            quizNoticeMessage = (quizNoticeMessage == null) ? guild.getDefaultChannel().sendMessage(embed.build()).complete() : quizNoticeMessage.editMessage(embed.build()).complete();
            Thread.sleep(30*1000);
            joinHandler.stopAcceptingJoins();
            joinHandlerThread.interrupt();
            embed.setDescription("**The quiz has now begun!**\nGood luck!");
            quizNoticeMessage.getChannel().sendMessage(embed.build()).queue();

            boolean sendQuestionsToAllChannels = quizTeams.size() <= 5;
            if (quizTeams.size() > 0)
            {
                AnswerHandler answerHandler = new AnswerHandler(quizCategory, questionsChannel, quizTeams, tj.results);
                for (int i = 0; i<tj.results.length; i++)
                {
                    sendQuestion(tj.results[i], i+1, sendQuestionsToAllChannels);
                    boolean earlyAnswers = answerHandler.getAnswers(i, 90);
                    if (earlyAnswers)
                    {
                        questionsChannel.sendMessage("Looks like everyone's got an answer already! So, moving on...").queue();
                    }
                }
            }
            end(tj.results);

        }
        catch (JsonSyntaxException e)
        {
            //The provided question collection is dodgy. Try again.
            startQuiz(guild, quickStart);
        }
        catch (InterruptedException e)
        {
            guild.getDefaultChannel().sendMessage("An error occurred during the countdown. Restarting...").queue();
            startQuiz(guild, quickStart);
        }
    }

    /*
    Category toggle system. Due to API limitations, this would require *multiple* calls. Roughly the same number as there is questions, up to 31. This many API calls is unfair and slooow.
     One possible solution would be to gather 50 questions (the maximum) and only select the first X (question count) with supported categories.
     If there are insufficient supported questions, and there is at least one allowed category, make continuous API calls, and add questions with matching categories *if* they are not already queued.

     If only one category is available, we can make a special exception for it.
     */
    /*private String getRestQuery(QuizSettings.GuildQuizConfig gqc, int questionCount)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("https://opentdb.com/api.php?amount=").append(questionCount);
        for (int i = 0; i<X; i++)
        {
            if (!gqc.BannedCategories.contains(i))
            {
                sb.append(i).append("-");
            }
        }
        return sb.toString();
    }*/

    private void initialiseChannels(Guild guild)
    {
        quizCategory = (Category) guild.getController().createCategory("quiz").complete();
        quizCategory.createPermissionOverride(guild.getPublicRole()).setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
        questionsChannel = (TextChannel) quizCategory.createTextChannel("Questions").complete();

        EmbedBuilder introEmbed = new EmbedBuilder();
        introEmbed.setColor(CmdUtil.getHighlightColour(quizCategory.getGuild().getSelfMember()));
        introEmbed.setTitle("=== Quiz ===");
        introEmbed.setThumbnail("https://i.imgur.com/FSLXDTj.png");
        introEmbed.setDescription("**Welcome to the Quiz!**\n\nQuestions will appear here, and you can answer them in your team channel. You can enter your answer as either what is listed, or as \"Option X\". Good luck!");
        questionsChannel.sendMessage(introEmbed.build()).queue();
    }

    private void sendQuestion(TriviaJson.TriviaQuestion tq, int questionNo, boolean sendQuestionsToAllChannels)
    {
        MessageEmbed embed = buildEmbed(tq, questionNo).build();
        questionsChannel.sendMessage(embed).queue();
        if (sendQuestionsToAllChannels)
        {
            for (QuizTeam team : quizTeams.values())
            {
                team.getTeamChannel().sendMessage(embed).queue();
            }
        }
    }

    private EmbedBuilder buildEmbed(TriviaJson.TriviaQuestion tq, int questionNo)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Question "+questionNo+" - "+tq.category);
        embed.setDescription(StringEscapeUtils.unescapeHtml4(tq.question));
        switch (tq.difficulty)
        {
            case "easy":
                embed.setTitle("Easy");
                embed.setThumbnail("https://i.imgur.com/M0axget.png");
                embed.setColor(Color.decode("#38BC23"));
                break;
            case "medium":
                embed.setTitle("Medium");
                embed.setThumbnail("https://i.imgur.com/IlYb9PC.png");
                embed.setColor(Color.decode("#247AAF"));
                break;
            case "hard":
                embed.setTitle("Hard");
                embed.setThumbnail("https://i.imgur.com/0hdxuiX.png");
                embed.setColor(Color.decode("#FF2626"));
                break;
        }
        ArrayList<String> answers = new ArrayList<>();
        answers.add(StringEscapeUtils.unescapeHtml4(tq.correct_answer));
        for (String incorrectanswer : tq.incorrect_answers)
        {
            answers.add(StringEscapeUtils.unescapeHtml4(incorrectanswer));
        }
        Collections.shuffle(answers);
        for (int i = 0; i<answers.size(); i++)
        {
            embed.addField("Option "+(i+1), answers.get(i), true);
            if (answers.get(i).equals(tq.correct_answer))
            {
                tq.correct_answer_id = (i+1);    //This allows the user to also select an answer by inputting "Option X"
            }
        }
        return embed;
    }

    private void end(TriviaJson.TriviaQuestion[] questions)
    {
        try
        {
            if (quizTeams.size() > 0)
            {
                for (QuizTeam qt : quizTeams.values())
                {
                    for (int i = 0; i<questions.length; i++)
                    {
                        if (qt.isCorrect(i))                          //Tally up points
                        {
                            qt.addPoints(questions[i].getPoints());
                        }
                    }
                }

                QuizTeam winningTeam = sendLeaderboard();
                UserStatManager.saveQuizNightStats(winningTeam.getTeamName(), questions, quizTeams.values());
                if (quizTeams.size() <= 20)
                {
                    for (QuizTeam qt : quizTeams.values())
                    {
                        sendResults(qt, questions);
                        qt.getTeamChannel().putPermissionOverride(winningTeam.getTeamChannel().getGuild().getPublicRole()).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                    }
                }
                Thread.sleep(10*60*1000);
            }
        }
        catch (InterruptedException e)
        {
            //Not ideal, but there's not much we can do here.
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

    private QuizTeam sendLeaderboard()
    {
        ArrayList<QuizTeam> leaderboard = new ArrayList<>();
        leaderboard.addAll(quizTeams.values());
        leaderboard.sort(Comparator.comparingInt(QuizTeam::getPoints).reversed());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(CmdUtil.getHighlightColour(quizCategory.getGuild().getSelfMember()));
        embedBuilder.setAuthor("=== Leaderboard ===");
        embedBuilder.setThumbnail("https://i.imgur.com/FSLXDTj.png");
        if (quizTeams.size() > 0)
        {
            StringBuilder descBuilder = new StringBuilder();
            for (int i = 0; i<leaderboard.size(); i++)
            {
                descBuilder.append((i+1));
                if ((i+1) % 10 == 1)
                {
                    descBuilder.append("st. ");
                }
                else if ((i+1) % 10 == 2)
                {
                    descBuilder.append("nd. ");
                }
                else if ((i+1) % 10 == 3)
                {
                    descBuilder.append("rd. ");
                }
                else
                {
                    descBuilder.append("th. ");
                }
                descBuilder.append(leaderboard.get(i).getTeamName()).append(" - ").append(leaderboard.get(i).getPoints()).append("pts.");
                descBuilder.append("\n");
            }
            embedBuilder.setDescription(descBuilder.toString());
            questionsChannel.sendMessage(embedBuilder.build()).queue();
            return leaderboard.get(0);
        }
        else
        {
            embedBuilder.setDescription("There were no players.");
            questionsChannel.sendMessage(embedBuilder.build()).queue();
            return null;
        }

    }

    private void sendResults(QuizTeam qt, TriviaJson.TriviaQuestion[] questions)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("=== Results ===");
        embed.setThumbnail("https://i.imgur.com/FSLXDTj.png");
        embed.setColor(CmdUtil.getHighlightColour(quizCategory.getGuild().getSelfMember()));
        StringBuilder descBuilder = new StringBuilder();
        for (int i = 0; i<questions.length; i++)
        {
            descBuilder.append("**Question ").append((i+1)).append("** (").append(questions[i].getPoints()).append("pts)\n");
        }
        descBuilder.append("==============\n").append("Total Points - ").append(qt.getPoints());
        embed.addField("Question", descBuilder.toString(), true);
        StringBuilder answerBuilder = new StringBuilder();
        for (int i = 0; i<questions.length; i++)
        {
            if (qt.isCorrect(i))
            {
                answerBuilder.append("O\n");
            }
            else
            {
                answerBuilder.append("X\n");
            }
        }
        embed.addField("Results", answerBuilder.toString(), true);
        qt.getTeamChannel().sendMessage(embed.build()).queue();
    }
}
