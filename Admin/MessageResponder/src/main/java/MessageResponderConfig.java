import configuration.GuildSettings;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

public class MessageResponderConfig extends ModuleConfig
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        EmbedBuilder embed = this.getDefaultEmbedStyle(msgEvent);
        embed.setDescription("Would you like to enable custom message responses? (Y/N)");
        textChannel.sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        Message message = null;
        while (true)
        {
            try
            {
                while (true)
                {
                    message = mm.getNextMessage(textChannel);
                    if (message.getMember().equals(msgEvent.getMember()))
                    {
                        break;
                    }
                }
                setEnabledState(msgEvent, message.getContentDisplay(), guildSettings, textChannel);
                return;
            }
            catch (InvalidParameterException e)
            {
                embed.setDescription("Invalid response. Yes or No expected.\nPlease try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        if (collection.size() > 0)
        {
            try
            {
                ArrayList<String> list = new ArrayList<>(collection);
                setEnabledState(msgEvent, list.get(0), guildSettings, textChannel);
            }
            catch (InvalidParameterException e)
            {
                EmbedBuilder embed = this.getDefaultEmbedStyle(msgEvent);
                embed.setDescription("Invalid response. Yes or No expected.\nPlease try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
        else
        {
            run(msgEvent, guildSettings, textChannel);
        }
    }

    private void setEnabledState(GuildMessageReceivedEvent msgEvent, String response, GuildSettings guildSettings, TextChannel textChannel) throws InvalidParameterException
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        try
        {
            response = response.toLowerCase();
            if (response.equals("y") || response.equals("enable") || response.equals("yes"))
            {
                guildSettings.setCommandConfiguration(true, null, "MessageResponder");
                embed.setDescription("Custom message responses enabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else if (response.equals("n") || response.equals("disable") || response.equals("no"))
            {
                guildSettings.setCommandConfiguration(false, null, "MessageResponder");
                embed.setDescription("Custom message responses disabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        catch (IOException e)
        {
            embed.setDescription("Unable to save settings.");
            textChannel.sendMessage(embed.build()).queue();
            LoggerFactory.getLogger(getClass()).error(e.toString());
        }

    }
}
