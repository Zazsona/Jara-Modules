package com.zazsona.beatthecores;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.configuration.SettingsUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

public class GameDriver
{
    public static final String POUND_SIGN = "\u00A3";

    private TextChannel channel;
    private Member selfMember;
    private Member player;

    private MessageManager mm;
    private TriviaDatabase triviaDatabase;
    private boolean gameOver;

    private int minCores = 2;
    private int maxCores = 5;

    public GameDriver(TextChannel channel, Member selfMember, Member player)
    {
        mm = new MessageManager();
        triviaDatabase = new TriviaDatabase();
        this.channel = channel;
        this.selfMember = selfMember;
        this.player = player;
    }

    public void start()
    {
        channel.sendMessage(getWelcomeEmbed()).queue();
        try {Thread.sleep(1500);} catch (InterruptedException e) {};
        runGameplayLoop();
    }

    private void runGameplayLoop()
    {
        try
        {
            CashBuilderPerformance cashBuilderPerformance = runCashBuilder();
            if (cashBuilderPerformance != null && cashBuilderPerformance.getQuestionsCorrectCount() > 0)
            {
                CoreChallengeOffer[] offers = getChallengeOffers(cashBuilderPerformance);
                CoreChallengeOffer selectedOffer = selectOffer(offers);
                boolean isPlayerWin = runCoreChallengeQuestions(selectedOffer);
            }
            else
                endGame(false);
        }
        catch (CancellationException e)
        {
            endGame(true);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private CashBuilderPerformance runCashBuilder() throws CancellationException
    {
        int totalQuestions = 5;
        ArrayList<Long> answerTimes = new ArrayList<>();
        for (int questionNo = 1; questionNo < (totalQuestions + 1); questionNo++)
        {
            long questionStartTime = Instant.now().getEpochSecond();
            Trivia trivia = triviaDatabase.getTrivia(questionNo, DifficultyLevel.EASY);
            EmbedBuilder triviaEmbed = trivia.getEmbed();
            channel.sendMessage(triviaEmbed.build()).queue();
            Message msg = mm.getNextMessage(channel, player);
            String answer = msg.getContentDisplay().trim();
            if (answer.equalsIgnoreCase("quit") || answer.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                throw new CancellationException("Game quit.");
            }
            else
            {
                boolean correct = trivia.isAnswerCorrect(answer);
                if (correct)
                {
                    long questionAnswerDuration = questionStartTime - Instant.now().getEpochSecond();
                    answerTimes.add(questionNo - 1, questionAnswerDuration);
                    if (questionNo < 5)
                    {
                        MessageEmbed embed = getEmbedStyle().setDescription(String.format("Correct! You're on %s%d. Here comes your next question...", POUND_SIGN, questionNo * 1000)).build();
                        channel.sendMessage(embed).queue();
                    }
                    else if (questionNo == 5)
                    {
                        MessageEmbed embed = getEmbedStyle().setDescription(String.format("Correct! You've completed the Cash Builder! That's %s%d to bet against the Cores!", POUND_SIGN, questionNo * 1000)).build();
                        channel.sendMessage(embed).queue();
                        return new CashBuilderPerformance(questionNo, questionNo * 1000, answerTimes);
                    }
                }
                else
                {
                    if (questionNo == 1)
                    {
                        channel.sendMessage(getEmbedStyle().setDescription(String.format("Sorry, that's the incorrect answer and you're out the game. Better luck next time!", trivia.getCorrectAnswer())).build()).queue();
                        return new CashBuilderPerformance(0, 0, answerTimes);
                    }
                    else
                    {
                        MessageEmbed embed = getEmbedStyle().setDescription(String.format("Sorry, that's the incorrect answer. It is in fact %s.\nHowever, you've still got %s%d to bet against the Cores.", trivia.getCorrectAnswer(), POUND_SIGN, ((questionNo - 1) * 1000))).build();
                        channel.sendMessage(embed).queue();
                        return new CashBuilderPerformance(questionNo, questionNo * 1000, answerTimes);
                    }
                }
            }
        }
        return null;
    }

    private CoreChallengeOffer[] getChallengeOffers(CashBuilderPerformance builderPerformance)
    {
        CoreChallengeOffer[] challengeOffers = new CoreChallengeOffer[4];
        int maxTime = 90;
        for (int cores = minCores; cores < (maxCores + 1); cores++)
        {
            int prizeOffer = builderPerformance.getCash();
            int timeOffer = maxTime;
            switch (cores)
            {
                case 2:
                    prizeOffer = builderPerformance.getCash();
                    timeOffer = Math.round(maxTime * 0.5f);
                    break;
                case 3:
                    prizeOffer = builderPerformance.getCash() * 3;
                    timeOffer = Math.round(maxTime * 0.58f);
                    break;
                case 4:
                    prizeOffer = builderPerformance.getCash() * 10;
                    timeOffer = Math.round(maxTime * 0.65f);
                    break;
                case 5:
                    prizeOffer = builderPerformance.getCash() * 30;
                    timeOffer = Math.round(maxTime * 0.70f);
                    break;
            }
            challengeOffers[cores - minCores] = new CoreChallengeOffer(cores, timeOffer, maxTime, prizeOffer);
        }
        return challengeOffers;
    }

    private CoreChallengeOffer selectOffer(CoreChallengeOffer[] challengeOffers) throws CancellationException
    {
        sendOffers(challengeOffers);
        while (true)
        {
            Message msg = mm.getNextMessage(channel, player);
            String content = msg.getContentDisplay().trim().toLowerCase(Locale.ROOT);
            String formattedContent = content.replace("#", "").replace("offer", "").replace("bet", "").replace("option", "").trim();
            if (formattedContent.matches("[0-9]"))
            {
                int offerNo = Integer.parseInt(formattedContent);
                if (offerNo > 0 && offerNo <= challengeOffers.length)
                {
                    int offerIndex = offerNo - 1;
                    CoreChallengeOffer offer = challengeOffers[offerIndex];
                    return offer;
                }
            }
            else if (content.equalsIgnoreCase("quit") || content.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId()) + "quit"))
            {
                throw new CancellationException("Game quit.");
            }
        }
    }

