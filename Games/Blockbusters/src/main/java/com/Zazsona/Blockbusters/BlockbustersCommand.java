package com.Zazsona.Blockbusters;

import com.Zazsona.Blockbusters.game.GameMaster;
import com.Zazsona.Blockbusters.game.QuestionSheet;
import com.Zazsona.Blockbusters.game.objects.Team;
import com.Zazsona.Blockbusters.game.objects.TileState;
import jara.Core;
import module.ModuleCommand;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BlockbustersCommand extends ModuleGameCommand
{
    private static final String WHITE_EMOJI = "\u26AA";
    private static final String BLUE_EMOJI = "\uD83D\uDD35";
    private static final String CONFIRM_EMOJI = "\uD83C\uDD97";

    private Team whiteTeam = new Team(true);
    private Team blueTeam = new Team(false);

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            QuestionSheet.getInstance();
            TextChannel channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Blockbusters", msgEvent.getMember());
            Message teamJoinMessage = channel.sendMessage(JaraBlockbustersUI.getEmbed(msgEvent.getGuild().getSelfMember(), "Select a team, then press OK to start.")).complete();
            teamJoinMessage.addReaction(WHITE_EMOJI).queue();
            teamJoinMessage.addReaction(BLUE_EMOJI).queue();
            teamJoinMessage.addReaction(CONFIRM_EMOJI).queue();

            TeamReactionListener teamReactionListener = new TeamReactionListener(teamJoinMessage);
            Core.getShardManagerNotNull().addEventListener(teamReactionListener);
            while (!teamReactionListener.isTeamsConfirmed())
            {
                try {Thread.sleep(500);} catch (InterruptedException e) {} //Do nothing on interrupt
            }
            Core.getShardManagerNotNull().removeEventListener(teamReactionListener);
            initialiseAI(channel);

            JaraBlockbustersUI jaraBlockbustersUI = new JaraBlockbustersUI(msgEvent.getChannel());
            GameMaster gameMaster = new GameMaster(jaraBlockbustersUI, whiteTeam, blueTeam);
            Team winningTeam = gameMaster.run();
            msgEvent.getChannel().sendMessage(winningTeam.getTeamName()+" are the winners!").queue();

            deleteGameChannel();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initialiseAI(TextChannel channel) //TODO: AI
    {
        Team aiTeam = null;
        if (whiteTeam.getMembers().size() == 0)
        {
            aiTeam = whiteTeam;
        }
        else if (blueTeam.getMembers().size() == 0)
        {
            aiTeam = blueTeam;
        }
        if (aiTeam != null)
        {
            aiTeam.setAITeam(true);
            channel.sendMessage(aiTeam.getTeamName()+" doesn't have enough players, a CPU will join in.").queue();
        }
    }

    public class TeamReactionListener extends ListenerAdapter
    {
        private boolean teamsConfirmed = false;
        private Message teamJoinMessage;

        public TeamReactionListener(Message teamJoinMessage)
        {
            this.teamJoinMessage = teamJoinMessage;
        }

        public boolean isTeamsConfirmed()
        {
            return teamsConfirmed;
        }

        @Override
        public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
        {
            super.onMessageReactionAdd(event);
            if (!event.getMember().getUser().isBot() && event.getMessageId().equals(teamJoinMessage.getId()))
            {
                switch (event.getReactionEmote().getName())
                {
                    case WHITE_EMOJI:
                        if (whiteTeam.getMembers().contains(event.getMember()))
                        {
                            whiteTeam.removeMember(event.getMember());
                        }
                        else
                        {
                            whiteTeam.addMember(event.getMember());
                            if (blueTeam.getMembers().contains(event.getMember()))
                            {
                                blueTeam.removeMember(event.getMember());
                                teamJoinMessage.removeReaction(BLUE_EMOJI, event.getMember().getUser()).queue();
                            }
                        }
                        break;
                    case BLUE_EMOJI:
                        if (blueTeam.getMembers().contains(event.getMember()))
                        {
                            blueTeam.removeMember(event.getMember());
                        }
                        else
                        {
                            blueTeam.addMember(event.getMember());
                            if (whiteTeam.getMembers().contains(event.getMember()))
                            {
                                whiteTeam.removeMember(event.getMember());
                                teamJoinMessage.removeReaction(WHITE_EMOJI, event.getMember().getUser()).queue();
                            }
                        }
                        break;
                    case CONFIRM_EMOJI:
                        if (blueTeam.getMembers().size() > 0 || whiteTeam.getMembers().size() > 0)
                        {
                            teamsConfirmed = true;
                        }
                        else
                        {
                            event.getChannel().sendMessage(JaraBlockbustersUI.getEmbed(event.getGuild().getSelfMember(), "Insufficient Players.")).queue();
                            teamJoinMessage.removeReaction(CONFIRM_EMOJI, event.getMember().getUser()).queue();
                        }
                        break;
                }
            }
        }

        @Override
        public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event)
        {
            super.onMessageReactionRemove(event);

            if (!event.getMember().getUser().isBot() && event.getMessageId().equals(teamJoinMessage.getId()))
            {
                switch (event.getReactionEmote().getName())
                {
                    case WHITE_EMOJI:
                        whiteTeam.removeMember(event.getMember());
                        break;
                    case BLUE_EMOJI:
                        blueTeam.removeMember(event.getMember());
                        break;
                }
            }
        }
    }
}
