package com.Zazsona.Quiz;

import com.Zazsona.Quiz.quiz.Quiz;
import configuration.SettingsUtil;
import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidParameterException;

public class Start extends ModuleCommand
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
