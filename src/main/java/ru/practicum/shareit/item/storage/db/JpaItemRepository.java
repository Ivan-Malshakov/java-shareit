package ru.practicum.shareit.item.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Integer> {

    @Query("select it from Item as it where (upper(it.name) like CONCAT('%',UPPER(:name),'%') " +
            "or upper(it.description) like CONCAT('%',UPPER(:description),'%')) AND it.available = true")
    List<Item> findByName(@Param("name") String name, @Param("description") String description);

    List<Item> findByOwnerIdOrderByIdAsc(Integer id);
}
