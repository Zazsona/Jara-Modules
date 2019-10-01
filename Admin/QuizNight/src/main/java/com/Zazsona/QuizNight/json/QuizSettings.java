package com.Zazsona.QuizNight.json;

import java.io.Serializable;
import java.util.HashSet;

public class QuizSettings implements Serializable
{
    private static final long serialVersionUID = 1L;
    public GuildQuizConfig[] GuildQuizConfigs = new GuildQuizConfig[0];

    public static class GuildQuizConfig implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public String GuildID = "0";
        public int StartMinute = 1260; //9PM
        public boolean[] Days = new boolean[7];
        public String[] AllowedRoles = new String[0];
        public boolean PingQuizAnnouncement = false;
        public HashSet<Integer> BannedCategories = new HashSet<>(); //TODO: All categories enabled by default.
    }
}
