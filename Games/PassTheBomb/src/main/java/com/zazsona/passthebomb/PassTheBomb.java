package com.zazsona.passthebomb;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PassTheBomb extends ModuleGameCommand
{
    private TextChannel channel;
    private String topic;
    private ArrayList<Member> players;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            players = new ArrayList<>();
            for (String name : parameters)
            {
                try
                {
                    Member member = msgEvent.getGuild().getMembersByName(name, true).get(0);
                    players.add(member);
                }
                catch (NullPointerException | IndexOutOfBoundsException e)
                {
                    try
                    {
                        Member member = msgEvent.getGuild().getMembersByNickname(name, true).get(0);
                        players.add(member);
                    }
                    catch (NullPointerException | IndexOutOfBoundsException e1)
                    {
                        continue;
                    }

                }
            }
            if (!players.contains(msgEvent.getMember()))
            {
                players.add(msgEvent.getMember());
            }
            topic = CmdUtil.getRandomTopic();
            channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-pass-the-bomb", players.toArray(new Member[0]));
            Collections.shuffle(players);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Pass The Bomb");
            embed.setDescription(":game_die: Welcome to Pass The Bomb! :game_die:\nI'm looking for **"+ topic +"**.\n\n**"+ players.get(0).getEffectiveName()+"**, you're up first! 10 seconds. Go!");
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/LxPYsv7.png");
            channel.sendMessage(embed.build()).queue();

            runGame(msgEvent);
        }
        catch (IOException e)
        {
            channel.sendMessage("An unexpected error occurred.").queue();
            e.printStackTrace();
            super.deleteGameChannel();
        }

    }

    private void runGame(GuildMessageReceivedEvent msgEvent)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Pass The Bomb");
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/LxPYsv7.png");

        MessageManager mm = new MessageManager();
        int round = 10;
        int timer = (round*1000);
        int playerIndex = 0;
        Message message = null;
        while (players.size() > 0)
        {
            long startTime = System.currentTimeMillis();
            message = mm.getNextMessage(channel, timer); //Grab one message, or let [timer] seconds elapse.

            if (message != null && message.getMember().equals(players.get(playerIndex)))
            {
                round--;
                timer = round > 1 ? round*1000 : 1000;
                playerIndex++;
                if (playerIndex >= players.size())
                {
                    playerIndex = 0;   //Can't be having those OOB exceptions!
                }
                embed.setDescription("Well done, you passed the bomb!\n\n**"+players.get(playerIndex).getEffectiveName()+"**, quick! You've got **"+(timer/1000)+"** seconds! I need **"+topic+"**!");
                channel.sendMessage(embed.build()).queue();
            }
            else if (message != null && !message.getMember().equals(players.get(playerIndex)))
            {
                long timeTaken = System.currentTimeMillis() - startTime;
                timer = timer - (int) timeTaken;
                if (timer <= 0) //If we pass a timer value of 0 to getNextMessage, it will act as infinite time.
                {
                    timer = 1;
                }
            }
            else
            {
                StringBuilder descBuilder = new StringBuilder();
                descBuilder.append("Oh dear! **").append(players.get(playerIndex).getEffectiveName()).append("** was blown to smithereens!");
                players.remove(playerIndex);

                if (players.size() != 0)
                {
                    round--;
                    timer = round > 1 ? round*1000 : 1000;
                    playerIndex++;
                    if (playerIndex >= players.size())
                        playerIndex = 0;   //Can't be having those OOB exceptions!

                    descBuilder.append("\n\n");
                    descBuilder.append("Here's another bomb. **").append(players.get(playerIndex).getEffectiveName()).append("**, you've got ").append(timer/1000).append(" seconds!");
                }
                else
                {
                    descBuilder.append("\n\n");
                    descBuilder.append("You're all down, so that's game! Thanks for playing.");
                    super.deleteGameChannel();
                }
                embed.setDescription(descBuilder.toString());
                embed.setThumbnail("https://i.imgur.com/U7JJwRS.png");
                channel.sendMessage(embed.build()).queue();
            }



        }

    }
}
