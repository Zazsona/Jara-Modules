import commands.*;
import configuration.SettingsUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.security.SecureRandom;

public class Dice extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        int rollCount = 1;
        if (parameters.length > 1)
        {
            try
            {
                rollCount = Integer.parseInt(parameters[1]);
            }
            catch (NumberFormatException e)
            {
                rollCount = 1;
            }
        }
        int[] results = new int[rollCount];
        String diceType = "";
        switch (parameters[0].replace(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()).toString(), "").toLowerCase())
        {
            case "d2":
                results = rollDice(1, 1, rollCount); //Deliberately breaking the code here as a D2 technically can't exist, by setting a face of 1, we get 1 or 0, that is, true or false.
                diceType = "D2";
                break;
            case "d4":
                results = rollDice(4, 1, rollCount);
                diceType = "D4";
                break;
            case "d6":
                results = rollDice(6, 1, rollCount);
                diceType = "D6";
                break;
            case "d8":
                results = rollDice(8, 1, rollCount);
                diceType = "D8";
                break;
            case "d10":
                results = rollDice(10, 1, rollCount);
                diceType = "D10";
                break;
            case "d12":
                results = rollDice(12, 1, rollCount);
                diceType = "D12";
                break;
            case "d20":
                results = rollDice(20, 1, rollCount);
                diceType = "D20";
                break;
            case "d100":
                results = rollDice(10, 10, rollCount);
                diceType = "D100";
                break;
            default:
                results = rollDice(6, 1, rollCount);
                diceType = "D6";
                break;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("=== Dice ===");
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        for (int result : results)
        {
            embed.getDescriptionBuilder().append(diceType).append(": **").append(result).append("**\n");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int[] rollDice(int faces, int stepAmount, int rolls)
    {
        SecureRandom r = new SecureRandom();
        int[] results = new int[rolls];
        for (int i = 0; i<rolls; i++)
        {
            results[i] = (r.nextInt(faces)+1)*stepAmount; //+1 since dice don't have a zero value (Except D10/D100, but those are read as 10/100 respectively.)
        }
        return results;
    }
}
