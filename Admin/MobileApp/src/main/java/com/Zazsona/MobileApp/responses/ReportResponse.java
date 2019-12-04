package com.Zazsona.MobileApp.responses;

import configuration.SettingsUtil;
import jara.CommandHandler;
import jara.Core;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

public class ReportResponse extends Response
{
    private String botName;
    private String profileImageURL;
    private int activeGuilds;
    private int shardCount;
    private boolean online;
    private long uptimeSeconds;
    private double ping;
    private int connectedGuilds;
    private long commandUsageForSession;
    private HashMap<Integer, Integer> usageGraph; //HourSinceEpoch : Commands

    public ReportResponse()
    {
        super();
        this.botName = Core.getShardManagerNotNull().getShards().get(0).getSelfUser().getName();
        this.profileImageURL = Core.getShardManagerNotNull().getShards().get(0).getSelfUser().getAvatarUrl();
        this.connectedGuilds = Core.getShardManagerNotNull().getGuilds().size();
        this.shardCount = Core.getShardManagerNotNull().getShardsTotal();
        this.online = true;
        this.uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime()/1000;
        this.ping = Core.getShardManager().getAverageGatewayPing();
        this.activeGuilds = SettingsUtil.getActiveGuildCount();
        this.commandUsageForSession = CommandHandler.getCommandCount();
        this.usageGraph = CommandHandler.getCommandUsageMap();
    }

    public String getBotName()
    {
        return botName;
    }

    public String getProfileImageURL()
    {
        return profileImageURL;
    }

    public int getActiveGuilds()
    {
        return activeGuilds;
    }

    public int getShardCount()
    {
        return shardCount;
    }

    public boolean isOnline()
    {
        return online;
    }

    public long getUptimeSeconds()
    {
        return uptimeSeconds;
    }

    public double getPing()
    {
        return ping;
    }

    public int getConnectedGuilds()
    {
        return connectedGuilds;
    }

    public long getCommandUsageForSession()
    {
        return commandUsageForSession;
    }

    public HashMap<Integer, Integer> getUsageGraph()
    {
        return usageGraph;
    }
}
