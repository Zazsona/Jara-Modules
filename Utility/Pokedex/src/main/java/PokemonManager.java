import com.google.gson.Gson;
import commands.CmdUtil;
import json.Pokemon;
import json.PokemonSpecies;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

public class PokemonManager
{
    private final static HashMap<String, Pokemon> pkmnCache = new HashMap<>();
    private static long lastRequestMinute;
    private static int requestsThisMinute;

    public static Pokemon getPokemon(String name) throws IllegalArgumentException, IllegalAccessException
    {
        try
        {
            name = formatName(name).replaceAll("[^a-zA-Z -]", "").toLowerCase();
            Pokemon pkmn = pkmnCache.get(name);
            if (pkmn == null)
            {
                if (requestsThisMinute <= 98 || lastRequestMinute != (Instant.now().getEpochSecond()/60))
                {
                    HashMap<String, String> headers = new HashMap();
                    headers.put("User-Agent", "Jara");
                    String pkmnJson = CmdUtil.sendHTTPRequestWithHeader("https://pokeapi.co/api/v2/pokemon/"+name, headers);
                    Gson gson = new Gson();
                    pkmn = gson.fromJson(pkmnJson, Pokemon.class);
                    String speciesJson = CmdUtil.sendHTTPRequestWithHeader(pkmn.species.url, headers);
                    pkmn.speciesData = gson.fromJson(speciesJson, PokemonSpecies.class);
                    pkmnCache.put(pkmn.name, pkmn);
                    if (lastRequestMinute == (Instant.now().getEpochSecond()/60))
                    {
                        requestsThisMinute += 2;
                    }
                    else
                    {
                        lastRequestMinute = Instant.now().getEpochSecond()/60;
                        requestsThisMinute = 2;
                    }
                }
                else
                {
                    throw new IllegalAccessException("Ratelimit.");
                }

            }
            return pkmn;
        }
        catch (NullPointerException | IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Invalid Pokemon.");
        }
    }

    private static String formatName(String name)
    {
        name = name.toLowerCase();
        if (name.contains(" "))
        {
            StringBuilder formattedName = new StringBuilder();
            String[] nameWords = name.split(" ");
            for (int i = nameWords.length-1; i>-1; i--) //Backwards, as the API titles as such (E.g, raichu-alola)
            {
                formattedName.append(nameWords[i]).append("-");
            }
            formattedName.deleteCharAt(formattedName.length()-1);
            name = formattedName.toString();
        }
        name = correctCommonMistakes(name);
        return name;
    }

