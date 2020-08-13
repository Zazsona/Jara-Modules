package com.Zazsona.LastCommand;

import jara.ModuleAttributes;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HistoricCommand
{
    private GuildMessageReceivedEvent msgEvent;
    private ModuleAttributes attributes;
    private String[] parameters;

    public HistoricCommand(GuildMessageReceivedEvent msgEvent, ModuleAttributes attributes)
    {
        this.msgEvent = msgEvent;
        this.attributes = attributes;
        String msgContent = msgEvent.getMessage().getContentRaw();
        parameters = msgContent.split(" ");
    }

    /**
     * Gets ownerId
     *
     * @return ownerId
     */
    public GuildMessageReceivedEvent getMsgEvent()
    {
        return msgEvent;
    }

    /**
     * Gets attributes
     *
     * @return attributes
     */
    public ModuleAttributes getAttributes()
    {
        return attributes;
    }

    /**
     * Gets parameters
     *
     * @return parameters
     */
    public String[] getParameters()
    {
        return parameters;
    }
}
