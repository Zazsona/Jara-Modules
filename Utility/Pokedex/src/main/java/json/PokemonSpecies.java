package json;

public class PokemonSpecies
{
    public int gender_rate;
    public int capture_rate;
    public int base_happiness;
    public APIResourcePointer[] egg_groups;
    public FlavorText[] flavor_text_entries;
    public Genus[] genera;
    public int hatch_counter;

    public static class FlavorText
    {
        public String flavor_text;
        public APIResourcePointer language;
        public APIResourcePointer version;
    }

    public static class Genus
    {
        public String genus;
        public APIResourcePointer language;
    }
}
