package com.Zazsona.CountdownNumbers;

import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class CountdownNumbers extends ModuleGameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/KwjqNkH.png");
        TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember()+"s-Countdown-Numbers");
        int[] numbers = generateNumbers();
        int target = new Random().nextInt(1000);
        embed.setDescription(buildWelcomeDescription(numbers, target));
        Message embedMessage = channel.sendMessage(embed.build()).complete();

        MessageManager mm = new MessageManager();
        Thread gameThread = Thread.currentThread();
        Thread quitThread = new Thread(() -> { checkForQuit(mm, gameThread, channel); });
        quitThread.start();
        boolean clockComplete = runClock(embed, embedMessage);

        if (clockComplete)
        {
            quitThread.interrupt();
            embed.setThumbnail("https://i.imgur.com/KwjqNkH.png");
            embed.setDescription("You've got 8 seconds to post your solutions!");
            channel.sendMessage(embed.build()).queue();
            Message[] answers = mm.getNextMessages(channel, 8*1000, Integer.MAX_VALUE);
            if (answers != null && answers.length > 0)
            {
                Message winningMessage = getWinningMessage(answers, numbers, target);
                embed.setThumbnail("https://i.imgur.com/scKHMRb.png");
                embed.setDescription("And the winner is... **"+winningMessage.getMember().getEffectiveName()+"** with "+winningMessage.getContentDisplay()+"!\nCongratulations.");

            }
            else
            {
                embed.setDescription("Wait... Nobody got anything!?\nWell, game over, then.");
            }
            channel.sendMessage(embed.build()).queue();
        }
        else
        {
            embed.setDescription("Clocking off early? Alright.");
            channel.sendMessage(embed.build()).queue();
        }
        super.deleteGameChannel();
    }

    private int[] generateNumbers()
    {
        Random r = new Random();
        int[] largeNumbers = {25, 100, 75, 50};
        int[] smallNumbers = { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10 };
        int[] selectedNumbers = new int[6];
        int index = 0;
        for (int i = 0; i<2; i++)
        {
            do
            {
                 index = r.nextInt(largeNumbers.length);

            } while (largeNumbers[index] == 0);
            selectedNumbers[i] = largeNumbers[index];
            largeNumbers[index] = 0;
        }
        for (int i = 2; i<selectedNumbers.length; i++)
        {
            do
            {
                index = r.nextInt(smallNumbers.length);

            } while (smallNumbers[index] == 0);
            selectedNumbers[i] = smallNumbers[index];
            smallNumbers[index] = 0;
        }
        return selectedNumbers;
    }

    private String buildWelcomeDescription(int[] numbers, int target)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("**Welcome to Countdown - Numbers Round!**\n");
        sb.append("You've got 30 seconds to get as close to the target as possible using +, -, *, / and ^.\n\n");
        sb.append("Target: **").append(target).append("**!\n");
        sb.append("Numbers: **- ");
        for (int number : numbers)
        {
            sb.append(number).append(" - ");
        }
        sb.append("**");
        return sb.toString();
    }

    private boolean runClock(EmbedBuilder embed, Message embedMessage)
    {
        try
        {
            String[] clockURLs = {"https://i.imgur.com/llmbGHa.png", "https://i.imgur.com/JpwwNrY.png", "https://i.imgur.com/F2deeEY.png", "https://i.imgur.com/eMrORg8.png"};
            for (int i = 0; i<4; i++)
            {
                Thread.sleep(7500);
                embed.setThumbnail(clockURLs[i]);
                embedMessage.editMessage(embed.build()).queue();
            }
            return true; //Clock has successfully run its course.
        }
        catch (InterruptedException e)
        {
            //Game has been quit
            return false; //Clock was interrupted.
        }
    }

    private void checkForQuit(MessageManager mm, Thread gameThread, TextChannel channel)
    {
        while (!Thread.interrupted())
        {
            Message msg = mm.getNextMessage(channel);
            if (msg != null && msg.getContentDisplay().equals(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                gameThread.interrupt();
                Thread.currentThread().interrupt();
            }
        }
    }

    private Message getWinningMessage(Message[] answers, int[] numbers, int target)
    {
        Message winningMessage = answers[0];
        double winningOffset = Integer.MAX_VALUE;
        for (Message message : answers)
        {
            String msgContent = message.getContentDisplay();
            if (validateAnswer(msgContent, numbers))
            {
                Stack<String> rpn = ShuntingYard.getPostFixNotation(msgContent);
                double result = ReversePolishNotationCalculator.calculate(rpn);
                double offset = (target-result >= 0) ? target-result : result-target; //We always want offset to be positive for easy comparisons.
                if (offset < winningOffset)
                {
                    winningMessage = message;
                    winningOffset = offset;
                }
            }
        }
        return winningMessage;
    }

    private boolean validateAnswer(String answer, int[] numbers)
    {
        ArrayList<String> remainingNumbers = new ArrayList<>();
        for (int num : numbers)
        {
            remainingNumbers.add(String.valueOf(num));
        }

        answer = answer.replace(" ", "");
        String[] usedNumbers = answer.split("[^0-9]+");
        for (String usedNum : usedNumbers)
        {
            boolean isValid = remainingNumbers.remove(usedNum);
            if (!isValid)
            {
                return false;
            }
        }
        return true;


    }

}
