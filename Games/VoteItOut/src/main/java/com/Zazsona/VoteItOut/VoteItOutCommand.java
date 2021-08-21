package com.Zazsona.VoteItOut;

import com.Zazsona.VoteItOut.game.VoteItOutGame;
import com.zazsona.jara.Core;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.LinkedList;

public class VoteItOutCommand extends ModuleGameCommand
{
    private TextChannel textChannel;

    private static final String JOIN_REACTION = "\uD83C\uDFB2";
    private static final String START_REACTION = "\uD83C\uDD97";

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        textChannel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Vote-It-Out");
        try
        {
            EmbedBuilder joinEmbed = getEmbedStyle(msgEvent.getGuild().getSelfMember()).setDescription("Click the dice to join, and OK when everyone's ready!").setFooter("Spaces Remaining: "+(VoteItOutGame.getMaxPlayers()));
            Message joinMessage = textChannel.sendMessage(joinEmbed.build()).complete();
            joinMessage.addReaction(JOIN_REACTION).queue();
            joinMessage.addReaction(START_REACTION).queue();
            JoinListener joinListener = new JoinListener(joinMessage, joinEmbed, msgEvent.getMember());
            Core.getShardManagerNotNull().addEventListener(joinListener);
            while (!joinListener.isGameStart())
            {
                try {Thread.sleep(500);} catch (InterruptedException e) {};
            }
            Core.getShardManagerNotNull().removeEventListener(joinListener);

            VoteItOutGame gameInstance = new VoteItOutGame(textChannel, joinListener.getPlayers().toArray(new Member[0]));
            gameInstance.runGame();

        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(getClass()).error(e.toString());
            textChannel.sendMessage("An error has occurred.").queue();
        }
        finally
        {
            super.deleteGameChannel();
        }
    }

    private EmbedBuilder getEmbedStyle(Member selfMember)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(selfMember));
        embed.setTitle("Vote It Out");
        return embed;
    }

    private class JoinListener extends ListenerAdapter
    {
        private Object lock = new Object();
        private Message joinMessage;
        private EmbedBuilder joinEmbed;
        private Member gameOwner;
        private boolean isGameStart;
        private LinkedList<Member> players;

        public JoinListener(Message joinMessage, EmbedBuilder joinEmbed, Member gameOwner)
        {
            this.joinMessage = joinMessage;
            this.joinEmbed = joinEmbed;
            this.gameOwner = gameOwner;
            players = new LinkedList<>();
        }

        public boolean isGameStart()
        {
            return isGameStart;
        }

        public LinkedList<Member> getPlayers()
        {
            return players;
        }

        @Override
        public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
        {
            synchronized (lock)
            {
                super.onMessageReactionAdd(event);
                if (!event.getMember().getUser().isBot() && (VoteItOutGame.getMaxPlayers() > players.size()+1) && event.getReactionEmote().getName().equals(JOIN_REACTION))
                {
                    players.add(event.getMember());
                    joinEmbed.setFooter("Spaces Remaining: "+(VoteItOutGame.getMaxPlayers()-players.size()));
                    joinMessage.editMessage(joinEmbed.build()).complete();
                }
                else if (event.getMember().equals(gameOwner) && event.getReactionEmote().getName().equals(START_REACTION))
                {
                    if (players.size() >= 2)
                        isGameStart = true;
                    else
                    {
                        textChannel.sendMessage(getEmbedStyle(textChannel.getGuild().getSelfMember()).setDescription("Insufficient Players!").build()).queue();
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                }
                else if (!event.getMember().equals(event.getGuild().getSelfMember())) //Check, so that the initial reactions don't get removed
                {
                    event.getReaction().removeReaction(event.getUser()).complete();
                }
            }
        }

        @Override
        public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event)
        {
            synchronized (lock)
            {
                super.onMessageReactionRemove(event);
                players.remove(event.getMember());
                joinEmbed.setFooter("Spaces Remaining: "+(VoteItOutGame.getMaxPlayers()-players.size()));
                joinMessage.editMessage(joinEmbed.build()).complete();
            }
        }
    }
}
