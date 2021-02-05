package fr.mohamed.app.APIs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.mohamed.app.Pokemons.Results;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ResultsPokemonDao {

    //Insert query
    @Insert(onConflict = REPLACE)
    void insert(Results mainData);

    //delete query
    @Delete
    void delete(Results  mainData);
    @Query("delete from Results where generationType = :generationType")
    void reset(String generationType);

//    @Query("UPDATE table_name SET text = :sText WHERE ID = :sID")
//    void update(int sID, String sText);
//
    @Query("SELECT * FROM Results where generationType = :generationType")
    List<Results> getAll(String generationType);

}
