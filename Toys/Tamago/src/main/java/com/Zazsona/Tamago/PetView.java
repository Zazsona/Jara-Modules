package com.Zazsona.Tamago;

import com.Zazsona.Tamago.petdata.LifeStage;
import com.Zazsona.Tamago.petdata.Pet;
import com.Zazsona.Tamago.petdata.PetType;
import commands.CmdUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Random;

public class PetView
{
    public static MessageEmbed getPetView(Pet pet, Member owner)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("==== "+pet.getPetName()+" ====");
        embed.setDescription(getPetMoodDescription(pet));
        embed.setFooter("Owner: "+owner.getEffectiveName());
        embed.setColor(CmdUtil.getHighlightColour(owner));
        embed.setImage(getPetImage(pet));
        embed.addField("Age", getLifeStagePrettyPrint(pet.getLifeStage()), true);
        embed.addField("Hunger", buildStatBar(pet.getHungerPercentage()), true);
        embed.addField("Happiness", buildStatBar(pet.getHappinessPercentage()), true);
        embed.addField("Discipline", buildStatBar(pet.getDisciplinePercentage()), true);
        return embed.build();
    }

    private static String getPetMoodDescription(Pet pet)
    {
        Random r = new Random();
        String description;
        switch (pet.getPetMood())
        {
            case HAPPY:
                String[] happyDescriptions = {"is frolicking happily", "is happy to see you", "has a beaming smile on their face", "looks happy"};
                description = pet.getPetName()+" "+happyDescriptions[r.nextInt(happyDescriptions.length)]+".";
                break;
            case HUNGRY:
                String[] hungryDescriptions = {"has a rumbly tummy", "looks hungry", "is starving", "needs to eat", "is glancing at the food", "looks worn out"};
                description = pet.getPetName()+" "+hungryDescriptions[r.nextInt(hungryDescriptions.length)]+".";
                break;
            case SLEEPING:
                String[] sleepyDescriptions = {"is asleep", "has fallen asleep", "seems tired", "looks disinterested"};
                description = pet.getPetName()+" "+sleepyDescriptions[r.nextInt(sleepyDescriptions.length)]+".";
                break;
            case NOISY:
                String[] noisyDescriptions = {"is causing a ruckus", "is being noisy", "won't shut up", "is causing an uproar"};
                description = pet.getPetName()+" "+noisyDescriptions[r.nextInt(noisyDescriptions.length)]+".";
                break;
            default:
                description = pet.getPetName()+" is here!";
        }
        return description;
    }

    private static String getPetImage(Pet pet)
    {
        if (pet.getLifeStage() == LifeStage.EGG)
        {
            return "https://i.imgur.com/Hfm5CRB.png";
        }
        else if (pet.getPetType() == PetType.CIRCLE_RED)
        {
            if (pet.getLifeStage() == LifeStage.BABY)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/SP4A9tG.png";
                    case SLEEPING:
                        return "https://i.imgur.com/fIzd6pv.png";
                    case HUNGRY:
                        return "https://i.imgur.com/TIWC2Hi.png";
                    case NOISY:
                        return "https://i.imgur.com/i5iVNHG.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.CHILD)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/nvtiOeN.png";
                    case SLEEPING:
                        return "https://i.imgur.com/EdJWwDA.png";
                    case HUNGRY:
                        return "https://i.imgur.com/42JIXTK.png";
                    case NOISY:
                        return "https://i.imgur.com/QiUFcse.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.TEEN)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/ifhEtgk.png";
                    case SLEEPING:
                        return "https://i.imgur.com/JDOL4nd.png";
                    case HUNGRY:
                        return "https://i.imgur.com/gsV5rpM.png";
                    case NOISY:
                        return "https://i.imgur.com/WNABqAU.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.ADULT)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/u9fitFQ.png";
                    case SLEEPING:
                        return "https://i.imgur.com/2LQ8nbW.png";
                    case HUNGRY:
                        return "https://i.imgur.com/oj0h5fv.png";
                    case NOISY:
                        return "https://i.imgur.com/gUlBpHX.png";
                }
            }
        }
        else if (pet.getPetType() == PetType.SQUARE_BLUE)
        {
            if (pet.getLifeStage() == LifeStage.BABY)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/od2QQt4.png";
                    case SLEEPING:
                        return "https://i.imgur.com/hu3n3MQ.png";
                    case HUNGRY:
                        return "https://i.imgur.com/58ma1I3.png";
                    case NOISY:
                        return "https://i.imgur.com/SXQOEfz.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.CHILD)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/2svUPof.png";
                    case SLEEPING:
                        return "https://i.imgur.com/GFn4b0R.png";
                    case HUNGRY:
                        return "https://i.imgur.com/XdkHHGo.png";
                    case NOISY:
                        return "https://i.imgur.com/g6hEZ7z.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.TEEN)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/NlVHQ1x.png";
                    case SLEEPING:
                        return "https://i.imgur.com/D3Lwabo.png";
                    case HUNGRY:
                        return "https://i.imgur.com/iqi9ppP.png";
                    case NOISY:
                        return "https://i.imgur.com/aswdzAi.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.ADULT)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/WqaGmlu.png";
                    case SLEEPING:
                        return "https://i.imgur.com/XVgeLHS.png";
                    case HUNGRY:
                        return "https://i.imgur.com/F6T5ORs.png";
                    case NOISY:
                        return "https://i.imgur.com/WzxEN2p.png";
                }
            }
        }
        else if (pet.getPetType() == PetType.SQUARE_PURPLE)
        {
            if (pet.getLifeStage() == LifeStage.BABY)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/Odggkkg.png";
                    case SLEEPING:
                        return "https://i.imgur.com/gjKlZQa.png";
                    case HUNGRY:
                        return "https://i.imgur.com/1uKJav3.png";
                    case NOISY:
                        return "https://i.imgur.com/vDnF7Z6.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.CHILD)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/Fs7CGEG.png";
                    case SLEEPING:
                        return "https://i.imgur.com/yy4Vrc6.png";
                    case HUNGRY:
                        return "https://i.imgur.com/lMWsgoC.png";
                    case NOISY:
                        return "https://i.imgur.com/erZe4KP.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.TEEN)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/BUvDvun.png";
                    case SLEEPING:
                        return "https://i.imgur.com/eCmp68s.png";
                    case HUNGRY:
                        return "https://i.imgur.com/r6vQPbN.png";
                    case NOISY:
                        return "https://i.imgur.com/g33DYm3.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.ADULT)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/UparPHB.png";
                    case SLEEPING:
                        return "https://i.imgur.com/FayDOsM.png";
                    case HUNGRY:
                        return "https://i.imgur.com/E2KWYVD.png";
                    case NOISY:
                        return "https://i.imgur.com/eCyDo2X.png";
                }
            }
        }
        else if (pet.getPetType() == PetType.CIRCLE_GREEN)
        {
            if (pet.getLifeStage() == LifeStage.BABY)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/YsZLTGb.png";
                    case SLEEPING:
                        return "https://i.imgur.com/k59iP94.png";
                    case HUNGRY:
                        return "https://i.imgur.com/qHtY3Il.png";
                    case NOISY:
                        return "https://i.imgur.com/7FoCqSk.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.CHILD)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/zmStBYK.png";
                    case SLEEPING:
                        return "https://i.imgur.com/XOiaOJy.png";
                    case HUNGRY:
                        return "https://i.imgur.com/l8W2LhX.png";
                    case NOISY:
                        return "https://i.imgur.com/0n5t53R.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.TEEN)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/Iv8nwKH.png";
                    case SLEEPING:
                        return "https://i.imgur.com/uX09xSO.png";
                    case HUNGRY:
                        return "https://i.imgur.com/zV6NExG.png";
                    case NOISY:
                        return "https://i.imgur.com/kFNQCZF.png";
                }
            }
            else if (pet.getLifeStage() == LifeStage.ADULT)
            {
                switch (pet.getPetMood())
                {
                    case HAPPY:
                        return "https://i.imgur.com/PxvC1u7.png";
                    case SLEEPING:
                        return "https://i.imgur.com/7vC0Nph.png";
                    case HUNGRY:
                        return "https://i.imgur.com/KkRlP5b.png";
                    case NOISY:
                        return "https://i.imgur.com/wLLt0E6.png";
                }
            }
        }
        return "";
    }

    private static String buildStatBar(float statPercentage)
    {
        int barCount = (int) Math.round(statPercentage/10);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i<barCount; i++)
        {
            sb.append("l");
        }
        for (int i = barCount; i<10; i++)
        {
            sb.append("-");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String getLifeStagePrettyPrint(LifeStage lifeStage)
    {
        switch (lifeStage)
        {
            case BABY:
                return "Baby";
            case CHILD:
                return "Child";
            case TEEN:
                return "Teen";
            case ADULT:
                return "Adult";
            case EGG:
                return "Egg";
        }
        return "Unknown";
    }
}
