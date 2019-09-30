package com.Zazsona.OWStats;

import com.Zazsona.OWStats.json.Region;

public class PlayerStats
{
    private Region us;

    public double getKPD()
    {
        double kpd = us.stats.quickplay.game_stats.kpd;
        if (hasPlayedCompetitive())
        {
            if (us.stats.competitive.game_stats.kpd > kpd)
            {
                kpd = us.stats.competitive.game_stats.kpd;
            }
        }
        return kpd;
    }

    public int getBestKillStreak()
    {
        int ksb = us.stats.quickplay.game_stats.kill_streak_best;
        if (hasPlayedCompetitive())
        {
            if (us.stats.competitive.game_stats.kill_streak_best > ksb)
            {
                ksb = us.stats.competitive.game_stats.kill_streak_best;
            }
        }
        return ksb;
    }

    public int getHeroDamage()
    {
        return (us.stats.competitive.game_stats.hero_damage_done == 0) ? (us.stats.quickplay.game_stats.hero_damage_done) : (us.stats.quickplay.game_stats.hero_damage_done+us.stats.competitive.game_stats.hero_damage_done);
    }

    public int getHealingDone()
    {
        return (us.stats.competitive.game_stats.healing_done == 0) ? (us.stats.quickplay.game_stats.healing_done) : (us.stats.quickplay.game_stats.healing_done+us.stats.competitive.game_stats.healing_done);
    }

    public int getMedals()
    {
        return (us.stats.competitive.game_stats.medals == 0) ? (us.stats.quickplay.game_stats.medals) : (us.stats.quickplay.game_stats.medals+us.stats.competitive.game_stats.medals);
    }

    public int getCards()
    {
        return (us.stats.competitive.game_stats.cards == 0) ? (us.stats.quickplay.game_stats.cards) : (us.stats.quickplay.game_stats.cards+us.stats.competitive.game_stats.cards);
    }

    public int getLevel()
    {
        return us.stats.quickplay.overall_stats.level;
    }

    public String getPrestige()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<us.stats.quickplay.overall_stats.prestige; i++)
        {
            stringBuilder.append("\u2606");
        }
        if (stringBuilder.length() == 0)
            stringBuilder.append("-");

        return stringBuilder.toString();
    }

    public String getProfileIcon()
    {
        return us.stats.quickplay.overall_stats.avatar;
    }

    public String getLevelBorder()
    {
        return us.stats.quickplay.overall_stats.rank_image;
    }

    public int getPrestigeLevel()
    {
        return us.stats.quickplay.overall_stats.prestige;
    }

    public String getCompRank()
    {
        String rank = us.stats.quickplay.overall_stats.comprank;
        return (rank == null) ? "Unranked" : rank;
    }

    public long getTimePlayed()
    {
        double playtime = (us.stats.competitive.game_stats.time_played == 0) ? (us.stats.quickplay.game_stats.time_played) : (us.stats.quickplay.game_stats.time_played+us.stats.competitive.game_stats.time_played);
        return Math.round(playtime);
    }

    public boolean hasPlayedQuickplay()
    {
        return (us.stats.quickplay.game_stats.kpd != 0);
    }
    public boolean hasPlayedCompetitive()
    {
        return (us.stats.competitive.game_stats.kpd != 0);
    }


}