    private boolean runCoreChallengeQuestions(CoreChallengeOffer offer) throws CancellationException, InterruptedException
    {
        boolean isPlayerTurn = true;
        try
        {
            Random r = new Random();
            int questionNo = 0;

            float minCorrectChance = 0.33f;
            float maxCorrectChance = 0.9f;
            float coresRange = maxCores - minCores;
            float step = (maxCorrectChance - minCorrectChance) / coresRange;
            float coresCorrectChance = minCorrectChance + ((offer.getCoreCount() - minCores) * step);

            ChallengeTimer challengeTimer = new ChallengeTimer(offer.getPlayerTime() * 1000, offer.getCoreTime() * 1000);
            challengeTimer.setTimerMode(true);
            challengeTimer.start();
            while (true)
            {
                challengeTimer.setTimerMode(isPlayerTurn);
                long remainingTime = (isPlayerTurn) ? challengeTimer.getRemainingPlayerTimeMs() : challengeTimer.getRemainingCoresTimeMs();
                questionNo++;
                DifficultyLevel difficultyLevel = (r.nextBoolean()) ? DifficultyLevel.EASY : DifficultyLevel.MEDIUM;
                Trivia trivia = triviaDatabase.getTrivia(questionNo, difficultyLevel);
                EmbedBuilder triviaEmbed = trivia.getEmbed().setColor(Color.RED);
                triviaEmbed.setTitle(String.format("Time: %ds", remainingTime / 1000));
                channel.sendMessage(triviaEmbed.build()).complete();
                if (isPlayerTurn)
                {
                    Message msg = mm.getNextMessage(channel, player, (int) challengeTimer.getRemainingPlayerTimeMs()); // TODO: I should definitely change this to take a long in Jara core. Why on Earth is it an int!? Should be fine for now though.
                    if (msg == null)
                        throw new TimeoutException();
                    String answer = msg.getContentDisplay().trim();
                    if (answer.equalsIgnoreCase("quit") || answer.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                        throw new CancellationException("Game quit.");
                    else if (answer.equalsIgnoreCase("pass") || answer.equalsIgnoreCase("ignore") || answer.equalsIgnoreCase("next"))
                        continue;
                    else
                    {
                        boolean correct = trivia.isAnswerCorrect(answer);
                        if (correct)
                        {
                            EmbedBuilder embedBuilder = getEmbedStyle();
                            embedBuilder.setDescription(String.format("Correct! **Cores** are up with **%ds** on the clock!", (challengeTimer.getRemainingCoresTimeMs() / 1000)));
                            channel.sendMessage(embedBuilder.build()).complete();
                            isPlayerTurn = false;
                        }
                    }
                }
                else
                {
                    int questionWordCount = trivia.getQuestion().split(" ").length + trivia.getAnswers().length;
                    float averageHumanWordsPerSecond = 5.0f;
                    int readingTime = Math.round(questionWordCount / averageHumanWordsPerSecond) * 1000; // Milliseconds for the average human to read the question
                    boolean timeout = challengeTimer.getRemainingCoresTimeMs() <= readingTime;
                    if (timeout)
                    {
                        Thread.sleep(challengeTimer.getRemainingCoresTimeMs());
                        throw new TimeoutException();
                    }
                    else
                        Thread.sleep(readingTime);
                    float correctRoll = r.nextFloat();
                    if (correctRoll < coresCorrectChance)
                    {
                        channel.sendMessage(trivia.getCorrectAnswer()).complete();
                        EmbedBuilder embedBuilder = getEmbedStyle();
                        embedBuilder.setDescription(String.format("Correct! **%s** is up with **%ds** on the clock!", player.getEffectiveName(), (challengeTimer.getRemainingPlayerTimeMs() / 1000)));
                        channel.sendMessage(embedBuilder.build()).complete();
                        isPlayerTurn = true;
                    }
                    else
                    {
                        if (correctRoll >= 0.97f)                        // Basically, to appear more "human" the bot passes every 1/33 questions.
                        {
                            channel.sendMessage("Pass.").queue(); // Queued so that the "reading timer" isn't extended by waiting for a rate limit to lift.
                            continue;
                        }
                        else
                        {
                            int answerIndex = r.nextInt(trivia.getAnswers().length);
                            if (answerIndex == trivia.getCorrectAnswerIndex())
                                answerIndex = (answerIndex + 1 >= trivia.getAnswers().length) ? 0 : answerIndex + 1;
                            channel.sendMessage(trivia.getAnswers()[answerIndex]).queue(); // Queued so that the "reading timer" isn't extended by waiting for a rate limit to lift.
                        }
                    }

                }
            }
        }
        catch (TimeoutException e)
        {
            if (!isPlayerTurn)
                channel.sendMessage(getEmbedStyle().setDescription(String.format("The Cores have run out of time!\n**%s** has won **%s%d**!", player.getEffectiveName(), POUND_SIGN, offer.getPrizeCash())).build()).queue();
            else
                channel.sendMessage(getEmbedStyle().setDescription(String.format("%s has run out of time!\n**The Cores** are the winners!", player.getEffectiveName())).build()).queue();
            return !isPlayerTurn;
        }
    }

    private void sendOffers(CoreChallengeOffer[] challengeOffers)
    {
        EmbedBuilder embedBuilder = getEmbedStyle();
        StringBuilder descBuilder = embedBuilder.getDescriptionBuilder();
        descBuilder.append("The Cores have processed your ability and calculated their offers. The larger advantage they get, the more cash is at stake.\nSelect an offer! (E.g Offer 1)\n\n- Time  -  Prize  -  Cores -\n");
        for (int i = 0; i < challengeOffers.length; i++)
        {
            CoreChallengeOffer offer = challengeOffers[i];
            descBuilder.append("#").append((i + 1)).append(": ").append(offer.getCoreTime()).append("s  -  ").append(POUND_SIGN).append(offer.getPrizeCash()).append("  -  ");
            for (int j = 0; j < offer.getCoreCount(); j++)
            {
                descBuilder.append(":blue_square:   ");
            }
            descBuilder.append("\n");
        }
        channel.sendMessage(embedBuilder.build()).queue();
    }

    private void endGame(boolean voluntary)
    {
        if (voluntary)
            channel.sendMessage(getEmbedStyle().setDescription("Game cancelled!").build()).queue();
        gameOver = true;
    }


    private MessageEmbed getWelcomeEmbed()
    {
        EmbedBuilder embed = getEmbedStyle();
        embed.setDescription("Welcome to Beat the Cores! Think you can take on multiple pieces of the smartest metal in the world, "+player.getEffectiveName()+"?\n\nTo start with, you're going to need some cash to bet against the Cores, so let's kickstart with some multiple choice questions. Get the first one wrong though, and you're out.");
        return embed.build();
    }

    private EmbedBuilder getEmbedStyle()
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Beat The Cores!");
        embed.setColor(Color.RED);
        return embed;
    }
}
