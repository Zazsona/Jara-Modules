package com.Zazsona.MessageResponder;

import configuration.SettingsUtil;
import jara.Core;
import module.ModuleLoad;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageResponder extends ModuleLoad
{
    private static FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        fm.restore();
        MessageListener msgListener = new MessageListener();
        Core.getShardManagerNotNull().addEventListener(msgListener);
    }

    public class MessageListener extends ListenerAdapter
    {
        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event)
        {
            String guildID = event.getGuild().getId();
            if (fm.doesGuildHaveResponses(guildID) && !event.getMessage().getAuthor().isBot())
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
