import commands.CmdUtil;
import module.Command;
import json.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PokeDex extends Command
{
    private enum DexType
    {
        NORMAL,
        BATTLE,
        BREEDING
    }

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 1)
            {
                DexType dexType = getDexType(parameters[parameters.length-1]);
                Pokemon pkmn = PokemonManager.getPokemon(getPokemonName(parameters, dexType));
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                embed.setFooter("National Pokedex", "https://i.imgur.com/wXSYWpN.png");
                embed.setTitle(formatNamedItems(pkmn.name) + " | " + getGenus(pkmn));
                embed.setDescription(getPokedexDescription(pkmn));
                embed.setThumbnail(pkmn.sprites.front_default);

                embed.addField("Abilities", getAbilities(pkmn), true);
                embed.addField("Types", getTypes(pkmn), true);
                embed.addField("Base Stats", getBaseStats(pkmn), true);
                if (dexType == DexType.NORMAL)
                {
                    embed.addField("Wild Encounters", getCaptureInfo(pkmn), true);
                }
                else if (dexType == DexType.BREEDING)
                {
                    embed.addField("Breeding", getBreedingInfo(pkmn), true);
                }
                else if (dexType == DexType.BATTLE)
                {
                    embed.addField("Body", getWeightAndHeight(pkmn), true);
                    embed.addField("Moves", getMoves(pkmn), false);
                }

                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        catch (IllegalArgumentException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setTitle("??? | ???");
            embed.setDescription("A yet undiscovered or unknown Pokemon.");
            embed.setThumbnail("https://i.imgur.com/4TUoYOM.png");
            embed.setFooter("National Pokedex", "https://i.imgur.com/wXSYWpN.png");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        catch (IllegalAccessException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("The Pokedex is overloaded. Please try again later.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }

    private DexType getDexType(String typeParameter)
    {
        if (typeParameter.equalsIgnoreCase("battle") || typeParameter.equalsIgnoreCase("battling"))
        {
            return DexType.BATTLE;
        }
        else if (typeParameter.equalsIgnoreCase("breeding") || typeParameter.equalsIgnoreCase("breed"))
        {
            return DexType.BREEDING;
        }
        else
        {
            return DexType.NORMAL;
        }
    }

    private String getAbilities(Pokemon pkmn)
    {
        StringBuilder sb = new StringBuilder();
        for (PokemonAbility pa : pkmn.abilities)
        {
            sb.append(formatNamedItems(pa.ability.name));
            if (pa.is_hidden)
            {
                sb.append(" (Hidden)");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String getTypes(Pokemon pkmn)
    {
        StringBuilder sb = new StringBuilder();
        for (PokemonType pt : pkmn.types)
        {
            if (pt.slot == 1)
            {
                sb.insert(0, formatNamedItems(pt.type.name)+"\n");
            }
            else
            {
                sb.insert(sb.length(), formatNamedItems(pt.type.name)).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String getBaseStats(Pokemon pkmn)
    {
        StringBuilder sb = new StringBuilder();
        for (PokemonStat stat : pkmn.stats)
        {
            sb.insert(0, formatNamedItems(stat.stat.name)+": "+stat.base_stat+"\n");
        }
        return sb.toString();
    }

    private String getWildHeldItems(Pokemon pkmn)
    {
        if (pkmn.held_items.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (PokemonHeldItem heldItem : pkmn.held_items)
            {
                sb.append(formatNamedItems(heldItem.item.name)).append(", ");
            }
            sb.setLength(sb.length()-2);
            return sb.toString().trim();
        }
        else
        {
            return "None";
        }
    }

    private String getGenus(Pokemon pkmn)
    {
        try
        {
            for (PokemonSpecies.Genus genus : pkmn.speciesData.genera)
            {
                if (genus.language.name.equals("en"))
                {
                    return "The "+genus.genus;
                }
            }
            return "Unknown Genus";
        }
        catch (NullPointerException e)
        {
            return "Unknown Genus";
        }
    }

    private String getPokedexDescription(Pokemon pkmn)
    {
        try
        {
            for (PokemonSpecies.FlavorText ft : pkmn.speciesData.flavor_text_entries)
            {
                if (ft.language.name.equals("en"))
                {
                    return ft.flavor_text;
                }
            }
            return "This Pokemon is a mystery.";
        }
        catch (NullPointerException e)
        {
            return "This Pokemon is a mystery.";
        }
    }

    private String getBreedingInfo(Pokemon pkmn)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            if (pkmn.speciesData.gender_rate != -1)
            {
                sb.append("Gender Ratio (F:M): ").append(pkmn.speciesData.gender_rate).append(":").append(8-pkmn.speciesData.gender_rate);
            }
            else
            {
                sb.append("Gender: None");
            }
            sb.append("\n");
            sb.append("Egg Steps: ").append(pkmn.speciesData.hatch_counter*250).append("\n");
            sb.append("Egg Groups:\n");

            for (APIResourcePointer eggGroup : pkmn.speciesData.egg_groups)
            {
                sb.append(formatNamedItems(eggGroup.name)).append(", ");
            }
            sb.setLength(sb.length()-2);
            return sb.toString().trim();
        }
        catch (NullPointerException e)
        {
            return "???";
        }
    }

    private String getMoves(Pokemon pkmn)
    {
        StringBuilder sb = new StringBuilder();
        for (PokemonMove pkmnMove : pkmn.moves)
        {
            sb.append(formatNamedItems(pkmnMove.move.name)).append(", ");
        }
        sb.setLength(sb.length()-2);
        if (sb.length() >= 1024)
        {
            sb.setLength(1020);
            sb.append("...");
        }
        return sb.toString();
    }

    private String getWeightAndHeight(Pokemon pkmn)
    {
        return "Height: "+(pkmn.height*10)+"cm\nWeight: "+(pkmn.weight/10)+"kg";
    }

    private String getCaptureInfo(Pokemon pkmn)
    {
        try
        {
            return "Capture Rate: "+pkmn.speciesData.capture_rate+"\nExperience: "+pkmn.base_experience+"\nHeld Items:\n"+getWildHeldItems(pkmn);
        }
        catch (NullPointerException e)
        {
            return "No wild sightings.";
        }
    }


    private String getPokemonName(String[] parameters, DexType dexType)
    {
        StringBuilder sb = new StringBuilder();
        int limit = (dexType == DexType.NORMAL) ? parameters.length : parameters.length-1; //If user has specified the dex type, then it isn't part of the name.
        for (int i = 1; i<limit; i++)
        {
            sb.append(parameters[i]).append(" ");
        }
        return sb.toString().trim();
    }

    private String formatNamedItems(String text)
    {
        String[] words = text.split("-");
        StringBuilder sb = new StringBuilder();
        for (String word : words)
        {
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
