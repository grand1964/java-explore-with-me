package ru.practicum.ewm.jpa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.rating.dto.EventWithRating;
import ru.practicum.ewm.rating.dto.UserWithRating;
import ru.practicum.ewm.rating.model.Like;
import ru.practicum.ewm.rating.model.LikeKey;
import ru.practicum.ewm.rating.storage.LikeRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RatingJpaTests {
    private static User userVasya;
    private static User userPetya;
    private static User userFedya;
    private static User userKolya;
    private static Category cat1;


    @Autowired
    private TestEntityManager em;

    @Autowired
    private LikeRepository likeRepository;

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
        likeRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createLikeTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквестора
        User requester = userRepository.save(userPetya);
        //создаем событие
        Event event = JpaTestsUtil.createEvent(initiator, savedCategory);
        eventRepository.save(event);
        //создаем лайк
        Like like = new Like(requester, event, 1);
        likeRepository.save(like);
        //читаем лайк из базы
        Optional<Like> oLike = likeRepository.findById(new LikeKey(requester.getId(), event.getId()));
        assertTrue(oLike.isPresent());
        assertEquals(like, oLike.get());
    }

    @Test
    public void countOfEventParticipationByUserTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквесторов
        User requester1 = userRepository.save(userPetya);
        User requester2 = userRepository.save(userFedya);
        User requester3 = userRepository.save(userKolya);
        //создаем события
        Event event = JpaTestsUtil.createEvent(initiator, savedCategory);
        eventRepository.save(event);
        //создаем заявки
        Request request1 = JpaTestsUtil.createRequest(event, requester1);
        request1.setStatus(RequestStatus.PENDING); //не одобренная
        requestRepository.save(request1);
        Request request2 = JpaTestsUtil.createRequest(event, requester2);
        request2.setStatus(RequestStatus.CONFIRMED); //одобренная
        requestRepository.save(request2);
        Request request3 = JpaTestsUtil.createRequest(event, requester3);
        request3.setStatus(RequestStatus.REJECTED); //отвергнутая
        requestRepository.save(request3);
        //валидируем реквесторов
        assertEquals(likeRepository.countOfEventParticipationByUser(requester1.getId(), event.getId(),
                RequestStatus.CONFIRMED), 0);
        assertEquals(likeRepository.countOfEventParticipationByUser(requester2.getId(), event.getId(),
                RequestStatus.CONFIRMED), 1);
        assertEquals(likeRepository.countOfEventParticipationByUser(requester3.getId(), event.getId(),
                RequestStatus.CONFIRMED), 0);
    }

    /*@Test
    public void countOfEventParticipationByUserTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквесторов
        User requester1 = userRepository.save(userPetya);
        User requester2 = userRepository.save(userFedya);
        //создаем события
        Event event1 = JpaTestsUtil.createEvent(initiator, savedCategory);
        event1.setEventDate("2007-09-06 00:11:22"); //прошлое
        eventRepository.save(event1);
        Event event2 = JpaTestsUtil.createEvent(initiator, savedCategory);
        event2.setEventDate("2027-09-06 00:11:22"); //будущее
        eventRepository.save(event2);
        //создаем заявки
        Request request1 = JpaTestsUtil.createRequest(event1, requester1);
        request1.setStatus(RequestStatus.PENDING); //не одобренная
        requestRepository.save(request1);
        Request request2 = JpaTestsUtil.createRequest(event2, requester1);
        request2.setStatus(RequestStatus.CONFIRMED); //одобренная, но на будущее событие
        requestRepository.save(request2);
        Request request3 = JpaTestsUtil.createRequest(event1, requester2);
        request3.setStatus(RequestStatus.CONFIRMED); //одобренная, на прошлое событие
        requestRepository.save(request3);
        Request request4 = JpaTestsUtil.createRequest(event2, requester2);
        request4.setStatus(RequestStatus.REJECTED); //не одобренная, на будущее событие
        requestRepository.save(request4);
        //валидируем реквесторов
        assertEquals(likeRepository.countOfEventParticipationByUser(requester1.getId(), event1.getId(),
                TimeConverter.formatNow(), RequestStatus.CONFIRMED), 0);
        assertEquals(likeRepository.countOfEventParticipationByUser(requester1.getId(), event2.getId(),
                TimeConverter.formatNow(), RequestStatus.CONFIRMED), 0);
        assertEquals(likeRepository.countOfEventParticipationByUser(requester2.getId(), event1.getId(),
                TimeConverter.formatNow(), RequestStatus.CONFIRMED), 1);
        assertEquals(likeRepository.countOfEventParticipationByUser(requester2.getId(), event2.getId(),
                TimeConverter.formatNow(), RequestStatus.CONFIRMED), 0);
    }*/

    @Test
    public void getEventsWithRatingTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем реквесторов
        User requester1 = userRepository.save(userPetya);
        User requester2 = userRepository.save(userFedya);
        //создаем события
        Event event1 = JpaTestsUtil.createEvent(initiator, savedCategory);
        event1.setTitle("1");
        eventRepository.save(event1);
        Event event2 = JpaTestsUtil.createEvent(initiator, savedCategory);
        event2.setTitle("2");
        eventRepository.save(event2);
        Event event3 = JpaTestsUtil.createEvent(initiator, savedCategory);
        event3.setTitle("3");
        eventRepository.save(event3);
        Event event4 = JpaTestsUtil.createEvent(initiator, savedCategory);
        event4.setTitle("4");
        eventRepository.save(event4);
        //ставим лайки
        likeRepository.save(new Like(requester1, event1, 1));
        likeRepository.save(new Like(requester2, event1, 1));
        likeRepository.save(new Like(requester1, event2, 2));
        likeRepository.save(new Like(requester2, event2, -1));
        likeRepository.save(new Like(requester1, event3, -2));
        likeRepository.save(new Like(requester2, event3, 1));
        likeRepository.save(new Like(requester1, event4, -1));
        likeRepository.save(new Like(requester2, event4, -1));

        PageRequest pageable = PageRequest.of(0, 10);
        //проверяем положительные рейтинги
        List<EventWithRating> list = likeRepository.getEventsWithPositiveRating(pageable).getContent();
        assertEquals(list.size(), 4);
        assertEquals(list.get(0).getRating(), 2);
        assertEquals(list.get(0).getEvent().getTitle(), "1");
        assertEquals(list.get(1).getRating(), 1);
        assertEquals(list.get(1).getEvent().getTitle(), "2");
        //проверяем отрицательные рейтинги
        list = likeRepository.getEventsWithNegativeRating(pageable).getContent();
        assertEquals(list.size(), 4);
        assertEquals(list.get(0).getRating(), -2);
        assertEquals(list.get(0).getEvent().getTitle(), "4");
        assertEquals(list.get(1).getRating(), -1);
        assertEquals(list.get(1).getEvent().getTitle(), "3");
    }

    @Test
    public void getUsersWithRatingTest() {
        //создаем реквестора
        User requester = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем инициаторов
        User initiator1 = userRepository.save(userPetya);
        User initiator2 = userRepository.save(userFedya);
        User initiator3 = userRepository.save(userKolya);
        //создаем события
        Event event1 = JpaTestsUtil.createEvent(initiator1, savedCategory);
        eventRepository.save(event1);
        Event event2 = JpaTestsUtil.createEvent(initiator1, savedCategory);
        eventRepository.save(event2);
        Event event3 = JpaTestsUtil.createEvent(initiator1, savedCategory);
        eventRepository.save(event3);
        Event event4 = JpaTestsUtil.createEvent(initiator2, savedCategory);
        eventRepository.save(event4);
        Event event5 = JpaTestsUtil.createEvent(initiator2, savedCategory);
        eventRepository.save(event5);
        Event event6 = JpaTestsUtil.createEvent(initiator2, savedCategory);
        eventRepository.save(event6);
        Event event7 = JpaTestsUtil.createEvent(initiator3, savedCategory);
        eventRepository.save(event7);
        Event event8 = JpaTestsUtil.createEvent(initiator3, savedCategory);
        eventRepository.save(event8);
        Event event9 = JpaTestsUtil.createEvent(initiator3, savedCategory);
        eventRepository.save(event9);
        //ставим лайки
        likeRepository.save(new Like(requester, event1, 1));
        likeRepository.save(new Like(requester, event2, 1));
        likeRepository.save(new Like(requester, event3, 1));
        likeRepository.save(new Like(requester, event4, -1));
        likeRepository.save(new Like(requester, event5, -1));
        likeRepository.save(new Like(requester, event6, 1));
        likeRepository.save(new Like(requester, event7, 1));
        likeRepository.save(new Like(requester, event8, 1));
        likeRepository.save(new Like(requester, event9, -1));

        PageRequest pageable = PageRequest.of(0, 10);
        //проверяем положительные рейтинги
        List<UserWithRating> list = likeRepository.getUsersWithPositiveRating(pageable).getContent();
        assertEquals(list.size(), 3);
        assertEquals(list.get(0).getRating(), 3);
        assertEquals(list.get(0).getUser().getName(), "Petya");
        assertEquals(list.get(1).getRating(), 1);
        assertEquals(list.get(1).getUser().getName(), "Kolya");
        assertEquals(list.get(2).getRating(), -1);
        assertEquals(list.get(2).getUser().getName(), "Fedya");
        //проверяем отрицательные рейтинги
        list = likeRepository.getUsersWithNegativeRating(pageable).getContent();
        assertEquals(list.get(0).getRating(), -1);
        assertEquals(list.get(0).getUser().getName(), "Fedya");
        assertEquals(list.get(1).getRating(), 1);
        assertEquals(list.get(1).getUser().getName(), "Kolya");
        assertEquals(list.get(2).getRating(), 3);
        assertEquals(list.get(2).getUser().getName(), "Petya");
    }
}

