package com.irfancan.deliverpad.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;


@Dao
public interface ItemDao {

    @Query("SELECT * FROM item")
    Maybe<List<Item>> getAll();

    @Query("SELECT * FROM item WHERE uid IN (:itemIds)")
    Single<List<Item>> loadAllByIds(int[] itemIds);

    /*
    @Query("SELECT * FROM item WHERE first_name LIKE :first AND "
            + "last_name LIKE :last LIMIT 1")
    Item findByName(String first, String last);*/

    @Insert
    void insertAll(Item... items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllItems(List<Item> tournaments);

    @Delete
    void delete(Item item);

}
