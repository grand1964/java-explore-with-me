package ru.practicum.ewm.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /*@Query("select count(e) from Event e " +
            "where e.category.id = ?1 ")
    Long countEventsWithCategory(long categoryId);*/
}
