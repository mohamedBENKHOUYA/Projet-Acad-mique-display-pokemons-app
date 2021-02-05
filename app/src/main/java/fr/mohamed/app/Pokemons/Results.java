package fr.mohamed.app.Pokemons;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "Results")
public class Results implements Serializable {
    // create id column
    @PrimaryKey(autoGenerate = true)
    private int cle;
    private String generationType;



    //Url
    private String url;
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "image")
    private String image = null;
    @ColumnInfo(name = "base_experience")
    private int base_experience;
    @ColumnInfo(name = "height")
    private int height;
    @ColumnInfo(name = "is_default")
    private boolean is_default;
    @ColumnInfo(name = "order")
    private int order;
    @ColumnInfo(name = "weight")
    private int weight;


    public Results(String generationType, String url, int id, String name, String image, int base_experience, int height, boolean is_default, int order, int weight) {
        this.generationType = generationType;
        this.url = url;
        this.id = id;
        this.name = name;
        this.image = image;
        this.base_experience = base_experience;
        this.height = height;
        this.is_default = is_default;
        this.order = order;
        this.weight = weight;
    }

    public String getGenerationType() {
        return generationType;
    }

    public void setGenerationType(String generationType) {
        this.generationType = generationType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public List<PokemonSpecies> getPokemon_species() {
//        return pokemon_species;
//    }
//
//    public void setPokemon_species(List<PokemonSpecies> pokemon_species) {
//        this.pokemon_species = pokemon_species;
//    }

//    private List<PokemonSpecies> pokemon_species;


//    @ColumnInfo(name = "abilities")
//    private List<Ability> abilities;
//    @ColumnInfo(name = "types")
//    private List<PokemonType> types;
//    @ColumnInfo(name = "stats")
//    private List<PokemonStat> stats;
//
//    @ColumnInfo(name = "sprites")
//    private List<PokemonSprites> sprites;
//    @ColumnInfo(name = "moves")
//    private List<PokemonMove> moves;


//    public Results(String url, String image) {
//        this.url = url;
//        this.image = image;
//    }
//
//    public Results(int id, String name, int base_experience, int height, boolean is_default, int order, int weight) {
//        this.id = id;
//        this.name = name;
//        this.base_experience = base_experience;
//        this.height = height;
//        this.is_default = is_default;
//        this.order = order;
//        this.weight = weight;
//    }

    public void setCle(int cle) {
        this.cle = cle;
    }
    public int getCle() {
        return cle;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBase_experience() {
        return base_experience;
    }

    public void setBase_experience(int base_experience) {
        this.base_experience = base_experience;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
