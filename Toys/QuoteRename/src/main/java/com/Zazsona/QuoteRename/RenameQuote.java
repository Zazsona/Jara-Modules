package com.Zazsona.QuoteRename;

import commands.CmdUtil;
import module.Command;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;


public class RenameQuote extends Command
{

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length >= 3)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            FileManager fm = new FileManager(msgEvent.getGuild().getId());
            ArrayList<Quote> foundQuotes = new ArrayList<>();
            for (int i = 2; i<parameters.length; i++)
            {
                Quote potentialQuote = fm.getQuoteByName(parameters[1]);
                if (potentialQuote != null)
                {
                    foundQuotes.add(potentialQuote);
                }
                parameters[1] += " "+parameters[i]; //Yes, this skips checking the last parameter as a quote... But we have to have *something* to rename it to, anyway.
            }

            Quote quoteToRename = resolveQuote(msgEvent.getChannel(), msgEvent.getMember(), embed, foundQuotes);
            if (quoteToRename != null)
            {
                int startIndex = quoteToRename.name.length();
                String newName = parameters[1].substring(startIndex).trim();       //This way we can preserve case.
                if (newName.length() > 35)
                {
                    embed.setDescription("That name is too long. Limit: 35 characters.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                    return;
                }
                if (fm.isQuoteNameTaken(newName))
                {
                    embed.setDescription("That name has already been taken.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                    return;
                }
                fm.renameQuote(msgEvent.getGuild().getId(), quoteToRename.name, newName);
                embed.setDescription("Successfully renamed quote to "+newName);
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }

    private Quote resolveQuote(TextChannel channel, Member member, EmbedBuilder embed, ArrayList<Quote> quotes)
    {
        if (quotes.size() == 0)
        {
            embed.setDescription("There is no quote with that name.");
            channel.sendMessage(embed.build()).queue();
            return null;
        }
        else if (quotes.size() == 1)
        {
            return quotes.get(0);
        }
        else
        {
            StringBuilder descBuilder = new StringBuilder();
            descBuilder.append("Found multiple matching quotes. Which would you like to rename?\n\n");
            for (int i = 0; i<quotes.size(); i++)
            {
                descBuilder.append((i+1)).append(". ").append(quotes.get(i).name).append("\n");
            }
            embed.setDescription(descBuilder.toString());
            channel.sendMessage(embed.build()).queue();

            Message message = null;
            MessageManager mm = new MessageManager();
            int selection = -1;
            while (selection < 1 || selection > quotes.size()+1)
            {
                while (message == null || (!message.getMember().equals(member)) || (message.getMember().equals(member) && !message.getContentDisplay().matches("[0-9]+")))
                {
                    message = mm.getNextMessage(channel);
                    if (message.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                    {
                        embed.setDescription("Renaming quit.");
                        return null;
                    }
                }
                selection = Integer.parseInt(message.getContentDisplay());
            }
            return quotes.get(selection-1);
        }
    }
}
