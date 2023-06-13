package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface RequestStorageDb extends JpaRepository<Request, Integer> {
    Collection<Request> findRequestsByRequestor(User requestor);

    List<Request> findRequestsByRequestorNot(User requestor, Pageable pageable);
}
