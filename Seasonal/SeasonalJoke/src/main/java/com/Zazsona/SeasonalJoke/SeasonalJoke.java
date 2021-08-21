package com.zazsona.seasonaljoke;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public class SeasonalJoke extends ModuleGameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        Random r = new Random();
        if (CmdUtil.getSeason().equals(CmdUtil.Season.SPRING))
        {
            String[] jokes = {"What do you call a rabbit with fleas?\n\n **Bugs bunny**",
                    "What wrong with Easter jokes?\n\n **They crack you up.**",
                    "How does the Easter bunny stay fit?\n\n **Eggs-ercise**",
                    "What do you call a cheeky egg?\n\n **A practical yolker**",
                    "What sport are eggs good at?\n\n **Running**",
                    "What do you call an egg from outer space?\n\n **Weird**",
                    "What music does the Easter bunny like?\n\n **Hip hop**",
                    "What's an egg's least favourite day?\n\n **Fry-day**",
                    "What do you call an egg on a skateboard?\n\n **Eggs-treme**",
                    "How do you place an egg on a spoon for a race?\n\n **Egg-sactly**",
                    "Why did the Easter egg hide?\n\n **He was a little chicken**"};
            embed.setColor(Color.decode("#5de527"));
            embed.setDescription(jokes[r.nextInt(jokes.length)]);
        }
        else if (CmdUtil.getSeason().equals(CmdUtil.Season.SUMMER))
        {
            String[] jokes = {"Why was it so hot in the stadium after the baseball game?\n\n**All the fans left!**",
            "Where do sharks go on summer vacation?\n\n**Finland!**",
            "What do you call six weeks of rain in Scotland?\n\n**Summer!**",
            "How do teddy bears keep their den cool in summer?\n\n**They use bear conditioning!**",
            "What is a math teacher's favorite sum?\n\n**Summer!**",
            "What do you call a snowman in July?\n\n**A puddle.**",
            "What do frogs like to drink on a hot summer day?\n\n**Croak-o-cola.**",
            "What do you call a witch who lives on the beach?\n\n**A sandwitch!**"};
            embed.setColor(Color.decode("#e1ff00"));
            embed.setDescription(jokes[r.nextInt(jokes.length)]);
        }
        else if (CmdUtil.getSeason().equals(CmdUtil.Season.AUTUMN))
        {
            String[] jokes = {"What did the tree say to autumn?\n\n**Leaf me alone.**",
            "How do you fix a broken pumpkin?\n\n**With a pumpkin patch!**",
            "What's the cutest season?\n\n**Awwtumn.**",
            "How to leaves get from place to place?\n\n**With autumn-mobiles.**",
            "If money did grow on trees, Autumn would be the best time of year.",
            "What do ghosts eat?\n\n**Spooketi!**",
            "Why didn't the skeleton join the dance?\n\n**His heart wasn't in it.**",
            "What do you call a fat pumpkin?\n\n**A plumpkin!**",
            "What room goes a ghost not need?\n\n**A living room.**",
            "What kind of dessert to ghosts have?\n\n**I scream!**"};
            embed.setColor(Color.decode("#f77b00"));
            embed.setDescription(jokes[r.nextInt(jokes.length)]);
        }
        else if (CmdUtil.getSeason().equals(CmdUtil.Season.WINTER))
        {
            String[] jokes = {"What do snowmen wear on their heads? **Ice caps**",
                    "What do you get if you cross a bell with a skunk? **Jingle smells**",
                    "Who delivers presents to cats? **Santa Paws**",
                    "What do you call a Christmas tree with a really long nose? **Pineocchio**",
                    "What do monkeys sing at Christmas? **Jungle bells**",
                    "What illness do you get if you eat Christmas decorations? **Tinselitis**",
                    "Which reindeer has the worst manners? **Rudeolph**",
                    "What do you get if you cross a Christmas tree with an apple? **A pineapple**",
                    "What do reindeer hang on their trees at Christmas? **Horn-aments!**",
                    "What do you call an old snowman? **Water.**",
                    "What do you get if you cross Santa with a duck? **Christmas Quackers**",
                    "Who's the king of Christmas? **The Stocking**",
                    "Where does mistle toe go to become famous? **HOLLYwood**",
                    "How many presents can Santa fit in an empty sack? **Only one, 'cause it's not empty after that.**",
                    "What do Santa's little helpers learn at school? **The elf-abet**",
                    "What do snowmen eat for breakfast? **Frosties**",
                    "What do you have in December that you don't have in any other month? **The letter D**",
                    "Why can't penguins fly? **Because even penguins don't trust RyanAir**",
                    "What do you call a blind reindeer? **No-eye deer**",
                    "Who hides in the bakery at Christmas? **A mince spy**",
                    "How do snowmen get around? **By riding an ‘icicle**",
                    "What do you get when you cross a snowman with a vampire? **Frostbite**",
                    "What do you give a dog for Christmas? **A mobile bone**",
                    "Got a Christmas card full of rice today, I think it was from my **Uncle Ben**.",
                    "What goes 'Oh, Oh, Oh'? **Santa walking backwards**",
                    "Why are Christmas trees so bad at sewing? **They always drop their needles**"};
            embed.setColor(Color.decode("#FFFFFF"));
            embed.setDescription(jokes[r.nextInt(jokes.length)]);
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

}
