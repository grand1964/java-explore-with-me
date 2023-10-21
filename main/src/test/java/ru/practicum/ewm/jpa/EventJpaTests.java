package ru.practicum.ewm.jpa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EventJpaTests {
    private static User userVasya;
    private static User userPetya;
    private static Category cat1;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    public static void setUp() {
        userVasya = new User(null, "Vasya", "vasya@com");
        userPetya = new User(null, "Petya", "petya@com");
        cat1 = new Category(null, "Категория");
    }

    @BeforeEach
    public void clearAll() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findByIdAndStateEventTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем события
        Event event1 = eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory, EventState.PUBLISHED));
        Event event2 = eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory, EventState.PENDING));
        Optional<Event> event = eventRepository.findByIdAndStateIs(event2.getId(), EventState.PENDING);
        assertTrue(event.isPresent());
        assertEquals(event.get().getId(), event2.getId());
        event = eventRepository.findByIdAndStateIs(event1.getId(), EventState.PENDING);
        assertTrue(event.isEmpty());
    }

    @Test
    public void findByIdEventTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем события
        Event event1 = eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory));
        Event event2 = eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory));
        Event event3 = eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory));
        Event event4 = eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory));
        Set<Long> ids = Set.of(event2.getId(), event4.getId());
        Set<Event> events = eventRepository.findByIdIn(ids);
        assertEquals(events.size(), 2);
        assertTrue(events.contains(event2));
        assertTrue(events.contains(event4));
        assertFalse(events.contains(event1));
        assertFalse(events.contains(event3));
    }

    @Test
    public void getRequestsForEventTest() {
        //создаем инициаторов
        User initiator1 = userRepository.save(userVasya);
        User initiator2 = userRepository.save(userPetya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем события
        Event event1 = eventRepository.save(JpaTestsUtil.createEvent(initiator1, savedCategory));
        eventRepository.save(JpaTestsUtil.createEvent(initiator2, savedCategory));
        Event event3 = eventRepository.save(JpaTestsUtil.createEvent(initiator1, savedCategory));
        eventRepository.save(JpaTestsUtil.createEvent(initiator2, savedCategory));
        Pageable pageable = PageRequest.of(0, 4);
        List<Event> events = eventRepository.getEventsByInitiator(initiator1.getId(), pageable).getContent();
        assertEquals(events.size(), 2);
        assertEquals(events.get(0).getId(), event1.getId());
        assertEquals(events.get(1).getId(), event3.getId());
    }

    @Test
    public void getEventByInitiatorTest() {
        //создаем инициатора
        User initiator1 = userRepository.save(userVasya);
        User initiator2 = userRepository.save(userPetya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем события
        Event event1 = eventRepository.save(JpaTestsUtil.createEvent(initiator1, savedCategory));
        Event event2 = eventRepository.save(JpaTestsUtil.createEvent(initiator2, savedCategory));
        Optional<Event> event = eventRepository.getEventByInitiator(initiator1.getId(), event1.getId());
        assertTrue(event.isPresent());
        assertEquals(event.get().getId(), event1.getId());
        event = eventRepository.getEventByInitiator(initiator1.getId(), event2.getId());
        assertTrue(event.isEmpty());
    }
}

