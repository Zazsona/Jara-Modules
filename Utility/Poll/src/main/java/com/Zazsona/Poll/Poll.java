package com.Zazsona.Poll;

import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Poll extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        parameters = checkForCommaSeparation(parameters);
        if (parameters.length > 21)
        {
            msgEvent.getChannel().sendMessage("ERROR: Limit of 20 options.").queue();
        }
        else
        {
            byte asciiCode = 65; //The ascii code for "A"
            StringBuilder descBuilder = new StringBuilder();
            for (String option : parameters)
            {
                if (!option.equals(parameters[0]))
                    descBuilder.append(((char) asciiCode++)).append(". ").append(option).append("\n");
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("====================");
            embed.setDescription("**Poll**\n"+descBuilder.toString()+"====================");
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            Message msg = msgEvent.getChannel().sendMessage(embed.build()).complete();
            String[] reactions = {"\uD83C\uDDE6", "\uD83C\uDDE7",  "\uD83C\uDDE8",  "\uD83C\uDDE9",  "\uD83C\uDDEA",   "\uD83C\uDDEB",   "\uD83C\uDDEC",   "\uD83C\uDDED",   "\uD83C\uDDEE",  "\uD83C\uDDEF",   "\uD83C\uDDF0",   "\uD83C\uDDF1",   "\uD83C\uDDF2",   "\uD83C\uDDF3",   "\uD83C\uDDF4",  "\uD83C\uDDF5",  "\uD83C\uDDF6",  "\uD83C\uDDF7",  "\uD83C\uDDF8",  "\uD83C\uDDF9",  "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE",  "\uD83C\uDDFF"};
            for (int i = 1; i<parameters.length; i++)
            {
                msg.addReaction(reactions[i-1]).queue();
            }
        }
    }


    private String[] checkForCommaSeparation(String[] parameters)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parameters[0]+","); //Separation for the command call.
        for (int i = 1; i<parameters.length; i++)
        {
            stringBuilder.append(parameters[i]).append(" ");
        }
        String[] list = stringBuilder.toString().split("[,]");
        if (list.length > 2)
        {
            return list;
        }
        else
        {
            return parameters;
        }
    }
}
