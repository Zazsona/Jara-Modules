package com.zazsona.namethatpokemon;

import com.zazsona.jara.ModuleResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PokemonList
{
    private String[] pokemon;

    public String[] getPokemon() throws IOException
    {
        if (pokemon == null)
        {
            Scanner scanner = new Scanner(ModuleResourceLoader.getResourceStream("Pokemon", "com/Zazsona/namethatpokemon/pokemon.json"));
            ArrayList<String> pokemonList = new ArrayList<>();
            while (scanner.hasNextLine())
            {
                pokemonList.add(scanner.nextLine());
            }
            this.pokemon = pokemonList.toArray(new String[0]);
        }
        return pokemon;
    }
}
