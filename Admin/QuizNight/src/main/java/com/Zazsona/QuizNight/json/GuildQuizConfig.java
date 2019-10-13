package com.Zazsona.QuizNight.json;

import java.io.Serializable;
import java.util.HashSet;

public class GuildQuizConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String guildID;
    private DayJson[] days;
    private HashSet<String> allowedRoles;
    private boolean pingQuizAnnouncement;

    public GuildQuizConfig(String guildID)
    {
        this.guildID = guildID;
        days = new DayJson[7];
        allowedRoles = new HashSet<>();
        pingQuizAnnouncement = false;
    }

    public String getGuildID()
    {
        return guildID;
    }

    public DayJson getDay(int dayValue)
    {
        if (days[dayValue-1] == null)
        {
            days[dayValue-1] = new DayJson();
        }
        return days[dayValue-1];
    }

    public void addAllowedRole(String roleID)
    {
        allowedRoles.add(roleID);
    }

    public void removeAllowedRole(String roleID)
    {
        allowedRoles.remove(roleID);
    }

    public boolean isRoleAllowedToJoin(String roleID)
    {
        if (allowedRoles.size() == 0)
            return true;
        else
            return allowedRoles.contains(roleID);
    }

    public HashSet<String> getAllowedRoles()
    {
        return allowedRoles;
    }

    public boolean isPingQuizAnnouncement()
    {
        return pingQuizAnnouncement;
    }

    public void setPingQuizAnnouncement(boolean pingQuizAnnouncement)
    {
        this.pingQuizAnnouncement = pingQuizAnnouncement;
    }
}
