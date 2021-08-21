package com.zazsona.welcomemessage;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WelcomeMessage extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle("==== Welcome Message ====");
        if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("config") && SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).isPermitted(msgEvent.getMember(), "Config"))
            {
                try
                {
                    WelcomeMessageConfig config = new WelcomeMessageConfig();
                    config.run(msgEvent, SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()), msgEvent.getChannel(), false);
                }
                catch (IOException e)
                {
                    LoggerFactory.getLogger(WelcomeMessageConfig.class).error(e.toString());
                    embed.setDescription("An error occurred when saving.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }

            }
        }
        else
        {
            String message = FileManager.getInstance().getWelcomeMessage(msgEvent.getGuild().getId());
            if (message == null)
            {
                message = "No welcome message has been set.";
            }
            embed.setDescription(message);
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }

    private String getInput(TextChannel channel, Member member)
    {
        MessageManager mm = new MessageManager();
        Message msg = null;
        while (msg == null || !msg.getMember().equals(member) || !msg.getTextChannel().equals(channel))
        {
            msg = mm.getNextMessage(channel);
            if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msg.getGuild().getId())+"quit") || msg.getContentDisplay().equalsIgnoreCase("quit"))
            {
                return null;
            }
        }
        return msg.getContentRaw();
    }

    private void toggleGuildState(GuildMessageReceivedEvent msgEvent, FileManager fm, boolean enable)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        if (enable != fm.isGuildEnabled(msgEvent.getGuild().getId()))
        {
            fm.modifyGuildState(msgEvent.getGuild().getId(), enable);
            embed.setDescription((enable) ? "Enabled welcome messages." : "Disabled welcome messages.");
        }
        else
        {
            embed.setDescription((enable) ? "Welcome messages are already enabled." : "Welcome messages are already disabled.");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();

    }
}
