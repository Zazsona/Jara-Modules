import commands.GameCommand;
import jara.MessageManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class RockPaperScissors extends GameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        msgEvent.getChannel().sendMessage("Rock! Paper! Scissors!").queue();

        Message userMessage = new MessageManager().getNextMessage(msgEvent.getChannel());
        String[] selections = {"rock", "paper", "scissors"};
        String selection = userMessage.getContentDisplay().toLowerCase().replaceAll("[^\\w\\s]", "");

        String botSelection = selections[new Random().nextInt(selections.length)];
        msgEvent.getChannel().sendMessage(botSelection.toUpperCase()+"!").queue();

        String endMessage = "What in the world?";
        if (selection.equals(botSelection))
        {
            endMessage = ("Snap! It's a draw.");
        }
        else if (selection.equals("rock"))
        {
            if (botSelection.equals("paper"))
            {
                endMessage = ("That one's wrapped up in my favour!");
            }
            else if (botSelection.equals("scissors"))
            {
                endMessage = ("That one didn't slice up like I wanted.");
            }
        }
        else if (selection.equals("paper"))
        {
            if (botSelection.equals("scissors"))
            {
                endMessage = ("I'd say that's a pretty clear cut victory for me.");
            }
            else if (botSelection.equals("rock"))
            {
                endMessage = ("Now you've got me all rustled. Are you cheating?");
            }
        }
        else if (selection.equals("scissors"))
        {
            if (botSelection.equals("rock"))
            {
                endMessage = ("Booyah! I rock.");
            }
            else if (botSelection.equals("paper"))
            {
                endMessage = ("Guh. I've been cut down.");
            }
        }
        else
        {
            endMessage = ("Hm. Don't know that one.");
        }
        msgEvent.getChannel().sendMessage(endMessage).queue();
    }
}
