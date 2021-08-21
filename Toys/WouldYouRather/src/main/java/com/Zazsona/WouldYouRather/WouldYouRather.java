package com.zazsona.wouldyourather;

import com.google.gson.Gson;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WouldYouRather extends ModuleCommand
{
    public static final String NSFW_FILTER_KEY = "com.Zazsona.WouldYouRather.NSFWFilter";

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Gson gson = new Gson();
        TextChannel channel = msgEvent.getChannel();
        String response = CmdUtil.sendHTTPRequest("http://www.rrrather.com/botapi");
        ResponseJSON json = gson.fromJson(response, ResponseJSON.class);

        if (!channel.isNSFW() && WYRConfig.isNSFWFilterEnabled(msgEvent.getGuild().getId()))
        {
            while (json.nsfw)                                                           //We're ensuring that this question is tagged as safe for work ONLY if the channel has NSFW disabled.
            {
                response = CmdUtil.sendHTTPRequest("http://www.rrrather.com/botapi");   //Since we can't request a SFW one specifically, we'll have to brute force for one. As most are SFW, this isn't really an issue.
                json = gson.fromJson(response, ResponseJSON.class);
            }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle(json.title);
        embed.setDescription("A. "+json.choicea+"\nB. "+json.choiceb);
        channel.sendMessage(embed.build()).queue();
    }

    private class ResponseJSON
    {
        String title;
        String choicea;
        String choiceb;
        boolean nsfw;
    }
}
