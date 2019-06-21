import commands.CmdUtil;
import commands.Load;
import configuration.SettingsUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class MonthlyUsageRecorder extends Load
{
    private static FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        CommandListener cmdListener = new CommandListener();
        CmdUtil.getJDA().addEventListener(cmdListener);


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
        Calendar broadcastTime = Calendar.getInstance();
        if (fm.getLastReset().getMonthValue() == utc.getMonthValue() && fm.getLastReset().getYear() == utc.getYear())
            broadcastTime.set(Calendar.MONTH, utc.getMonthValue()+1);
        else
            broadcastTime.set(Calendar.MONTH, utc.getMonthValue());
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

    public class CommandListener extends ListenerAdapter
    {
        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event)
        {
            /*
            Yes, this is very rough, but this has enough performance cost as it is. I don't want to be verifying it's a command here as well as for the main command loader.
             */
            if (event.getMessage().getContentDisplay().startsWith(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId()).toString()))
            {
                fm.addUsage(event.getGuild().getId(), event.getAuthor().getId());
            }
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
                Guild guild = CmdUtil.getJDA().getGuildById(guildID);
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
