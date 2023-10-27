package ru.practicum.ewm.jpa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class RequestJpaTests {
    private static User userVasya;
    private static User userPetya;
    private static User userFedya;
    private static User userKolya;
    private static Category cat1;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        userVasya = new User(null, "Vasya", "vasya@com");
        userPetya = new User(null, "Petya", "petya@com");
        userFedya = new User(null, "Fedya", "fedya@com");
        userKolya = new User(null, "Kolya", "kolya@com");
        cat1 = new Category(null, "Категория");
    }

    @BeforeEach
    public void clearAll() {
        requestRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createDuplicatedRequestForEventTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквестора
        User requester = userRepository.save(userPetya);
        //создаем событие
        Event event = JpaTestsUtil.createEvent(initiator, savedCategory);
        eventRepository.save(event);
        //создаем запрос на событие
        requestRepository.save(JpaTestsUtil.createRequest(event, requester));
        //повторный запрос должен давать ошибку целостности
        assertThrows(
                DataIntegrityViolationException.class,
                () -> requestRepository.save(JpaTestsUtil.createRequest(event, requester))
        );
    }

    @Test
    public void getRequestsForEventTest() {
        //создаем инициаторов
        User initiator = userRepository.save(userVasya);
        User otherInitiator = userRepository.save(userKolya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквесторов
        User requester1 = userRepository.save(userPetya);
        User requester2 = userRepository.save(userFedya);
        //создаем события
        Event event = JpaTestsUtil.createEvent(initiator, savedCategory);
        eventRepository.save(event);
        Event otherEvent = JpaTestsUtil.createEvent(otherInitiator, savedCategory);
        eventRepository.save(otherEvent);
        //создаем запросы на них
        Request request1 = requestRepository.save(JpaTestsUtil.createRequest(event, requester1));
        requestRepository.save(JpaTestsUtil.createRequest(otherEvent, requester1));
        Request request3 = requestRepository.save(JpaTestsUtil.createRequest(event, requester2));
        List<Request> answer = requestRepository.getRequestsForEvent(initiator.getId(), event.getId());
        assertEquals(answer.size(), 2);
        assertEquals(answer.get(0).getId(), request1.getId());
        assertEquals(answer.get(1).getId(), request3.getId());
    }

    @Test
    public void getRequestsForEventWithIdsTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквесторов
        User requester1 = userRepository.save(userPetya);
        User requester2 = userRepository.save(userFedya);
        User requester3 = userRepository.save(userKolya);
        //создаем событие
        Event event = JpaTestsUtil.createEvent(initiator, savedCategory);
        eventRepository.save(event);
        //создаем запросы на него
        Request request1 = requestRepository.save(JpaTestsUtil.createRequest(event, requester1));
        requestRepository.save(JpaTestsUtil.createRequest(event, requester2));
        Request request3 = requestRepository.save(JpaTestsUtil.createRequest(event, requester3));
        List<Long> ids = List.of(request1.getId(), request3.getId());
        List<Request> answer = requestRepository.getRequestsForEventWithIds(
                initiator.getId(), event.getId(), ids);
        assertEquals(answer.size(), 2);
        assertEquals(answer.get(0).getId(), request1.getId());
        assertEquals(answer.get(1).getId(), request3.getId());
    }

    @Test
    public void getUserRequestsForForeignEventsTest() {
        //создаем реквестора
        User requester = userRepository.save(userVasya);
        //создаем инициаторов
        User initiator1 = userRepository.save(userPetya);
        User initiator2 = userRepository.save(userFedya);
        User initiator3 = userRepository.save(userKolya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем чужие события
        Event event1 = eventRepository.save(JpaTestsUtil.createEvent(initiator1, savedCategory));
        Event event2 = eventRepository.save(JpaTestsUtil.createEvent(initiator2, savedCategory));
        Event event3 = eventRepository.save(JpaTestsUtil.createEvent(initiator3, savedCategory));
        //а это - свое
        Event event4 = eventRepository.save(JpaTestsUtil.createEvent(requester, savedCategory));
        //создаем запросы на него
        Request request1 = requestRepository.save(JpaTestsUtil.createRequest(event1, requester));
        Request request2 = requestRepository.save(JpaTestsUtil.createRequest(event2, requester));
        Request request3 = requestRepository.save(JpaTestsUtil.createRequest(event3, requester));
        requestRepository.save(JpaTestsUtil.createRequest(event4, requester));
        //запрос своего участия в чужих событиях
        List<Request> answer = requestRepository.getUserRequestsForForeignEvents(requester.getId());
        assertEquals(answer.size(), 3);
        assertEquals(answer.get(0).getId(), request1.getId());
        assertEquals(answer.get(1).getId(), request2.getId());
        assertEquals(answer.get(2).getId(), request3.getId());
    }

    @Test
    public void updateStatusTest() {
        //создаем реквестора
        User requester = userRepository.save(userVasya);
        //создаем инициаторов
        User initiator1 = userRepository.save(userPetya);
        User initiator2 = userRepository.save(userFedya);
        User initiator3 = userRepository.save(userKolya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем события
        Event event1 = eventRepository.save(JpaTestsUtil.createEvent(initiator1, savedCategory));
        Event event2 = eventRepository.save(JpaTestsUtil.createEvent(initiator2, savedCategory));
        Event event3 = eventRepository.save(JpaTestsUtil.createEvent(initiator3, savedCategory));
        //создаем запросы на него
        Request request1 = requestRepository.save(JpaTestsUtil.createRequest(event1, requester));
        Request request2 = requestRepository.save(JpaTestsUtil.createRequest(event2, requester));
        Request request3 = requestRepository.save(JpaTestsUtil.createRequest(event3, requester));
        //список id запросов, в которых меняется статус
        List<Long> ids = List.of(request1.getId(), request3.getId());
        //меняем статус
        int count = requestRepository.updateStatus(ids, RequestStatus.CONFIRMED);
        //читаем все запросы
        List<Request> requests = requestRepository.findAll();
        assertEquals(count, 2);
        assertEquals(requests.get(0).getId(), request1.getId());
        assertEquals(requests.get(0).getStatus(), RequestStatus.CONFIRMED);
        assertEquals(requests.get(1).getId(), request2.getId());
        assertEquals(requests.get(1).getStatus(), RequestStatus.PENDING);
        assertEquals(requests.get(2).getId(), request3.getId());
        assertEquals(requests.get(2).getStatus(), RequestStatus.CONFIRMED);
    }
}

