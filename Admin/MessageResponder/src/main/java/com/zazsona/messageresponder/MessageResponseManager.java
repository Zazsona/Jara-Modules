package com.zazsona.messageresponder;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

public class MessageResponseManager extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 1)
            {
                FileManager fm = MessageResponder.getFileManager();
                switch (parameters[1].toLowerCase())
                {
                    case "add":
                        addMessageResponse(fm, msgEvent.getChannel(), msgEvent.getMember());
                        break;
                    case "delete":
                    case "remove":
                        deleteMessageResponse(fm, msgEvent.getChannel(), msgEvent.getMember());
                        break;
                    case "list":
                        if (parameters.length > 2 && parameters[2].matches("[0-9]+"))
                        {
                            int pageNo = Integer.parseInt(parameters[2]);
                            pageNo = (pageNo == 0) ? 1 : pageNo;
                            listMessageResponses(fm, msgEvent.getChannel(), pageNo);
                        }
                        else
                        {
                            listMessageResponses(fm, msgEvent.getChannel(), 1);
                        }
                        break;
                }
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
            }
        }
        catch (CancellationException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription(e.getLocalizedMessage());
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }

    private void addMessageResponse(FileManager fm, TextChannel channel, Member member) throws CancellationException
    {
        String identifyMessage = null;
        String responseMessage = null;
        MessageManager mm = new MessageManager();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.setDescription("Please enter the message to identify. (Case insensitive)");
        channel.sendMessage(embed.build()).queue();

        Message msg = getMessage(channel, member, mm);
        identifyMessage = msg.getContentRaw();

        embed.setDescription("Please enter the message to respond with.");
        channel.sendMessage(embed.build()).queue();
        msg = getMessage(channel, member, mm);
        responseMessage = msg.getContentRaw();
        fm.addMessageResponse(channel.getGuild().getId(), identifyMessage, responseMessage);
        String identifyMessageSnippet = (identifyMessage.length() > 500) ? identifyMessage.substring(0, 497)+"..." : identifyMessage;
        String responseMessageSnippet = (responseMessage.length() > 500) ? responseMessage.substring(0, 497)+"..." : responseMessage;
        embed.setDescription("Response saved successfully!\n\n**Look for**:\n"+identifyMessageSnippet+"\n\n**Respond with**:\n"+responseMessageSnippet);
        channel.sendMessage(embed.build()).queue();
    }

    private Message getMessage(TextChannel channel, Member member, MessageManager mm) throws CancellationException
    {
        Message msg = null;
        boolean validMessageFound;
        validMessageFound = false;
        while (!validMessageFound)
        {
            msg = mm.getNextMessage(channel);
            validMessageFound = (msg.getMember().equals(member));
        }
        if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
        {
            throw new CancellationException("Operation has been quit.");
        }
        return msg;
    }

    private void listMessageResponses(FileManager fm, TextChannel channel, int pageNo)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        if (fm.doesGuildHaveResponses(channel.getGuild().getId()))
        {
            HashMap<String, String> responsesMap = fm.getMessageResponses(channel.getGuild().getId());
            ArrayList<String> keys = new ArrayList<>();
            keys.addAll(responsesMap.keySet());

            int listingsPerPage = 15;
            double totalPages = Math.ceil((double) keys.size()/(double) listingsPerPage);
            int startingIndex = (listingsPerPage*pageNo)-listingsPerPage;
            int endIndex = (keys.size() < startingIndex+listingsPerPage) ? keys.size() : (startingIndex+listingsPerPage);

            StringBuilder listBuilder = new StringBuilder();
            for (int i = startingIndex; i<endIndex; i++)
            {
                String key = keys.get(i);
                String response = responsesMap.get(key);
                String keySnippet = (key.length() > 20) ? key.substring(0, 20) : key;
                String responseSnippet = (response.length() > 20) ? response.substring(0, 20) : response;
                listBuilder.append(i).append(". ").append(keySnippet).append("\n").append("*").append(responseSnippet).append("*\n\n");
            }
            embed.setDescription(listBuilder.toString());
            embed.setFooter("Page "+pageNo+" / "+(int) totalPages, null);
        }
        else
        {
            embed.setDescription("You have no responses!");
        }
        channel.sendMessage(embed.build()).queue();
    }

    private void deleteMessageResponse(FileManager fm, TextChannel channel, Member member) throws CancellationException
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        if (fm.doesGuildHaveResponses(channel.getGuild().getId()))
        {
            try
            {
                embed.setDescription("Please enter a response ID to delete. (IDs are shown in the response list).");
                channel.sendMessage(embed.build()).queue();

                MessageManager mm = new MessageManager();
                Message msg = getMessage(channel, member, mm);
                int responseNo = Integer.parseInt(msg.getContentDisplay());

                ArrayList<String> keys = new ArrayList<>();
                keys.addAll(fm.getMessageResponses(channel.getGuild().getId()).keySet());
                String key = null;
                for (int i = 0; i<keys.size(); i++)
                {
                    if (i != responseNo)
                    {
                        continue;
                    }
                    else
                    {
                        key = keys.get(i);
                    }
                }
                if (key != null)
                {
                    fm.removeMessageResponse(channel.getGuild().getId(), key);
                    embed.setDescription("Response deleted.");
                    channel.sendMessage(embed.build()).queue();
                }
                else
                {
                    embed.setDescription("There is no message response with that ID.");
                    channel.sendMessage(embed.build()).queue();
                }
            }
            catch (NumberFormatException e)
            {
                embed.setDescription("Invalid ID. IDs are in a numeric format, and are listed in the response list.");
                channel.sendMessage(embed.build()).queue();
            }
        }
        else
        {
            embed.setDescription("You have no responses!");
            channel.sendMessage(embed.build()).queue();
        }

    }
}
