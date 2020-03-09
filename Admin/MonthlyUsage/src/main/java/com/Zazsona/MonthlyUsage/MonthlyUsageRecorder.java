package com.Zazsona.MonthlyUsage;

import configuration.SettingsUtil;
import jara.Core;
import jara.ModuleAttributes;
import listeners.CommandListener;
import listeners.ListenerManager;
import module.ModuleLoad;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class MonthlyUsageRecorder extends ModuleLoad
{
    private static FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        fm.restore();
        GlobalCommandListener cmdListener = new GlobalCommandListener();
        ListenerManager.registerListener(cmdListener);


        Timer autosaveScheduler = new Timer();
        TimerTask tt = new TimerTask()
        {
            @Override
            public void run()
            {
                fm.save();
            }
        };
        autosaveScheduler.schedule(tt, 1000*60, 1000*60);

        scheduleNextBroadcast();
    }

    private void scheduleNextBroadcast()
    {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Timer broadcastScheduler = new Timer();
        Calendar broadcastTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        if (fm.getLastReset().getMonthValue() == utc.getMonthValue() && fm.getLastReset().getYear() == utc.getYear())
        {
            broadcastTime.set(Calendar.MONTH, utc.getMonthValue());
        }
        else
        {
            broadcastTime.set(Calendar.MONTH, utc.getMonthValue()-1);
        }
        broadcastTime.set(Calendar.DAY_OF_MONTH, 1);
        broadcastTime.set(Calendar.HOUR_OF_DAY, 0);
        broadcastTime.set(Calendar.MINUTE, 0);
        broadcastTime.set(Calendar.SECOND, 0);
        TimerTask broadcastTask = new TimerTask()
        {
            @Override
            public void run()
            {
                broadcast();
            }
        };
        broadcastScheduler.schedule(broadcastTask, broadcastTime.getTime());
    }

    public class GlobalCommandListener extends CommandListener
    {
        @Override
        public void onCommandSuccess(GuildMessageReceivedEvent msgEvent, ModuleAttributes moduleAttributes)
        {
            fm.addUsage(msgEvent.getGuild().getId(), msgEvent.getAuthor().getId());
        }
    }

    private void broadcast()
    {
        MonthlyUsage mu = new MonthlyUsage();
        HashMap<String, HashMap<String, Integer>> commandUsage = fm.getCommandUsage();
        for (String guildID : commandUsage.keySet())
        {
            if (SettingsUtil.getGuildSettings(guildID).isCommandEnabled("MonthlyUsage"))
            {
                Guild guild = Core.getShardManager().getGuildById(guildID);
                guild.getDefaultChannel().sendMessage(mu.buildEmbed(fm, guild, true).build()).queue();
            }
        }
        fm.reset();
        scheduleNextBroadcast();
    }

    public static FileManager getFileManager()
    {
        return fm;
    }
}
