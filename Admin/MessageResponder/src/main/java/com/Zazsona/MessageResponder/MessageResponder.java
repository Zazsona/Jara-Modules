package com.Zazsona.MessageResponder;

import commands.CmdUtil;
import configuration.SettingsUtil;
import module.Load;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageResponder extends Load
{
    private static FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        MessageListener msgListener = new MessageListener();
        CmdUtil.getJDA().addEventListener(msgListener);
    }

    public class MessageListener extends ListenerAdapter
    {
        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event)
        {
            String guildID = event.getGuild().getId();
            if (fm.doesGuildHaveResponses(guildID))
            {
                String response = fm.getMessageResponses(guildID).get(event.getMessage().getContentRaw().toLowerCase());
                if (response != null && SettingsUtil.getGuildSettings(guildID).isCommandEnabled("MessageResponder"))
                {
                    event.getChannel().sendMessage(response).queue();
                }
            }
        }
    }


    public static FileManager getFileManager()
    {
        return fm;
    }
}
