package com.Zazsona.Blockbusters.game.objects;

import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class Team
{
    private ArrayList<Member> members;
    private boolean isWhiteTeam;

    public Team(Member captain, boolean isWhiteTeam)
    {
        this.isWhiteTeam = isWhiteTeam;
        members = new ArrayList<>();
        members.add(captain);
    }

    public void addMember(Member member)
    {
        members.add(member);
    }

    public void removeMember(Member member)
    {
        members.remove(member);
    }

    public List<Member> getMembers()
    {
        return members;
    }

    public boolean isWhiteTeam()
    {
        return isWhiteTeam;
    }

    public String getTeamName()
    {
        return (isWhiteTeam()) ? "White Team" : "Blue Team";
    }
}
