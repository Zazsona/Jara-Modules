import commands.CmdUtil;
import commands.GameCommand;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

public class XmasPresent extends GameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (OffsetDateTime.now(ZoneOffset.UTC).getMonthValue() == 12)
        {
            try
            {
                String msgID = msgEvent.getChannel().sendMessage(":gift:").complete().getId();
                String[] presents = {":dog:", ":cat:", ":bouquet:", ":microphone:", ":video_game:", ":8ball:", ":heart:", ":middle_finger:", ":bear:", ":sparkles:", ":lollipop:", ":candy:", ":chocolate_bar:", ":pound:", ":balloon:", ":ribbon:", ":robot:", ":crown:", ":tophat:", ":ring:", ":rabbit:", ":penguin:", ":bouquet:", ":cake:", ":beer:", ":drum:", ":soccer:", ":trumpet:", ":guitar:", ":tennis:"};
                Thread.sleep(1000);
                msgEvent.getChannel().getMessageById(msgID).complete().editMessage(":boom:").queue();
                Random r = new Random();
                msgEvent.getChannel().getMessageById(msgID).complete().editMessage(presents[r.nextInt(presents.length)]).queue();
            }
            catch (InterruptedException e)
            {
                LoggerFactory.getLogger(getClass()).info("Sleep timed out.");
            }
        }
    }

}
