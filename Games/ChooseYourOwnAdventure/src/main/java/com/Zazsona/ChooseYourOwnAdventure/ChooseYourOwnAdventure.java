package com.Zazsona.ChooseYourOwnAdventure;

import com.Zazsona.ChooseYourOwnAdventure.profile.ProfileManager;
import com.Zazsona.ChooseYourOwnAdventure.story.StoryManager;
import com.Zazsona.ChooseYourOwnAdventure.story.StoryNode;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class ChooseYourOwnAdventure extends ModuleGameCommand
{
    private boolean isLastSelectionOptionA;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        TextChannel channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Tale");
        Member player = msgEvent.getMember();

        StoryNode currentNode = restoreProgress(player.getUser(), channel);
        StoryNode lastNode = currentNode;
        MessageManager mm = new MessageManager();
        while (currentNode != null)
        {
            EmbedBuilder embed = getEmbedStyle(msgEvent.getGuild().getSelfMember());
            embed.setDescription(currentNode.getScenario()+"\n\n**A.** *"+currentNode.getOptionAText()+"*\n**B.** *"+currentNode.getOptionBText()+"*");
            channel.sendMessage(embed.build()).queue();

            lastNode = currentNode;
            while (currentNode.equals(lastNode))
            {
                Message msg = mm.getNextMessage(channel);
                if (msg.getMember().equals(player))
                {
                    currentNode = getNextNode(currentNode, msg.getContentDisplay());
                    if (currentNode == null || msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msg.getGuild().getId())+"quit"))
                    {
                        end(player, currentNode, lastNode, channel, mm);
                        return;
                    }

                }

            }

        }
    }

    private void end(Member player, StoryNode currentNode, StoryNode lastNode, TextChannel channel, MessageManager mm)
    {
        if (currentNode == null)
        {
            channel.sendMessage(getStoryEnd(channel.getGuild().getSelfMember()).build()).queue();
            ProfileManager.removeProfile(player.getUser().getId());
            Message confirmMsg = null;
            while (confirmMsg == null || !confirmMsg.getMember().equals(player))
            {
                confirmMsg = mm.getNextMessage(channel);
            }
            if (confirmMsg.getContentDisplay().toLowerCase().equals("y"))
            {
                boolean success = addNewStoryNode(mm, channel, player, lastNode, isLastSelectionOptionA);
                if (success)
                    channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Page added successfully!").build()).queue();
                else
                    channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Page addition cancelled!").build()).queue();
            }
        }
        else
        {
            ProfileManager.addProfile(player.getUser().getId(), currentNode.getNodeID());
            channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Progress saved. You can pick up from here next time!").build()).queue();
        }
        deleteGameChannel();
    }

    private EmbedBuilder getEmbedStyle(Member selfMember)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("=== :crossed_swords: Choose Your Own Adventure :shield: ===");
        embed.setColor(CmdUtil.getHighlightColour(selfMember));
        return embed;
    }

    private StoryNode restoreProgress(User player, TextChannel channel)
    {
        int nodeID = ProfileManager.getProfileProgress(player.getId());
        if (nodeID != 0)
        {
            channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Continuing from where you left off...").build()).queue();
        }
        return StoryManager.getStoryNode(nodeID);
    }

    private StoryNode getNextNode(StoryNode node, String messageContent)
    {
        messageContent = messageContent.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        if (messageContent.equals("a") || messageContent.equalsIgnoreCase(node.getOptionAText().toLowerCase().replaceAll("[^a-z0-9 ]", "")))
        {
            isLastSelectionOptionA = true;
            return node.getOptionANode();
        }
        else if (messageContent.equals("b") || messageContent.equalsIgnoreCase(node.getOptionBText().toLowerCase().replaceAll("[^a-z0-9 ]", "")))
        {
            isLastSelectionOptionA = false;
            return node.getOptionBNode();
        }
        return node;
    }

    private EmbedBuilder getStoryEnd(Member selfMember)
    {
        String[] storyEnds = {"Suddenly, everyone spontaneously combusted! The end.",
                "Suddenly, your mate's cousin who works for Nintendo shows up and bans everyone. The end.",
                "Everyone steps on a Lego and dies. The end.",
                "The end.",
                "Turns out it was all a simulation. How inconvenient. The end.",
                "Suddenly, I, "+selfMember.getEffectiveName()+", show up and go all pew pew on everyone with lasers! The end.",
                "Th-th-th-that's all, folks!",
                "Everyone makes up and lives happily ever after. The end."
        };
        Random r = new Random();
        EmbedBuilder embed = getEmbedStyle(selfMember);
        embed.setDescription("**"+storyEnds[r.nextInt(storyEnds.length)]+"**\n\nWould you like to add the next page? (Y/N)");
        return embed;
    }

    private boolean addNewStoryNode(MessageManager mm, TextChannel channel, Member player, StoryNode lastNode, boolean isOptionA)
    {
        String scenario = "";
        String optionA = "";
        String optionB = "";
        channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Please enter a follow-up scenario (E.g, X does Y, what do you do?).\nUse "+SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit to cancel at any time.").build()).queue();
        while (scenario.equals(""))
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getMember().equals(player))
            {
                String messageContent = msg.getContentDisplay().trim();
                if (messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                        return false;
                if (messageContent.length() <= 300)
                {
                    scenario = messageContent;
                }
                else
                {
                    channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("That scenario is too long. Keep it under 300 characters.").build()).queue();
                }
            }
        }
        channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Scenario set! Please enter the first action that can be taken (E.g, Do X.).").build()).queue();
        while (optionA.equals(""))
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getMember().equals(player))
            {
                String messageContent = msg.getContentDisplay().trim();
                if (messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId()) + "quit"))
                    return false;
                if (validateNewOptionInput(messageContent, channel))
                {
                    if (messageContent.toLowerCase().startsWith("a."))
                        messageContent = messageContent.substring(2).trim();
                    optionA = messageContent;
                }
            }
        }
        channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Option A set! Please enter the action for option B (E.g, Do Y.).").build()).queue();
        while (optionB.equals(""))
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getMember().equals(player))
            {
                String messageContent = msg.getContentDisplay().trim();
                if (messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId()) + "quit"))
                    return false;
                if (validateNewOptionInput(messageContent, channel))
                {
                    if (messageContent.toLowerCase().startsWith("b."))
                        messageContent = messageContent.substring(2).trim();
                    optionB = messageContent;
                }
            }
        }
        if (isOptionA)
            lastNode.setOptionANode(new StoryNode(scenario, optionA, optionB));
        else
            lastNode.setOptionBNode(new StoryNode(scenario, optionA, optionB));

        return true;
    }

    private boolean validateNewOptionInput(String messageContent, TextChannel channel)
    {
        String option = messageContent.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        if (messageContent.length() > 200)
        {
            channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("That's too long. Keep it under 200 characters.").build()).queue();
            return false;
        }
        else if (option.equalsIgnoreCase("a") || option.equalsIgnoreCase("b"))
        {
            channel.sendMessage(getEmbedStyle(channel.getGuild().getSelfMember()).setDescription("Very clever. But that's not going to work.").build()).queue();
            return false;
        }
        //We could block matching options here... But honestly, it could be interesting to see people use that to make different branches.
        return true;
    }
}
