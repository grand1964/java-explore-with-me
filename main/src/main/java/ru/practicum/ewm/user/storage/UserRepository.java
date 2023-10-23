package ru.practicum.ewm.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    //получение пользователей с заданными id
    @Query("select distinct u from User u " +
            "where u.id in ?1 " +
            "order by u.id asc ")
    List<User> findUsersForIds(List<Long> ids);
}