    private static String correctCommonMistakes(String name)
    {
        name = name.toLowerCase();
        name = name.replace("-alolan", "-alola");
        name = name.replace("-galarian", "-galar");
        switch (name)
        {
            case "shaymin":
                return "shaymin-land";
            case "castform-ice":
            case "castform-snow":
            case "castform-wind":
                return "castform-snowy";
            case "castform-sun":
            case "castform-hot":
            case "castform-fire":
                return "castform-sunny";
            case "castform-wet":
            case "castform-rain":
                return "castform-rainy";
            case "nidoran-male":
            case "nidoran-guy":
            case "nidoran-boy":
                return "nidoran-m";
            case "nidoran-female":
            case "nidoran-girl":
                return "nidoran-f";
            case "mr.mime":
            case "mrmime":
            case "mr.-mime":
                return "mr-mime";
            case "attack-deoxys":
                return "deoxys-attack";
            case "defense-deoxys":
                return "deoxys-defense";
            case "speed-deoxys":
                return "deoxys-speed";
            case "deoxys":
            case "normal-deoxys":
                return "deoxys-normal";
            case "wormadam":
            case "plant-wormadam":
                return "wormadam-plant";
            case "wormadam-sand":
            case "sand-wormadam":
            case "sandy-wormadam":
                return "wormadam-sandy";
            case "trash-wormadam":
                return "wormadam-trash";
            case "heat-rotom":
                return "rotom-heat";
            case "wash-rotom":
                return "rotom-wash";
            case "frost-rotom":
                return "rotom-frost";
            case "fan-rotom":
                return "rotom-fan";
            case "mow-rotom":
                return "rotom-mow";
            case "giratina":
            case "altered-giratina":
                return "giratina-altered";
            case "origin-giratina":
                return "giratina-origin";
            case "basculin":
            case "red-basculin":
            case "basculin-red":
                return "basculin-red-striped";
            case "blue-basculin":
            case "basculin-blue":
                return "basculin-blue-striped";
            case "darmanitan":
            case "standard-darmanitan":
                return "darmanitan-standard";
            case "zen-darmanitan":
                return "darmanitan-zen";
            case "landorus-t":
            case "t-landorus":
            case "therian-landorus":
                return "landorus-therian";
            case "thundurus-t":
            case "t-thundurus":
            case "therian-thundurus":
                return "thundurus-therian";
            case "tornadus-t":
            case "t-tornadus":
            case "therian-tornadus":
                return "tornadus-therian";
            case "landorus":
            case "landorus-i":
            case "i-landorus":
            case "incarnate-landorus":
                return "landorus-incarnate";
            case "thundurus":
            case "thundurus-i":
            case "i-thundurus":
            case "incarnate-thundurus":
                return "thundurus-incarnate";
            case "tornadus":
            case "tornadus-i":
            case "i-tornadus":
            case "incarnate-tornadus":
                return "tornadus-incarnate";
            case "keldeo":
            case "ordinary-keldeo":
                return "keldeo-ordinary";
            case "resolute-keldeo":
            case "sword-keldeo":
                return "keldeo-resolute";
            case "meloetta":
                return "meloetta-aria";
            case "aegislash":
            case "shield-aegislash":
                return "segislash-shield";
            case "blade-aegislash":
            case "sword-aegislash":
            case "aegislash-sword":
                return "aegislash-blade";
            case "pumpkaboo":
                return "pumpkaboo-average";
            case "gourgeist":
                return "gourgeist-average";
            case "zygarde-10-percent":
            case "zygarde-10%":
            case "10%-zygarde":
            case "percent-10-zygarde":
            case "zygarde-dog":
                return "zygarde-10";
            case "zygarde-50-percent":
            case "zygarde-50%":
            case "50%-zygarde":
            case "percent-50-zygarde":
            case "zygarde-snake":
                return "zygarde-50";
            case "complete-zygarde":
            case "zygarde-100-percent":
            case "zygarde-100%":
            case "100%-zygarde":
            case "percent-100-zygarde":
            case "zygarde-full":
                return "zygarde-complete";
            case "unbound-hoopa":
                return "hoopa-unbound";
            case "baile-oricorio":
            case "red-oricorio":
            case "oricorio-red":
                return "oricorio-baile";
            case "pom-pom-oricorio":
            case "yellow-oricorio":
            case "oricorio-yellow":
                return "ocicorio-pom-pom";
            case "p'au-oricorio":
            case "oricorio-p'au":
            case "oricorio-pink":
            case "pink-oricorio":
                return "ocicorio-pau";
            case "sensu-oricorio":
            case "purple-oricorio":
            case "oricorio-purple":
                return "ocicorio-sensu";
            case "lycanroc":
                return "lycanroc-dawn";
            case "wishiwashi":
                return "wishiwashi-solo";
            case "minior":
                return "minior-red-meteor";
            case "mimikyu":
                return "mimikyu-disguised";
            case "necrozma-mane-dusk":
            case "dusk-necroxma":
            case "mane-dusk-necrozma":
                return "necrozma-dusk";
            case "necrozma-wings-dawn":
            case "dawn-necroxma":
            case "wings-dawn-necrozma":
                return "necrozma-dawn";
            case "bulu-tapu":
                return "tapu-bulu";
            case "fini-tapu":
                return "tapu-fini";
            case "lele-tapi":
                return "tapu-lele";
            case "koko-tapu":
                return "tapu-koko";
            case "ash-greninja":
                return "greninja-ash";
            case "libre-pikachu":
                return "pikachu-libre";
            case "phd-pikachu":
                return "pikachu-phd";
            case "star-pop-pikachu":
            case "pop-star-pikachu":
                return "pikachu-pop-star";
            case "belle-pikachu":
                return "pikachu-belle";
            case "star-rock-pikachu":
            case "rock-star-pikachu":
                return "pikachu-rock-star";
            case "cosplay-pikachu":
                return "pikachu-cosplay";
            case "cap-original-pikachu":
            case "pikachu-cap-original":
            case "original-cap-pikachu":
            case "cap-kanto-pikachu":
            case "pikachu-cap-kanto":
            case "kanto-cap-pikachu":
            case "cap-johto-pikachu":
            case "pikachu-cap-johto":
            case "johto-cap-pikachu":
                return "pikachu-original-cap";
            case "cap-hoenn-pikachu":
            case "pikachu-cap-hoenn":
            case "hoenn-cap-pikachu":
                return "pikachu-hoenn-cap";
            case "cap-sinnoh-pikachu":
            case "pikachu-cap-sinnoh":
            case "sinnoh-cap-pikachu":
                return "pikachu-sinnoh-cap";
            case "cap-unova-pikachu":
            case "pikachu-cap-unova":
            case "unova-cap-pikachu":
                return "pikachu-unova-cap";
            case "cap-kalos-pikachu":
            case "pikachu-cap-kalos":
            case "kalos-cap-pikachu":
                return "pikachu-kalos-cap";
            case "cap-alola-pikachu":
            case "pikachu-cap-alola":
            case "alola-cap-pikachu":
                return "pikachu-alola-cap";
            case "cap-partner-pikachu":
            case "pikachu-cap-partner":
            case "partner-cap-pikachu":
                return "pikachu-partner-cap";

        }
        return name;
    }
}
