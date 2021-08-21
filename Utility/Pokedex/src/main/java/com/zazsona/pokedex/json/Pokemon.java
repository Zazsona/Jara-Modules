package com.zazsona.pokedex.json;

public class Pokemon
{
    public int id;
    public String name;
    public int base_experience;
    public int height; //decimetres
    public boolean is_default;
    public int order;
    public int weight; //hectograms
    public PokemonAbility[] abilities;
    public PokemonHeldItem[] held_items;
    public PokemonMove[] moves;
    public PokemonSprites sprites;
    public PokemonStat[] stats;
    public PokemonType[] types;
    public APIResourcePointer species;
    public APIResourcePointer[] forms;
    public PokemonSpecies speciesData;
}

