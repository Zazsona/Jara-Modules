package quiz;

import json.TriviaJson;
import commands.CmdUtil;
import configuration.SettingsUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.text.StringEscapeUtils;

import java.time.Instant;
import java.util.HashMap;

public class AnswerHandler
{
    private HashMap<String, QuizTeam> quizTeams;
    private HashMap<TextChannel, Boolean> teamsWithAnswers;
    private Category quizCategory;
    private TextChannel questionChannel;

    private TriviaJson.TriviaQuestion triviaQuestion;
    private int questionNo;

    public AnswerHandler(Category quizCategory, TextChannel questionChannel, HashMap<String, QuizTeam> quizTeams)
    {
        this.quizCategory = quizCategory;
        this.questionChannel = questionChannel;
        this.quizTeams = quizTeams;
    }

    /**
     * Gets the answers, and sets the result of them to the quiz teams associated with this object.
     * @param question the question
     * @param questionNo the question number
     * @param secondsToAnswer the seconds players have to answer
     * @return whether the clock ended early (because all players answered)
     */
    public boolean getAnswers(TriviaJson.TriviaQuestion question, int questionNo, int secondsToAnswer)
    {
        this.triviaQuestion = question;
        this.questionNo = questionNo;
        teamsWithAnswers = new HashMap<>();
        for (QuizTeam quizTeam : quizTeams.values())
        {
            if (quizTeam.hasMembers())
                teamsWithAnswers.put(quizTeam.getTeamChannel(), false);
        }

        MessageReceiver mr = new MessageReceiver();
        Thread listenerThread = new Thread(() -> CmdUtil.getJDA().addEventListener(mr));
        listenerThread.start();
        boolean earlyExit = runClock(secondsToAnswer);
        CmdUtil.getJDA().removeEventListener(mr);
        return earlyExit;
    }

    /**
     * Gets whether the provided answer is valid for the question
     * @param answer the user's answer
     * @param guild the quiz guild
     * @param tq the question
     * @return true/false answer is valid
     */
    private boolean validateAnswer(String answer, Guild guild, TriviaJson.TriviaQuestion tq)
    {
        if (answer.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(guild.getId())+"quit"))
        {
            return true;
        }
        if (answer.matches("option [1-4]") && tq.type.equals("multiple"))
        {
            return true;
        }
        if (answer.matches("option [1-2]") && tq.type.equals("boolean"))
        {
            return true;
        }
        if (answer.equals(StringEscapeUtils.unescapeHtml4(tq.correct_answer).toLowerCase()))
        {
            return true;
        }
        for (String incorrectAnswer : tq.incorrect_answers)
        {
            if (answer.equals(StringEscapeUtils.unescapeHtml4(incorrectAnswer).toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the quiz team by their channel
     * @param channel the channel
     * @return the quiz team
     */
    private QuizTeam getQuizTeamByChannel(TextChannel channel)
    {
        for (QuizTeam quizTeam : quizTeams.values())
        {
            if (quizTeam.getTeamChannel().equals(channel))
            {
                return quizTeam;
            }
        }
        return null;
    }

    /**
     * Takes messages and, if they are in a valid quiz channel, applies whether the team got it with in their {@link QuizTeam}
     */
    private class MessageReceiver extends ListenerAdapter
    {
        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent msgEvent)
        {
            Message message = msgEvent.getMessage();
            if (message.getTextChannel().getParent().equals(quizCategory))
            {
                String messageContent = message.getContentDisplay().toLowerCase();
                if (validateAnswer(messageContent, message.getGuild(), triviaQuestion))
                {
                    if (messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(quizCategory.getGuild().getId())+"quit"))
                    {
                        message.getTextChannel().putPermissionOverride(message.getMember()).setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                        questionChannel.putPermissionOverride(message.getMember()).setDeny(Permission.MESSAGE_READ).queue();
                        QuizTeam qt = getQuizTeamByChannel(message.getTextChannel());
                        qt.removeTeamMember(message.getMember());
                        if (!qt.hasMembers())
                        {
                            teamsWithAnswers.put(message.getTextChannel(), true);
                        }
                        return;
                    }
                    teamsWithAnswers.put(message.getTextChannel(), true);
                    message.getTextChannel().sendMessage("Answer noted!").queue();
                    if (messageContent.equalsIgnoreCase(StringEscapeUtils.unescapeHtml4(triviaQuestion.correct_answer)) || messageContent.equalsIgnoreCase("option "+triviaQuestion.correct_answer_id))
                    {
                        getQuizTeamByChannel(message.getTextChannel()).setAnswerResult(questionNo, true);
                    }
                    else if (teamsWithAnswers.get(message.getTextChannel())) //We do a check here as we only need to set answer result back to false if it's potentially already been set to true. This saves us a search if they're just entering their first answer.
                    {
                        getQuizTeamByChannel(message.getTextChannel()).setAnswerResult(questionNo, false);
                    }
                }
            }
        }
    }

    /**
     * Runs a clock for the provided seconds.
     * @param seconds the seconds to run for
     * @return true/false on if an early exit happened, due to all teams having answered.
     */
    private boolean runClock(int seconds)
    {
        long startTime = Instant.now().getEpochSecond();
        try
        {
            while (Instant.now().getEpochSecond() < startTime+seconds)
            {
                if (!teamsWithAnswers.values().contains(false))
                {
                    throw new InterruptedException("All teams have answered.");
                }
                Thread.sleep(1000);
            }
            return false;
        }
        catch (InterruptedException e)
        {
            return true;
        }

    }
}
