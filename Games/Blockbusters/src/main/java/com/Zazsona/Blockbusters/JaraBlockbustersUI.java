package com.Zazsona.Blockbusters;

import com.Zazsona.Blockbusters.AI.AIPlayer;
import com.Zazsona.Blockbusters.game.BlockbustersUI;
import com.Zazsona.Blockbusters.game.BlockbustersQuitException;
import com.Zazsona.Blockbusters.game.objects.Team;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.Core;
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

public class JaraBlockbustersUI implements BlockbustersUI //TODO: Nicer quits
{
    private TextChannel channel;
    private Thread quitThread;
    private QuitListener quitListener;
    private Team quitTeam;
    private Message questionMessage;

    public JaraBlockbustersUI(TextChannel channel)
    {
        this.channel = channel;
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
    public void sendAIMessage(String message)
    {
        channel.sendMessage(message).queue();
    }

    @Override
    public void dispose()
    {
        Core.getShardManagerNotNull().removeEventListener(quitListener);
        quitThread.interrupt();
    }

    @Override
    public Team waitForBuzzIn(Team whiteTeam, Team blueTeam, AIPlayer aiPlayer) throws BlockbustersQuitException
    {
        BuzzerListener buzzerListener = new BuzzerListener(whiteTeam, blueTeam, questionMessage);
        Core.getShardManagerNotNull().addEventListener(buzzerListener);
        questionMessage.addReaction("\uD83D\uDECE").queue();
        int aiBuzzInTime = (aiPlayer != null) ? aiPlayer.getRandomBuzzTimeMillis() : 0;
        final int POLL_TIME = 100;

        while (buzzerListener.getBuzzedTeam() == null && quitTeam == null)
        {
            try
            {
                Thread.sleep(POLL_TIME);
                aiBuzzInTime -= POLL_TIME;
                if (aiPlayer != null && aiBuzzInTime < 0)
                    buzzerListener.buzzedTeam = (whiteTeam.isAITeam()) ? whiteTeam : blueTeam;
            }
            catch (InterruptedException e)
            {
                //Do nothing
            }
        }
        Core.getShardManagerNotNull().removeEventListener(buzzerListener);
        if (quitTeam != null)
            throw new BlockbustersQuitException(quitTeam);
        return buzzerListener.getBuzzedTeam();
    }

    @Override
    public String getAnswer(Team answeringTeam, long secondsToAnswer) throws BlockbustersQuitException
    {
        AnswerListener answerListener = new AnswerListener(answeringTeam);
        Core.getShardManagerNotNull().addEventListener(answerListener);
        long startSecond = Instant.now().getEpochSecond();
        while ((answerListener.getAnswer() == null && Instant.now().getEpochSecond()-startSecond < secondsToAnswer)
                && quitTeam == null)
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
        if (quitTeam != null)
            throw new BlockbustersQuitException(quitTeam);
        return answerListener.getAnswer();
    }

    @Override
    public String getLetterSelection(Team answeringTeam) throws BlockbustersQuitException
    {
        AnswerListener answerListener = new AnswerListener(answeringTeam);
        Core.getShardManagerNotNull().addEventListener(answerListener);
        while ((answerListener.getAnswer() == null || !(answerListener.getAnswer().length() == 1 && answerListener.getAnswer().toUpperCase().matches("[A-Z]")))
                && quitTeam == null)
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
        if (quitTeam != null)
        {
            throw new BlockbustersQuitException(quitTeam);
        }

        return answerListener.getAnswer();
    }

    @Override
    public void listenForQuits(Team whiteTeam, Team blueTeam)
    {
        quitThread = new Thread(() ->
                                {
                                    quitListener = new QuitListener(whiteTeam, blueTeam);
                                    Core.getShardManagerNotNull().addEventListener(quitListener);
                                    while (whiteTeam.getMembers().size() > 0 && blueTeam.getMembers().size() > 0)
                                    {
                                        try
                                        {
                                            Thread.sleep(500);
                                        }
                                        catch (InterruptedException e)
                                        {
                                            //Do nothing
                                        }
                                    }
                                    Core.getShardManagerNotNull().removeEventListener(quitListener);
                                    quitTeam = (whiteTeam.getMembers().size() == 0) ? whiteTeam : blueTeam;
                                });
        quitThread.start();
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

    private class QuitListener extends ListenerAdapter
    {
        private Team whiteTeam;
        private Team blueTeam;

        public QuitListener(Team whiteTeam, Team blueTeam)
        {
            this.whiteTeam = whiteTeam;
            this.blueTeam = blueTeam;
        }

        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
        {
            super.onGuildMessageReceived(event);
            String messageContent = event.getMessage().getContentDisplay();
            if (event.getChannel().getId().equals(channel.getId()) && !event.getMember().getUser().isBot())
            {
                if (messageContent.equalsIgnoreCase("quit") || messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId())+"quit"))
                {
                    whiteTeam.removeMember(event.getMember());
                    blueTeam.removeMember(event.getMember());
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
