import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class Timecard extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        String[] timecards = { "https://i.imgur.com/gbr4PX7.jpg", "https://i.imgur.com/eMY68EA.jpg", "https://i.imgur.com/56ImsWx.jpg", "https://i.imgur.com/cSO4fyZ.jpg", "https://i.imgur.com/h2ZdXmH.jpg", "https://i.imgur.com/E5P4sjN.png", "https://i.imgur.com/tdzBeBo.png", "https://i.imgur.com/FN6DGu4.png", "https://i.imgur.com/qPE1wml.png", "https://i.imgur.com/LjTg2Ku.png", "https://i.imgur.com/RgC58pd.png", "https://i.imgur.com/Qtaarg4.png", "https://i.imgur.com/mzIBlGV.png", "https://i.imgur.com/4thuFxP.png", "https://i.imgur.com/y8au5B1.png", "https://i.imgur.com/lTaiMb5.png", "https://i.imgur.com/CnOTz7m.png", "https://i.imgur.com/msXRJXm.png", "https://i.imgur.com/U18zgIy.png", "https://i.imgur.com/3ZiNFGl.png", "https://i.imgur.com/ZXjXy9P.jpg", "https://i.imgur.com/GdrUgch.jpg", "https://i.imgur.com/d8Eos9Z.png"};
        Random r = new Random();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setImage(timecards[r.nextInt(timecards.length)]);
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
