package com.Zazsona.Jokes;

import module.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Jokes extends Command
{
    public static final String NSFW_FILTER_KEY = "com.Zazsona.Jokes.NSFWFilter";

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        ArrayList<String> jokes = new ArrayList<>();
        jokes.addAll(getSFWJokes());
        if (msgEvent.getChannel().isNSFW() || !JokesConfig.isNSFWFilterEnabled(msgEvent.getGuild().getId()))
        {
            jokes.addAll(getNSFWJokes());
        }
        Random r = new Random();
        msgEvent.getChannel().sendMessage(jokes.get(r.nextInt(jokes.size()))).queue();
    }


    private Collection<? extends String> getSFWJokes()
    {
        ArrayList<String> jokes = new ArrayList<>();
        jokes.add("If you understand English, press 1. If you do not understand English, press 2.");
        jokes.add("I'm employed at a computer security company and have a colleague whose name is M. Alware." + "\n" + "His e-mail address is malware@company.com.");
        jokes.add("I put so much more effort into naming my first Wi-Fi than my first child.");
        jokes.add("The cool part about naming your kid is you don't have to add six numbers to make sure the name is available.");
        jokes.add("What's the Boogeyman's part time job?" + "\n" + "BoogeyNAN!");
        jokes.add("I hate audio correct.");
        jokes.add("My girlfriend and I often laugh about how competitive we are." + "\n" + "But I laugh more.");
        jokes.add("Q: What kind of exercise do lazy people do?" + "\n" + "A: Diddly-squats.");
        jokes.add("What's the difference between a hippo and a Zippo? Ones really heavy and the others a little lighter.");
        jokes.add("Why did the chicken cross the road?" + "\n" + "Hey, you keep coming up with new material.");
        jokes.add("There are these two beautiful marble statues on either side of a big open piazza. For centuries they have stood frozen, starring longingly into each other's eyes." + "\n" + "One day the gods look down upon them with pity and decide to grant them one hour of mortal life. The statues, overwhelmed with joy, rush across the square and into each others arms and immediately run off into a bush to fulfill their greatest desires." + "\n" + "After about a half an hour of rustling around in the bushes they emerge, panting and sweaty." + "\n" + "\"Wow\" says the one statue, \"that was amazing\" \"A dream come true\" says the other \"but we've got a half an hour left, what should we do now?\" \"I know\" the first responds \"this time I'll hold the pigeon down while you poo on it\"");
        jokes.add("A farmer and his dog are herding sheep. They finish and his dog says \"I counted 40 sheep\". The farmer replies, \"That's odd I only got 37\". The dog replies \"I rounded them up\".");
        jokes.add("Three engineers are on a road trip, a mechanical engineer, and electrical engineer, and a software engineer. They pull over at a restaurant to eat and when they try to leave, the car won't start." + "\n" + "The mechanical engineer says \"Let me check the starter.\"" + "\n" + "The electrical engineer says \"Let me make sure the battery is connected. Always try the easiest solutions first.\"" + "\n" + "The software engineer rolls his eyes and says \"Before we do anything else, let's all just get out of the car and then get back in.\"");
        jokes.add("A Spanish magician says he will disappear on the count of 3. He says \"uno, dos...\" poof. He disappeared without a tres.");
        jokes.add("A man walks into a library and asked if they had any books about paranoia." + "\n" + "Librarian: \"They're right behind you!\".");
        jokes.add("I once bought some used paint. It was in the shape of a house.");
        jokes.add("The other day, my wife asked me to pass her lipstick but I accidentally passed her a glue stick. She still isn't talking to me.");
        jokes.add("I've been told I'm condescending." + "\n" + "(that means I talk down to people)");
        jokes.add("When you look really closely, all mirrors look like eyeballs.");
        jokes.add("Guy walks into a bar and orders a fruit punch." + "\n" + "Bartender says \"Pal, if you want a punch you'll have to stand in line\"" + "\n" + "Guy looks around, but there is no punch line.");
        jokes.add("What do you get when you cross a cow and an octopus?" + "\n" + "A call from the ethics committee and immediate revocation of your grant funding.");
        jokes.add("What's the difference between a good joke and a bad joke timing.");
        jokes.add("Why don't ants get sick?" + "\n" + "Because they have little antybodies.");
        jokes.add("Before your criticize someone, walk a mile in their shoes. That way, when you do criticize them, you're a mile away and have their shoes.");
        jokes.add("My friend says to me: \"What rhymes with orange\"" + "\n" + "I said: \"No, it doesn't.\"");
        jokes.add("I went bobsleighing the other day, took out 250 bobs");
        jokes.add("What did the pirate say when he turned 80 years old?" + "\n" + "Aye matey!");
        jokes.add("My husband told me I had to stop acting like a flamingo. So I had to put my foot down.");
        jokes.add("I couldn't figure out why the baseball kept getting larger. Then it hit me.");
        jokes.add("This is my step ladder. I never knew my real ladder.");
        jokes.add("I bought my friend an elephant for his room." + "\n" + "He said: Thanks." + "\n" + "I said: Don't mention it.");
        jokes.add("A man walks into a bar with a giraffe. They both get pissed. The giraffe falls over. The man goes to leave and the bartender says, \"Oi. You can't leave that lyin' there.\" And the man says, \"No. It's not a lion. It's a giraffe.\"");
        jokes.add("Did you hear about the antennas that got married? The ceremony was okay but the reception was amazing.");
        jokes.add("Two fish are in a tank. One looks to the other and says, \"You man the guns, I'll drive!\"");
        jokes.add("What's the difference between a well dressed man on a unicycle and a poorly dressed man on a bicycle?" + "\n" + "Attire");
        jokes.add("An **infinite** number of mathematicians walk into a bar. Bartender asks, \"what can I get you guys\"?" + "\n" + "First mathematician says \"I'd like a pint of beer please.\" The second one says \"I just want a half pint.\" The third one says \"a quarter pint\". The bartender listens for a while and then stops them." + "\n" + "He pours out two pints and says \"you know, you guys really ought to know your limits.\"");
        jokes.add("Bees?");
        jokes.add("Oh hey, I have a great knock knock joke, but you have to start it.");
        jokes.add("Did you know diarrhea is hereditary? It runs through your jeans.");
        jokes.add("What did the cannibal do after he dumped his girlfriend?" + "\n" + "Wiped his arse.");
        jokes.add("My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away.");
        jokes.add("In a boomerang shop: \"I'd like to buy a new boomerang please. Also, can you tell me how to throw the old one away?\"");
        jokes.add("I can't believe I forgot to go to the gym today. That's 7 years in a row now.");
        jokes.add("I thought I'd tell you a good time travel joke, but you didn't like it.");
        jokes.add("I heard a report about a bad outbreak of the tummy bug, apparently 9 out of 10 people there suffered from diarrhea. I can't stop thinking about that tenth person who apparently enjoyed it.");
        jokes.add("They threw me out of the cinema today for bringing my own food. But, come on, the prices are way too high, plus I haven't had a BBQ in months.");
        jokes.add("Why haven't you ever seen any elephants hiding up trees? Because they're really, really good at it.");
        jokes.add("We have a strange custom in our office. The food has names there. Yesterday for example I got me a sandwich out of the fridge and its name was \"Michael\".");
        jokes.add("I really don't know which kid I'm supposedly being unfair to, according to my wife. Thomas, Anton, or the fat, ugly one?");
        jokes.add("Why did the physics teacher break up with the biology teacher? There was no chemistry.");
        jokes.add("I got another letter from this lawyer today. It said 'Final Notice'. Good that he will not bother me anymore.");
        jokes.add("Don't be sad when a bird craps on your head. Be happy that dogs can't fly.");
        jokes.add("I went on a date with a chess player to an Italian restaurant. With checkered table cloths. It took him maybe half an hour to pass the salt.");
        jokes.add("I'll never buy a vacuum cleaner. It would only just gather dust.");
        jokes.add("Time is money. Therefore, ATMs are time machines.");
        jokes.add("Tomato is a fruit, right? Does that make ketchup a smoothie?");
        jokes.add("I dated a tennis player but I'll never make such a mistake again. Love has zero meaning to them.");
        jokes.add("How many believers in telekinesis here? Raise that guy's hand.");
        jokes.add("Artificial intelligence is very impressive but it's got nothing on natural idiocy.");
        jokes.add("I got Pavlov in my exam but I couldn't remember who that that dude was. His name really didn't ring a bell.");
        jokes.add("My friend wrote a book on poltergeists, it's simply flying off the shelves.");
        jokes.add("The stationary shop moved. It really surprised me.");
        jokes.add("'My career is in ruins.' ~Herbert Dillgrin, archaeologist");
        jokes.add("I'm a pro at sleeping. I could do it with my eyes closed.");
        jokes.add("Apple just posted on their Facebook page that their store got robbed. They're looking for iWitnesses.");
        jokes.add("Here, I bought you a calendar. Your days are numbered now.");
        jokes.add("Talk is cheap, yeah? Have you ever talked to a lawyer?!");
        jokes.add("When everything's coming your way, perhaps you're in the wrong direction on the motorway?");
        jokes.add("If I got 50 pence for every failed maths exam, I'd have £6.30 now.");
        jokes.add("Do I lose when the police officer says papers and I say scissors?");
        jokes.add("Famous last words of a postman: What a lovely dog you have!");
        jokes.add("It's cleaning day today. I've already polished off a whole chocolate bar.");
        jokes.add("You look so bad your mum got a penalty for littering when she dropped you off at school today.");
        jokes.add("I hate it when I run out of toilet paper and I have to make the trip to the grocery store in really small steps.");
        jokes.add("A cannibal came home late to family dinner. He got the cold shoulder.");
        jokes.add("A magician was driving down the road and turned into a shopping mall.");
        jokes.add("I told my girlfriend she drew her eyebrows too high. She seemed surprised.");
        jokes.add("And God said to John, come forth and you shall be granted eternal life." + "\n" + "But John came fifth and won a toaster.");
        jokes.add("What do you get when you cross the Atlantic Ocean with the Titanic?\nHalfway.");
        jokes.add("How do you get idiots out of a tree?\nWave to them.");
        return jokes;
    }

    private Collection<? extends String> getNSFWJokes()
    {
        ArrayList<String> jokes = new ArrayList<>();
        jokes.add("Did you hear about the guy who ran in front of the bus?\nHe got tired.");
        jokes.add("What's worse than spiders on your piano?\nCrabs on your organ.");
        jokes.add("What kind of bees produce milk?\nBoobies.");
        jokes.add("Why does Santa Claus have such a big sack?\nHe only comes once a year.");
        jokes.add("What's the difference between oral and anal sex?\nOral sex makes your day. Anal sex makes your hole weak.");
        jokes.add("What do you call a herd of cows masturbating?\nBeef strokin' off.");
        return jokes;
    }
}
