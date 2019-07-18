import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import java.util.List;

public class Say extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            TextChannel channel = getChannel(msgEvent.getMessage());
            StringBuilder message = new StringBuilder();
            for (int i = 1; i<parameters.length; i++)
            {
                message.append(parameters[i]).append(" ");
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription(message.toString());
            channel.sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }

    private TextChannel getChannel(Message message)
    {
        List<TextChannel> channels = message.getMentionedChannels();
        if (channels.size() > 0)
        {
            return channels.get(0);
        }
        else if (message.getContentDisplay().length() > 2)
        {
            String channelName = message.getContentDisplay().split(" ")[2];
            for (TextChannel channel : message.getGuild().getTextChannels())
            {
                if (channel.getName().equalsIgnoreCase(channelName))
                {
                    return channel;
                }
            }
        }
        return message.getTextChannel();
    }
}
