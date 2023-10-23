package ru.practicum.ewm.jpa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserJpaTests {
    private static User userVasya;
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    @BeforeAll
    public static void setUp() {
        userVasya = new User(null, "Vasya", "vasya@com");
    }

    @BeforeEach
    public void clearAll() {
        repository.deleteAll();
    }

    @Test
    public void duplicatedEmailTest() {
        User badUser = new User(null, "Petya", "vasya@com");
        repository.save(userVasya);
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(badUser));
    }

    @Test
    public void getUsersForIdsTest() {
        User userPetya = new User(null, "Petya", "petya@com");
        User userFedya = new User(null, "Fedya", "fedya@com");
        List<Long> ids = new ArrayList<>();
        repository.save(userVasya);
        ids.add(repository.save(userPetya).getId());
        ids.add(repository.save(userFedya).getId());
        //добавляем дублирующийся индекс
        ids.add(ids.get(0));
        List<User> users = repository.findUsersForIds(ids);
        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getId(), ids.get(0));
        assertEquals(users.get(0).getName(), "Petya");
        assertEquals(users.get(1).getId(), ids.get(1));
        assertEquals(users.get(1).getName(), "Fedya");
    }

    @Test
    public void getUsersWithOffsetTest() {
        User userPetya = new User(null, "Petya", "petya@com");
        User userFedya = new User(null, "Fedya", "fedya@com");
        repository.save(userVasya);
        repository.save(userPetya);
        repository.save(userFedya);
        int from = 2;
        int size = 2;
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<User> users = repository.findAll(pageable).getContent();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), "Fedya");
    }

    @Test
    public void getLimitedUsersListTest() {
        User userPetya = new User(null, "Petya", "petya@com");
        User userFedya = new User(null, "Fedya", "fedya@com");
        repository.save(userVasya);
        repository.save(userPetya);
        repository.save(userFedya);
        PageRequest pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        List<User> users = repository.findAll(pageable).getContent();
        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getName(), "Vasya");
        assertEquals(users.get(1).getName(), "Petya");
    }
}

