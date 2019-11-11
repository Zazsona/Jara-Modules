package com.Zazsona.DealOrNoDeal;

import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.*;

public class DealOrNoDeal extends ModuleGameCommand
{
    private HashMap<Integer, Double> boxes;
    private long timeToAnswerLastDeal = 0;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            boxes = new HashMap<>();
            TextChannel channel = setup(msgEvent);
            Member player = msgEvent.getMember();
            MessageManager mm = new MessageManager();
            double selectedBoxValue = getStartingBox(channel, player, mm);
            boolean offerAccepted = false;
            int roundNo = 1;
            while (boxes.size() > 1 && !offerAccepted)
            {
                offerAccepted = playRound(channel, player, mm, roundNo, selectedBoxValue);
                roundNo++;
            }
            if (!offerAccepted)
            {
                selectedBoxValue = swapBoxes(channel, player, mm, selectedBoxValue);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.red);
                embed.setTitle("=== Deal or No Deal ===");
                embed.setDescription("You've risked it to this point, and your box is worth...\n\n**$"+String.format("%,.2f", selectedBoxValue)+"**!");
                channel.sendMessage(embed.build()).queue();
            }
            else
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.red);
                embed.setTitle("=== Deal or No Deal ===");
                embed.setDescription("**You accepted the banker's offer!**\n\nYour box would have been worth... **$"+String.format("%,.2f", selectedBoxValue)+"**!");
                channel.sendMessage(embed.build()).queue();
            }
            endGame();
        }
        catch (GameOverException e)
        {
            msgEvent.getChannel().sendMessage("The game has been quit.");
            endGame();
        }
    }

    private void endGame()
    {
        deleteGameChannel();
    }

    private double swapBoxes(TextChannel channel, Member player, MessageManager mm, double selectedBoxValue) throws GameOverException
    {
        if (boxes.size() == 1)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.red);
            embed.setTitle("=== Deal or No Deal ===");
            embed.setDescription("Would you like to swap your box? (Y/N)");
            channel.sendMessage(embed.build()).queue();
            while (true)
            {
                Message msg = mm.getNextMessage(channel);
                if (msg.getMember().equals(player))
                {
                    String msgContent = msg.getContentDisplay();
                    if (msgContent.equalsIgnoreCase("Y") || msgContent.equalsIgnoreCase("yes") || msgContent.equalsIgnoreCase("yeah"))
                    {
                        selectedBoxValue = boxes.values().toArray(new Double[1])[0];
                        embed.setDescription("Alright, I've swapped the boxes!");
                        break;
                    }
                    else if  (msgContent.equalsIgnoreCase("N") || msgContent.equalsIgnoreCase("no") || msgContent.equalsIgnoreCase("nah") || msgContent.equalsIgnoreCase("nope"))
                    {
                        embed.setDescription("Holding on to it? You've got a lot of faith in that one.");
                        break;
                    }
                    else if (msgContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                    {
                        throw new GameOverException();
                    }
                }
            }
            channel.sendMessage(embed.build()).queue();
            return selectedBoxValue;
        }
        else
        {
            channel.sendMessage("You can't do this yet, it's a bug!").queue();
            throw new GameOverException();
        }
    }

    private String getRemainingBoxes()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int boxNo : boxes.keySet())
        {
            stringBuilder.append(boxNo).append(" - ");
        }
        return stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("-")).toString().trim();
    }

    private boolean playRound(TextChannel channel, Member player, MessageManager mm, int roundNo, double playerBoxValue) throws GameOverException
    {
        int boxesToOpen = (roundNo == 1) ? 5 : 3;
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.red);
        embed.setTitle("=== Deal or No Deal ===");
        embed.setDescription("**Round "+roundNo+"**\n\nPick your "+boxesToOpen+" boxes!\n"+getRemainingBoxes());
        channel.sendMessage(embed.build()).queue();
        for (int i = 0; i<boxesToOpen; i++)
        {
            while (true)
            {
                Message msg = mm.getNextMessage(channel);
                String msgContent = msg.getContentDisplay();
                if (msgContent.matches("[0-9]+") && msg.getMember().equals(player))
                {
                    int boxNo = Integer.parseInt(msgContent);
                    double boxVal = pickBox(channel, boxNo, true);
                    if (boxVal == 0)
                    {
                        continue;
                    }
                    break;
                }
                else if (msgContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                {
                    throw new GameOverException();
                }
            }
        }
        return makeOffer(channel, player, mm, roundNo, playerBoxValue);
    }

    private boolean makeOffer(TextChannel channel, Member player, MessageManager mm, int roundNo, double playerBoxValue) throws GameOverException
    {
        long timeOfferMade = Instant.now().toEpochMilli();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.red);
        embed.setTitle("=== :telephone: ===");
        embed.setDescription("*ring ring*\n\nI'll make you an offer you can't refuse. **$"+String.format("%,.2f", getOfferValue(roundNo, playerBoxValue))+"**.\nDeal or no deal?");
        embed = addRemainingValuesFields(embed, playerBoxValue);
        channel.sendMessage(embed.build()).queue();

        while (true)
        {
            Message msg = mm.getNextMessage(channel);
            String msgContent = msg.getContentDisplay();
            if (msg.getMember().equals(player))
            {
                timeToAnswerLastDeal = Instant.now().toEpochMilli()-timeOfferMade;
                if (msgContent.equalsIgnoreCase("deal") || msgContent.equalsIgnoreCase("yes") || msgContent.equalsIgnoreCase("y"))
                {
                    return true;
                }
                else if (msgContent.equalsIgnoreCase("no deal") || msgContent.equalsIgnoreCase("no") || msgContent.equalsIgnoreCase("n"))
                {
                    return false;
                }
                else if (msgContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                {
                    throw new GameOverException();
                }
            }
        }
    }

    private EmbedBuilder addRemainingValuesFields(EmbedBuilder embed, double playerBoxValue)
    {
        ArrayList<Double> boxValues = new ArrayList<Double>();
        boxValues.addAll(boxes.values());
        boxValues.add(playerBoxValue);
        boxValues.sort(Double::compareTo);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<boxValues.size()/2; i++)
        {
            sb.append(String.format("%,.2f", boxValues.get(i))).append("\n");
        }
        embed.addField("Remaining Values", sb.toString(), true);
        sb = new StringBuilder();
        for (int i = boxValues.size()/2; i<boxValues.size(); i++)
        {
            sb.append(String.format("%,.2f", boxValues.get(i))).append("\n");
        }
        embed.addField("", sb.toString(), true);
        return embed;
    }

    private double getOfferValue(int roundNo, double playerBoxValue)
    {
        double estimatedValue = 0;
        double lowestValue = playerBoxValue;
        double highestValue = playerBoxValue;
        for (double value : boxes.values())
        {
            estimatedValue += value;
            lowestValue = (lowestValue > value) ? value : lowestValue;
            highestValue = (highestValue < value) ? value : highestValue;
        }
        estimatedValue += playerBoxValue;
        estimatedValue = estimatedValue/(boxes.size()+1);
        int percentage = (6*roundNo)+31;

        int volatilityWeight = 5; //The amount volatility can affect the final sum.
        double volatilityRisk = volatilityWeight-(((highestValue-lowestValue)/249999.99)*volatilityWeight); //249999.99 is the largest disparity.
        volatilityRisk = (volatilityRisk == 0) ? volatilityWeight : volatilityRisk; //Edge case for when it's 1/1, as 1-1=0.

        int confidenceWeight = 5;
        double clampedOfferTime = (timeToAnswerLastDeal > 60000.0) ? 60000.0 : timeToAnswerLastDeal;
        double confidenceRisk = (clampedOfferTime/60000.0)*confidenceWeight;

        double baseOffer = (estimatedValue/100*percentage);
        double volatilityConsciousOffer = baseOffer-(baseOffer/100*volatilityRisk);
        double volatilityConfidenceConsciousOffer = volatilityConsciousOffer-(baseOffer/100*confidenceRisk);

        /*System.err.println();
        System.err.println("Lowest Value: "+lowestValue);
        System.err.println("Highest Value: "+highestValue);
        System.err.println("Offer Time: "+clampedOfferTime);


        System.err.println("Expected V: "+estimatedValue);
        System.err.println("Volatility Effect: "+volatilityRisk);
        System.err.println("Confidence Effect: "+confidenceRisk);
        System.err.println("baseOffer: "+baseOffer);
        System.err.println("Base with Volatility "+volatilityConsciousOffer);
        System.err.println("Final Offer: "+volatilityConfidenceConsciousOffer);*/

        return volatilityConfidenceConsciousOffer;
    }

    private double getStartingBox(TextChannel channel, Member player, MessageManager mm) throws GameOverException
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.red);
        embed.setTitle("=== Deal or No Deal ===");
        embed.setDescription("Welcome!\n\nPlease select your starting box.\n"+getRemainingBoxes());
        channel.sendMessage(embed.build()).queue();

        while (true)
        {
            Message msg = mm.getNextMessage(channel);
            String msgContent = msg.getContentDisplay();
            if (msgContent.matches("[0-9]+") && msg.getMember().equals(player))
            {
                int boxNo = Integer.parseInt(msgContent);
                return pickBox(channel, boxNo, false);
            }
            else if (msgContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                throw new GameOverException();
            }
        }


    }

    private double pickBox(TextChannel channel, int boxNo, boolean revealContents)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.red);
        double boxValue = 0;
        if (boxes.containsKey(boxNo))
        {
            if (revealContents)
            {
                StringBuilder descBuilder = new StringBuilder();
                descBuilder.append("**-      Box #").append(boxNo).append("**      -").append("\n\n       $").append(String.format("%,.2f", boxes.get(boxNo)));
                embed.setDescription(descBuilder.toString());
            }
            else
            {
                embed.setDescription("You picked box **"+boxNo+"**");
            }
            boxValue = boxes.get(boxNo);
            boxes.remove(boxNo);
        }
        else if (boxNo < 1 || boxNo > 22)
        {
            embed.setDescription("That's not a valid box.");
        }
        else
        {
            embed.setDescription("You've already taken that box.");
        }
        channel.sendMessage(embed.build()).queue();
        return boxValue;
    }

    private TextChannel setup(GuildMessageReceivedEvent msgEvent)
    {
        TextChannel channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-DealOrNoDeal");
        Random r = new Random();
        LinkedList<Double> boxValues = getDefaultBoxValues();
        int boxNo = -1;
        for (int i = 0; i<22; i++)
        {
            while (boxes.containsKey(boxNo) || boxNo == -1)
            {
                boxNo = (r.nextInt(22)+1);
            }
            boxes.put(boxNo, boxValues.removeFirst());
        }
        return channel;
    }

    private LinkedList<Double> getDefaultBoxValues()
    {
        LinkedList<Double> boxValues = new LinkedList<>();
        boxValues.add(0.01);
        boxValues.add(0.10);
        boxValues.add(0.50);
        boxValues.add(1.0);
        boxValues.add(5.0);
        boxValues.add(10.0);
        boxValues.add(50.0);
        boxValues.add(100.0);
        boxValues.add(250.0);
        boxValues.add(500.0);
        boxValues.add(750.0);
        boxValues.add(1000.0);
        boxValues.add(3000.0);
        boxValues.add(5000.0);
        boxValues.add(10000.0);
        boxValues.add(15000.0);
        boxValues.add(20000.0);
        boxValues.add(35000.0);
        boxValues.add(50000.0);
        boxValues.add(75000.0);
        boxValues.add(100000.0);
        boxValues.add(250000.0);
        return boxValues;
    }
}
