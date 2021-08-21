package com.zazsona.lastcommand;

import com.zazsona.jara.ModuleAttributes;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.listeners.CommandListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LastCommandListener extends CommandListener
{
    private static LastCommandListener instance;
    private HashMap<String, HistoricCommand> userCommandMap;
    private ModuleAttributes lastCommandAttributes;

    public static LastCommandListener getInstance()
    {
        if (instance == null)
        {
            instance = new LastCommandListener();
        }
        return instance;
    }

    private LastCommandListener()
    {
        userCommandMap = new HashMap<>();
        Timer cleanTimer = new Timer();
        cleanTimer.scheduleAtFixedRate(new CleanUpTimerTask(), 1000*60*60, 1000*60*60);
    }

    public HistoricCommand getLastCommand(String userID)
    {
        return userCommandMap.get(userID);
    }

    public void setLastCommandAttributes(ModuleAttributes moduleAttributes)
    {
        this.lastCommandAttributes = moduleAttributes;
    }

    @Override
    public void onCommandSuccess(GuildMessageReceivedEvent msgEvent, ModuleAttributes moduleAttributes)
    {
        synchronized (instance)
        {
            if (!isLastCommandCall(msgEvent.getGuild(), msgEvent.getMessage().getContentRaw()))
            {
                userCommandMap.put(msgEvent.getMember().getUser().getId(), new HistoricCommand(msgEvent, moduleAttributes));
            }
        }
    }

    private boolean isLastCommandCall(Guild guild, String msgContent)
    {
        msgContent = msgContent.toLowerCase();
        String invocationTerm = msgContent.split(" ")[0].replace(""+ SettingsUtil.getGuildCommandPrefix(guild.getId()), "");
        for (String alias : lastCommandAttributes.getAliases())
        {
            if (alias.toLowerCase().equals(invocationTerm))
            {
                return true;
            }
        }
        return false;
    }

    private class CleanUpTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            synchronized (instance)
            {
                int timeToLive = 1000*60*60*3; //Three hours old
                ArrayList<String> usersToClear = new ArrayList<>();
                for (String userID : userCommandMap.keySet())
                {
                    if (userCommandMap.get(userID).getMsgEvent().getMessage().getTimeCreated().toEpochSecond()+timeToLive < Instant.now().getEpochSecond())
                    {
                        usersToClear.add(userID);
                    }
                }
                for (String userID : usersToClear)
                {
                    userCommandMap.remove(userID);
                }
            }
        }
    }
}
