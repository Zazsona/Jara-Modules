package com.Zazsona.Dodge;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GameReactionListener extends ListenerAdapter
{
    Dodge gameInstance;
    Message message;
    Member player;
    public GameReactionListener(Dodge gameInstance, Message message, Member player)
    {
        this.gameInstance = gameInstance;
        this.message = message;
        this.player = player;
    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent mra)
    {
        if (mra.getMessageId().equals(message.getId()) && mra.getMember().equals(player))
        {
            if (mra.getReactionEmote().getName().equals("\u25B6"))
            {
                gameInstance.plotPlayer(false);
            }
            else if (mra.getReactionEmote().getName().equals("\u25C0"))
            {
                gameInstance.plotPlayer(true);
            }
        }
        if (mra.getMessageId().equals(message.getId()) && !mra.getMember().equals(message.getGuild().getSelfMember()))
        {
            mra.getReaction().removeReaction(mra.getUser()).queue();
        }
    }
}
