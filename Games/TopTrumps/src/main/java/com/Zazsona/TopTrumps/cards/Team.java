package com.Zazsona.TopTrumps.cards;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Team
{
    private TextChannel channel;
    private ArrayList<Card> cards;
    private LinkedList<String> teamMembers;
    private LinkedList<String> exTeamMembers;

    public Team(TextChannel channel, List<Card> cards)
    {
        this.channel = channel;
        this.cards = new ArrayList<>();
        this.cards.addAll(cards);
        this.teamMembers = new LinkedList<>();
        this.exTeamMembers = new LinkedList<>();
    }

    public void cycleFrontCard()
    {
        Card card = cards.get(0);
        cards.remove(0);
        cards.add(card);
    }

    public Card addCard(Card card)
    {
        cards.add(card);
        return card;
    }

    public Card removeCard(int index)
    {
        return cards.remove(index);
    }

    public boolean hasMoreCards()
    {
        return !cards.isEmpty();
    }

    public TextChannel getChannel()
    {
        return channel;
    }

    public Card getFrontCard()
    {
        return cards.get(0);
    }

    public int getCardCount()
    {
        return cards.size();
    }

    public void addTeamMember(Member member)
    {
        teamMembers.add(member.getUser().getId());
        exTeamMembers.remove(member.getUser().getId());
    }

    public void removeTeamMember(Member member)
    {
        teamMembers.remove(member.getUser().getId());
        exTeamMembers.add(member.getUser().getId());
    }

    public int getMemberCount()
    {
        return teamMembers.size();
    }

    public boolean isPastOrPresentTeamMember(Member member)
    {
        boolean isTeamMember = teamMembers.contains(member.getUser().getId());
        if (!isTeamMember)
        {
            isTeamMember = exTeamMembers.contains(member.getUser().getId());
        }
        return isTeamMember;
    }


}
