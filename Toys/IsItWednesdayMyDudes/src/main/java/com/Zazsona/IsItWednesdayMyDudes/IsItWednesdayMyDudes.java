package com.Zazsona.IsItWednesdayMyDudes;

import configuration.SettingsUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;

public class IsItWednesdayMyDudes extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        ZonedDateTime guildTime = SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).getZonedDateTime();
        if (guildTime.getDayOfWeek().getValue() == 3)
        {
            msgEvent.getChannel().sendMessage("https://i.imgur.com/n7I7cKp.jpg").complete();
        }
        else
        {
            msgEvent.getChannel().sendMessage("https://i.imgur.com/FLXswHO.jpg").complete();
        }
    }
}
