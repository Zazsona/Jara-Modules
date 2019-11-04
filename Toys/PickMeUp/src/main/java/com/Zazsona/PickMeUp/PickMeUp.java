package com.Zazsona.PickMeUp;

import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class PickMeUp extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        /*

            Definitely need some more pictures here. Feel free to add some/your own.
            The format is URL, Author, Author URL (null if none available)

         */
        String[][] sources = { {"https://i.redd.it/un44882dvyj21.jpg", "Jules", "https://www.reddit.com/user/julcarls"},
                {"https://i.redd.it/g9w2q9iq3ok11.jpg", "YourWebCam", "https://www.reddit.com/user/YourWebCam"},
                {"https://i.redd.it/b7s8awb1zkl01.jpg", "LearnedBravery", "https://www.reddit.com/user/LearnedBravery"},
                {"https://i.redd.it/6h5137qf89d01.jpg", "KINGK7", "https://www.reddit.com/user/KINGK7"},
                {"https://i.imgur.com/dc1PU8j.jpg", "", null},
                {"https://i.imgur.com/u4qSjQL.jpg", "", null},
                {"https://i.imgur.com/r1L3ZR9.jpg", "", null},
                {"https://i.imgur.com/0cptOAb.jpg", "", null},
                {"https://i.redd.it/j5b8uhulp2z21.png", "Ahm3d143", "https://old.reddit.com/user/Ahm3d143"},
                {"https://i.redd.it/1ixi0z9qe3z21.jpg", "", null},
                {"https://i.redd.it/3hyt47cf3bu21.jpg", "Vechrotex", "https://www.reddit.com/user/Vechrotex"},
                {"https://i.redd.it/7wg0x9j7fsu21.png", "Hiken-Geos", "https://www.reddit.com/user/Hiken-Geos"},
                {"https://i.redd.it/lktk9s2jtnx21.jpg", "", null},
                {"https://i.imgur.com/MK4q6gg.jpg", "TheLifeOfMoz", "https://imgur.com/user/thelifeofmoz"},
                {"https://i.imgur.com/qNG7gow.jpg", "ColourMeAngel", "https://imgur.com/user/ColourMeAngel"},
                {"https://i.imgur.com/NwiSSNV.jpg", "ThorPom", "https://imgur.com/user/ThorPom"},
                {"https://i.imgur.com/9fpLFP7.jpg", "", null},
                {"https://i.imgur.com/onbEsVA.jpg", "", null},
                {"https://i.imgur.com/vfUqTz8.jpg", "", null},
                {"https://i.imgur.com/mwyZvPT.jpg", "", null},
                {"https://i.imgur.com/69SUs49.jpg", "Cloudgazing", "https://www.reddit.com/user/cloudgazing"},
                {"https://i.imgur.com/kMT9xpn.jpg", "Jodifer", "https://imgur.com/user/jodifer"},
                {"https://i.imgur.com/sjpa0Gg.jpg", "", null},
                {"https://i.imgur.com/r31yNMx.jpg", "", null},
                {"http://i.imgur.com/IPzcrc5.jpg", "Akiro27", "https://www.reddit.com/user/akiro27"},
                {"https://i.redd.it/j2eau19a95m31.jpg", "", null},
                {"https://i.imgur.com/d3phupW.jpg", "", null},
                {"https://i.imgur.com/bMgTimk.jpg", "serenalese", "https://imgur.com/user/serenalese"},
                {"https://i.imgur.com/9lnXPt7.jpg", "ModifiedMonkey", "https://imgur.com/user/modifiedmonkey"},
                {"https://i.imgur.com/ETgEVlN.jpg", "", null},
                {"https://i.imgur.com/kfDaOzt.jpg", "LaurieLoves321", "https://imgur.com/user/LaurieLoves321"},
                {"https://i.imgur.com/8R3xsHa.jpg", "RaxTheHusky", "https://imgur.com/user/raxthehusky"},
                {"https://i.imgur.com/lHjqXyv.jpg", "RaxTheHusky", "https://imgur.com/user/raxthehusky"},
                {"https://i.imgur.com/3dR4uQl.jpg", "", null},
                {"https://i.imgur.com/rQi8RXu.jpg", "", null},
                {"https://i.imgur.com/S7eP6Rs.jpg", "DoxieLoves", null},
                {"https://i.imgur.com/VudkQYZ.jpg", "DoxieLoves", null},
                {"https://i.imgur.com/X9bgIgn.jpg", "", null},
                {"https://i.imgur.com/4oHUiR2.jpg", "", null},
                {"https://i.imgur.com/yvZJXQA.jpg", "DankMemesMakeGreatDreams", "https://imgur.com/user/DankMemesMakeGreatDreams"},
                {"https://i.imgur.com/6BXK5Dw.jpg", "Somilasomi", "https://imgur.com/user/Somilasomi"},
                {"https://i.imgur.com/3FUXw7T.jpg", "itsmesofia", "https://imgur.com/user/itsmesofia"},
                {"https://i.imgur.com/wd0GeLZ.jpg", "memorialfield", "https://imgur.com/user/memorialfield"}};
        Random r = new Random();

        int selection = r.nextInt(sources.length);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setImage(sources[selection][0]);
        if (!sources[selection][1].equals(""))
            embed.setAuthor("Source: "+sources[selection][1], sources[selection][2]);
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
