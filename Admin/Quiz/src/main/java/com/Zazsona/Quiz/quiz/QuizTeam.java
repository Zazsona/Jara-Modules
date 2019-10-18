package com.Zazsona.Quiz.quiz;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;

public class QuizTeam
{
    private String name;
    private ArrayList<Member> members;
    private boolean[] correct;
    private TextChannel channel;
    private int points;


    public QuizTeam(String name, Member leader, TextChannel channel)
    {
        this.name = name;
        members = new ArrayList<>();
        members.add(leader);
        correct = new boolean[10];
        this.channel = channel;
    }

    public void addTeamMember(Member member)
    {
        members.add(member);
    }

    public void removeTeamMember(Member member)
    {
        members.remove(member);
    }

    public boolean isTeamMember(Member member)
    {
        return members.contains(member);
    }

    public Member[] getTeamMembers()
    {
        return members.toArray(new Member[0]);
    }

    public boolean hasMembers()
    {
        return (members.size() > 0);
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

    public boolean isCorrect(int questionNo)
    {
        return correct[questionNo];
    }

    public boolean[] getCorrectAnswers()
    {
        return correct;
    }


}
