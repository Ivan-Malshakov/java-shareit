package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface JpaItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findItemRequestByRequestor_IdOrderByCreatedDesc(Integer requestorId);

    @Query("select it from ItemRequest as it where it.requestor.id != :requestorId order by it.created desc")
    List<ItemRequest> findItemRequestNotByRequestor_IdOrderByCreatedDesc(Integer requestorId);
}
