package com.Zazsona.Blockbusters;

import com.Zazsona.Blockbusters.AI.AIDifficulty;
import com.Zazsona.Blockbusters.AI.AIPlayer;
import com.Zazsona.Blockbusters.game.BlockbustersQuitException;
import com.Zazsona.Blockbusters.game.GameMaster;
import com.Zazsona.Blockbusters.game.objects.Team;
import configuration.SettingsUtil;
import exceptions.QuitException;
import jara.Core;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.entities.Guild;
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

public class BlockbustersCommand extends ModuleGameCommand
{
    private static final String WHITE_EMOJI = "\u26AA";
    private static final String BLUE_EMOJI = "\uD83D\uDD35";
    private static final String CONFIRM_EMOJI = "\uD83C\uDD97";

    private AIDifficulty aiDifficulty = null;

    private Team whiteTeam = new Team(true);
    private Team blueTeam = new Team(false);

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            parseParameters(msgEvent.getGuild(), parameters);
            TextChannel channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Blockbusters", msgEvent.getMember());
            Message teamJoinMessage = channel.sendMessage(JaraBlockbustersUI.getEmbed(msgEvent.getGuild().getSelfMember(), "Select a team, then press OK to start.")).complete();
            if (aiDifficulty == null)
                teamJoinMessage.addReaction(WHITE_EMOJI).queue(); //Bot always takes white team, so we don't want users on it.
            teamJoinMessage.addReaction(BLUE_EMOJI).queue();
            teamJoinMessage.addReaction(CONFIRM_EMOJI).queue();

            TeamReactionListener teamReactionListener = new TeamReactionListener(teamJoinMessage, msgEvent.getMember());
            Core.getShardManagerNotNull().addEventListener(teamReactionListener);
            while (!teamReactionListener.isTeamsConfirmed())
            {
                try {Thread.sleep(500);} catch (InterruptedException e) {} //Do nothing on interrupt
            }
            Core.getShardManagerNotNull().removeEventListener(teamReactionListener);

            JaraBlockbustersUI jaraBlockbustersUI = new JaraBlockbustersUI(msgEvent.getChannel());
            GameMaster gameMaster = new GameMaster(jaraBlockbustersUI, whiteTeam, blueTeam, aiDifficulty);
            Team winningTeam = gameMaster.run();
            msgEvent.getChannel().sendMessage(JaraBlockbustersUI.getEmbed(msgEvent.getGuild().getSelfMember(), winningTeam.getTeamName()+" are the winners!")).queue();
        }
        catch (BlockbustersQuitException e)
        {
            msgEvent.getChannel().sendMessage(JaraBlockbustersUI.getEmbed(msgEvent.getGuild().getSelfMember(), e.getQuittingTeam().getTeamName()+" have all quit! Game over.")).queue();
        }
        catch (QuitException e)
        {
            msgEvent.getChannel().sendMessage(JaraBlockbustersUI.getEmbed(msgEvent.getGuild().getSelfMember(), "The game has been cancelled.")).queue();
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(this.getClass()).error(e.toString());
        }
        finally
        {
            deleteGameChannel();
        }
    }

    private void parseParameters(Guild guild, String[] parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("ai"))
            {
                String difficulty = "STANDARD";
                if (parameters.length > 2)
                    difficulty = parameters[2];
                initialiseAI(guild.getSelfMember(), difficulty);
            }
        }
    }

    private void initialiseAI(Member selfMember, String difficulty)
    {
        switch (difficulty.toUpperCase())
        {
            case "EASY":
                aiDifficulty = AIDifficulty.EASY;
                break;
            case "MEDIUM":
            case "STANDARD":
            case "NORMAL":
                aiDifficulty = AIDifficulty.STANDARD;
                break;
            case "HARD":
            case "PROUD":
            case "CRITICAL":
                aiDifficulty = AIDifficulty.HARD;
                break;
            default:
                aiDifficulty = AIDifficulty.STANDARD;
        }
        Team aiTeam = whiteTeam;
        aiTeam.setAITeam(true);
        aiTeam.addMember(selfMember);
    }

    public class TeamReactionListener extends ListenerAdapter
    {
        private boolean teamsConfirmed = false;
        private Message teamJoinMessage;
        private Member gameOwner;
        private boolean gameQuit = false;

        public TeamReactionListener(Message teamJoinMessage, Member gameOwner)
        {
            this.teamJoinMessage = teamJoinMessage;
            this.gameOwner = gameOwner;
        }

        public boolean isTeamsConfirmed() throws QuitException
        {
            if (gameQuit)
                throw new QuitException(null);
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
                        if (aiDifficulty == null)
                        {
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
                        if (event.getMember().equals(gameOwner))
                        {
                            if (blueTeam.getMembers().size() > 0 && whiteTeam.getMembers().size() > 0)
                            {
                                if (blueTeam.getMembers().contains(gameOwner) || whiteTeam.getMembers().contains(gameOwner))
                                    teamsConfirmed = true;
                                else
                                {
                                    event.getChannel().sendMessage(JaraBlockbustersUI.getEmbed(event.getGuild().getSelfMember(), "You must join a team!")).queue();
                                    teamJoinMessage.removeReaction(CONFIRM_EMOJI, event.getMember().getUser()).queue();
                                }
                            }
                            else
                            {
                                event.getChannel().sendMessage(JaraBlockbustersUI.getEmbed(event.getGuild().getSelfMember(), "Insufficient Players.")).queue();
                                teamJoinMessage.removeReaction(CONFIRM_EMOJI, event.getMember().getUser()).queue();
                            }
                        }
                        else
                        {
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

        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
        {
            super.onGuildMessageReceived(event);
            String messageContent = event.getMessage().getContentDisplay();
            if (messageContent.equalsIgnoreCase("quit") || messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId())+"quit"))
            {
                if (event.getChannel().getId().equals(teamJoinMessage.getChannel().getId()) && !event.getMember().getUser().isBot())
                {
                    if (event.getMember().equals(gameOwner))
                    {
                        gameQuit = true;
                    }
                    else
                    {
                        if (whiteTeam.getMembers().contains(event.getMember()))
                        {
                            whiteTeam.removeMember(event.getMember());
                            teamJoinMessage.removeReaction(WHITE_EMOJI, event.getMember().getUser()).queue();
                        }
                        else if (blueTeam.getMembers().contains(event.getMember()))
                        {
                            whiteTeam.removeMember(event.getMember());
                            teamJoinMessage.removeReaction(WHITE_EMOJI, event.getMember().getUser()).queue();
                        }
                    }
                }
            }
        }
    }
}
