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
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class CategoryJpaTests {
    private static User userVasya;
    private static Category cat1;
    private static Category cat2;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        userVasya = new User(null, "Vasya", "vasya@com");
        cat1 = new Category(null, "Категория 1");
        cat2 = new Category(null, "Категория 2");
    }

    @BeforeEach
    public void clearAll() {
        categoryRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void postWithDuplicatedNameTest() {
        Category badCategory = new Category(null, "Категория 1");
        categoryRepository.save(cat1);
        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(badCategory));
    }

    @Test
    public void patchWithDuplicatedNameTest() {
        long id = categoryRepository.save(cat1).getId(); //старый идентификатор
        categoryRepository.save(cat2);
        Category badCategory = new Category(id, "Категория 2"); //имя уже было
        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.saveAndFlush(badCategory));
    }

    @Test
    public void deleteNotEmptyCategoryTest() {
        //создаем инициатора
        User initiator = userRepository.save(userVasya);
        //создаем в базе категорию
        Category savedCategory = categoryRepository.save(cat1);
        //создаем в ней событие и сохраняем его
        eventRepository.save(JpaTestsUtil.createEvent(initiator, savedCategory));
        //удаление категории должно давать ошибку
        categoryRepository.delete(savedCategory);
        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.flush());
    }
}

