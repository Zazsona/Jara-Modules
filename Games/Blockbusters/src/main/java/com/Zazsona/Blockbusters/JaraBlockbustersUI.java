package com.Zazsona.Blockbusters;

import com.Zazsona.Blockbusters.game.BlockbustersUI;
import com.Zazsona.Blockbusters.game.objects.Team;
import commands.CmdUtil;
import jara.Core;
import jara.MessageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.File;
import java.time.Instant;

public class JaraBlockbustersUI implements BlockbustersUI //TODO: Add quits
{
    private TextChannel channel;
    private MessageManager mm;

    private Message questionMessage;

    public JaraBlockbustersUI(TextChannel channel)
    {
        this.channel = channel;
        this.mm = new MessageManager();
    }

    @Override
    public void sendBoard(File boardImageFile)
    {
        channel.sendFile(boardImageFile).complete();
    }

    @Override
    public void sendQuestion(String question)
    {
        questionMessage = channel.sendMessage(getEmbed(channel.getGuild().getSelfMember(), question)).complete();
    }

    @Override
    public void sendAnswerResponse(String response)
    {
        channel.sendMessage(getEmbed(channel.getGuild().getSelfMember(), response)).complete();
    }

    @Override
    public void sendWinMessage(String message)
    {
        channel.sendMessage(getEmbed(channel.getGuild().getSelfMember(), message)).complete();
    }

    @Override
    public Team waitForBuzzIn(Team whiteTeam, Team blueTeam)
    {
        BuzzerListener buzzerListener = new BuzzerListener(whiteTeam, blueTeam, questionMessage);
        Core.getShardManagerNotNull().addEventListener(buzzerListener);
        questionMessage.addReaction("\uD83D\uDECE").queue();

        while (buzzerListener.getBuzzedTeam() == null)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                //Do nothing
            }
        }
        Core.getShardManagerNotNull().removeEventListener(buzzerListener);
        return buzzerListener.getBuzzedTeam();
    }

    @Override
    public String getAnswer(Team answeringTeam, long secondsToAnswer)
    {
        AnswerListener answerListener = new AnswerListener(answeringTeam);
        Core.getShardManagerNotNull().addEventListener(answerListener);
        long startSecond = Instant.now().getEpochSecond();
        while (answerListener.getAnswer() == null && Instant.now().getEpochSecond()-startSecond < secondsToAnswer)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                //Do nothing
            }
        }
        Core.getShardManagerNotNull().removeEventListener(answerListener);
        return answerListener.getAnswer();
    }



    @Override
    public String getLetterSelection(Team answeringTeam)
    {
        AnswerListener answerListener = new AnswerListener(answeringTeam);
        Core.getShardManagerNotNull().addEventListener(answerListener);
        while (answerListener.getAnswer() == null || !(answerListener.getAnswer().length() == 1 && answerListener.getAnswer().toUpperCase().matches("[A-Z]")))
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                //Do nothing
            }
        }
        Core.getShardManagerNotNull().removeEventListener(answerListener);
        return answerListener.getAnswer();
    }

    private class BuzzerListener extends ListenerAdapter
    {
        private Team whiteTeam;
        private Team blueTeam;
        private Team buzzedTeam;
        private Message message;

        public BuzzerListener(Team whiteTeam, Team blueTeam, Message message)
        {
            this.whiteTeam = whiteTeam;
            this.blueTeam = blueTeam;
            this.message = message;
            buzzedTeam = null;
        }

        public Team getBuzzedTeam()
        {
            return buzzedTeam;
        }

        @Override
        public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
        {
            super.onMessageReactionAdd(event);
            if (event.getMessageId().equals(message.getId()) && !event.getMember().getUser().isBot())
            {
                if (event.getReactionEmote().getName().equals("\uD83D\uDECE") && buzzedTeam == null)
                {
                    if (whiteTeam.getMembers().contains(event.getMember()))
                    {
                        buzzedTeam = whiteTeam;
                    }
                    else if (blueTeam.getMembers().contains(event.getMember()))
                    {
                        buzzedTeam = blueTeam;
                    }
                }
            }
        }
    }

    private class AnswerListener extends ListenerAdapter
    {
        private Team answeringTeam;
        private String answer;

        public AnswerListener(Team answeringTeam)
        {
            this.answeringTeam = answeringTeam;
            this.answer = null;
        }

        public String getAnswer()
        {
            return answer;
        }

        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
        {
            super.onGuildMessageReceived(event);
            if (event.getChannel().equals(channel))
            {
                if (answeringTeam.getMembers().contains(event.getMember()))
                {
                    this.answer = event.getMessage().getContentDisplay();
                }
            }
        }
    }

    public static MessageEmbed getEmbed(Member selfMember, String description)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(selfMember));
        embed.setTitle("Blockbusters");
        embed.setDescription(description);
        return embed.build();
    }
}
