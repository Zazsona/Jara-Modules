package com.Zazsona.Quiz.quiz;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class QuizTeam
{
    private String name;
    private ArrayList<Member> members;
    private boolean[] correct;
    private TextChannel channel;
    private int points;

    public QuizTeam(String name, Member member, Category channelCategory, int questionCount)
    {
        this.name = name;
        this.members = new ArrayList<>();
        this.members.add(member);
        this.correct = new boolean[questionCount];
        this.channel = channelCategory.createTextChannel(name.replace(" ", "-")).complete();
        this.channel.putPermissionOverride(channelCategory.getGuild().getPublicRole()).setDeny(Permission.MESSAGE_READ).queue();
        addMemberToChannel(member);
    }

    public void addTeamMember(Member member)
    {
        if (!members.contains(member))
        {
            members.add(member);
            addMemberToChannel(member);
        }
    }

    public void removeTeamMember(Member member)
    {
        if (members.contains(member))
        {
            members.remove(member);
            removeMemberFromChannel(member);
        }
    }

    public List<Member> getMembers()
    {
        return members;
    }


    public boolean isTeamMember(Member member)
    {
        return members.contains(member);
    }

    public int getTeamSize()
    {
        return members.size();
    }

    public String getTeamName()
    {
        return name;
    }

    public TextChannel getTeamChannel()
    {
        return channel;
    }

    public void setAnswerResult(int questionNo, boolean correct)
    {
        this.correct[questionNo] = correct;
    }

    public int getPoints()
    {
        return points;
    }

    public void addPoints(int points)
    {
        this.points += points;
    }

    public boolean isCorrectlyAnswered(int questionNo)
    {
        return correct[questionNo];
    }

    public boolean[] getCorrectAnswers()
    {
        return correct;
    }

    public void addMemberToChannel(Member member)
    {
        channel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).complete();
    }

    public void removeMemberFromChannel(Member member)
    {
        channel.putPermissionOverride(member).setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).complete();
    }
}
