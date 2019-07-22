import commands.CmdUtil;
import commands.GameCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.security.SecureRandom;
import java.util.*;

public class Dodge extends GameCommand
{
    private final int BOARD_LENGTH = 7;
    private final int BOARD_HEIGHT = 5;

    private int score;
    private int currentPlayerLocation;
    private boolean playerHasMovedThisTick = false;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Dodge", msgEvent.getMember());            HashMap<Integer, LinkedList<Integer>> lasers = new HashMap<>();
        for (int i = 0; i<BOARD_LENGTH; i++)
        {
            lasers.put(i, new LinkedList<>());
        }
        currentPlayerLocation = BOARD_LENGTH/2;
        Message gameMessage = channel.sendMessage("The game will begin shortly...").complete();
        gameMessage.addReaction("\u25C0").complete();
        gameMessage.addReaction("\u25B6").queue();
        GameReactionListener grl = new GameReactionListener(this, gameMessage, msgEvent.getMember());
        msgEvent.getGuild().getJDA().addEventListener(grl);
        try
        {
            if (channel != msgEvent.getChannel())
            {
                Thread.sleep(4000); //This ensures the player has enough time to switch channels.
            }
            gameMessage.editMessage("Dodge:").complete();
            tick(gameMessage, lasers);
        }
        catch (InterruptedException e)
        {
            channel.sendMessage("An error has occurred and the game has been quit.").queue();
        }
        finally
        {
            msgEvent.getGuild().getJDA().removeEventListener(grl);
            channel.sendMessage("Game Over! Your final score was: **"+score+"** points!").queue();
        }

    }
    private void tick(Message message, HashMap<Integer, LinkedList<Integer>> lasers) throws InterruptedException
    {
        do
        {
            Thread.sleep(1000);
            lasers = plotLasers(lasers);
            message.editMessage(buildEmbed(message.getMember(), buildScreen(currentPlayerLocation, lasers)).build()).complete();
            playerHasMovedThisTick = false;
        } while (!checkForHit(currentPlayerLocation, lasers));
    }

    private boolean checkForHit(int currentPlayerLocation, HashMap<Integer, LinkedList<Integer>> lasers)
    {
        for (int laserLocation : lasers.get(currentPlayerLocation))
        {
            if (laserLocation == BOARD_HEIGHT-1)
            {
                return true;
            }
        }
        return false;
    }

    public int plotPlayer(boolean isLeft)
    {
        if (!playerHasMovedThisTick)
        {
            playerHasMovedThisTick = true;
            if (isLeft)
            {
                currentPlayerLocation--;
                if (currentPlayerLocation < 0)
                {
                    currentPlayerLocation = BOARD_LENGTH-1;
                }
            }
            else
            {
                currentPlayerLocation++;
                if (currentPlayerLocation >= BOARD_LENGTH)
                {
                    currentPlayerLocation = 0;
                }
            }
        }
        return currentPlayerLocation;
    }

    private HashMap<Integer, LinkedList<Integer>> plotLasers(HashMap<Integer, LinkedList<Integer>> lasers)
    {
        int currentLasers = 0;
        int maxLasers = (score/5)+1;//For every X points (X dodges), add a simultanious laser.
        for (int i = 0; i<lasers.size(); i++)
        {
            if (lasers.get(i).size() > 0)
            {
                for (int j = 0; j<lasers.get(i).size(); j++)
                {
                    lasers.get(i).set(j, lasers.get(i).get(j)+1);
                    currentLasers++;
                }
                Iterator iterator = lasers.get(i).iterator();
                while (iterator.hasNext())
                {
                    int val = (Integer) iterator.next();
                    if (val >= BOARD_HEIGHT)
                    {
                        iterator.remove();
                        currentLasers--;
                        score++;
                    }
                }
            }
        }
        if (currentLasers < maxLasers)
        {
            SecureRandom r = new SecureRandom();
            for (int i = currentLasers; i<maxLasers; i++)
            {
                int column = r.nextInt(lasers.size());
                if (!lasers.get(column).contains(1)) //There's no replacement here, think of it as a quirk. A moment's reprieve.
                {
                    lasers.get(column).add(1);
                }
            }
        }
        return lasers;
    }

    private String[][] buildScreen(int playerLocation, HashMap<Integer, LinkedList<Integer>> lasers)
    {
        String[][] screen = new String[BOARD_LENGTH][BOARD_HEIGHT];
        for (int i = 0; i<screen.length; i++)
        {
            Arrays.fill(screen[i], " ");
        }
        for (int i = 0; i<screen.length; i++)
        {
            screen[i][0] = "X";
        }
        for (int i = 0; i<screen.length; i++)
        {
            if (lasers.get(i).size() > 0)
            {
                for (int laserLocation : lasers.get(i))
                {
                    screen[i][laserLocation] = "|";
                }
            }
        }
        screen[playerLocation][BOARD_HEIGHT-1] = "O";
        return screen;
    }

    private EmbedBuilder buildEmbed(Member selfMember, String[][] screen)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(selfMember));
        embed.getDescriptionBuilder().append("```\n");
        embed.setAuthor("Score: "+score);
        for (int i = 0; i<screen[0].length; i++)
        {
            for (int j = 0; j<screen.length; j++)
            {
                embed.getDescriptionBuilder().append(screen[j][i]).append(" ");
            }
            embed.getDescriptionBuilder().append("\n");
        }
        embed.getDescriptionBuilder().append("```\n");
        return embed;
    }
}
