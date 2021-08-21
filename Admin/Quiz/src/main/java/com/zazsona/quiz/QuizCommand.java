package com.zazsona.quiz;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import com.zazsona.quiz.config.SettingsManager;
import com.zazsona.quiz.config.QuizBuilder;
import com.zazsona.quiz.quiz.Quiz;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidParameterException;

public class QuizCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            String operation = parameters[1].toLowerCase().trim();
            if (operation.equals("start"))
            {
                QuizBuilder quizBuilder = SettingsManager.getInstance().getGuildQuizBuilder(msgEvent.getGuild().getId()).clone(); //Clone so we can modify countdown time, etc, just for this instance.
                if (parameters.length > 2 && parameters[2].matches("[0-9]+"))
                {
                    int countdownSeconds = Integer.parseInt(parameters[2]);
                    if (countdownSeconds >= 10 && countdownSeconds <= 600)
                        quizBuilder.setJoinTimeSeconds(countdownSeconds);
                    else
                    {
                        msgEvent.getChannel().sendMessage("Invalid countdown!\nCountdown must be between 10-600 seconds.").queue();
                        return;
                    }
                }
                else
                    quizBuilder.setJoinTimeSeconds(30);
                Quiz quiz = quizBuilder.build();
                quiz.runQuiz();
            }
            else if (operation.equals("config") && SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).isPermitted(msgEvent.getMember(), "Config"))
            {
                QuizConfigWizard qcw = new QuizConfigWizard();
                qcw.run(msgEvent, SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()), msgEvent.getChannel(), false);
            }
        }
        catch (ArrayIndexOutOfBoundsException | InvalidParameterException e)
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
        catch (IOException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("An internal error occurred when saving settings.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            LoggerFactory.getLogger(getClass()).error(e.toString());
        }
    }
}
