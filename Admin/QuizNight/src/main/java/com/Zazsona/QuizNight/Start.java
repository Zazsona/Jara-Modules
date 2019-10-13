package com.Zazsona.QuizNight;

import com.Zazsona.QuizNight.quiz.Quiz;
import configuration.SettingsUtil;
import module.Command;
import commands.CmdUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.logging.Logger;

public class Start extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            String operation = parameters[1].toLowerCase().trim();
            if (operation.equals("start"))
            {
                Quiz qn = new Quiz();
                qn.startQuiz(msgEvent.getGuild(), true);
            }
            else if (operation.equals("config") && SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).isPermitted(msgEvent.getMember(),"Config"))
            {
                QuizConfigWizard qcw = new QuizConfigWizard();
                qcw.run(msgEvent, SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()), msgEvent.getChannel(), false);
            }
        }
        catch (ArrayIndexOutOfBoundsException | InvalidParameterException e)
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
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
