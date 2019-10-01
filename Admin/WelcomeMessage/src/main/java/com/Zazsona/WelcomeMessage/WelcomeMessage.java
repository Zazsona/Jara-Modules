package com.Zazsona.WelcomeMessage;

import commands.CmdUtil;
import jara.MessageManager;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class WelcomeMessage extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("enable"))
            {
                FileManager fm = new FileManager();
                toggleGuildState(msgEvent, fm, true);
            }
            else if (parameters[1].equalsIgnoreCase("disable"))
            {
                FileManager fm = new FileManager();
                toggleGuildState(msgEvent, fm, false);
            }
        }
        else if (parameters.length == 1)
        {
            FileManager fm = new FileManager();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("**Please enter a new welcome message.**\n\nExisting Message:\n"+fm.getWelcomeMessage(msgEvent.getGuild().getId()));
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            String message = getInput(msgEvent.getChannel(), msgEvent.getMember());
            fm.setWelcomeMessage(msgEvent.getGuild().getId(), message);
            embed.setDescription("**Welcome message set.**\n\n"+message);
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            if (!fm.isGuildEnabled(msgEvent.getGuild().getId()))
            {
                embed.setDescription("Would you like to enable the Welcome Messages? [Y/N]");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                String response = getInput(msgEvent.getChannel(), msgEvent.getMember());
                if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yeah"))
                {
                    toggleGuildState(msgEvent, fm, true);
                }
                else
                {
                    toggleGuildState(msgEvent, fm, false);
                }
            }

        }

    }

    private String getInput(TextChannel channel, Member member)
    {
        MessageManager mm = new MessageManager();
        Message msg = null;
        while (msg == null || !msg.getMember().equals(member) || !msg.getTextChannel().equals(channel))
        {
            msg = mm.getNextMessage(channel);
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
