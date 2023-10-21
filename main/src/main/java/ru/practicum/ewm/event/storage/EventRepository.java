package ru.practicum.ewm.event.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    //////////////////////////// Публичные запросы ///////////////////////////

    //запрос события по id и состоянию
    Optional<Event> findByIdAndStateIs(long id, EventState state);

    ////////////////////////// Запросы администратора ////////////////////////

    //запрос событий по набору id
    Set<Event> findByIdIn(Set<Long> eventIds);

    /////////////////////////// Запросы пользователя /////////////////////////

    //получение пользователем своих событий
    @Query("select e from Event e " +
            "where e.initiator.id = ?1 " +
            "order by e.id asc ")
    Page<Event> getEventsByInitiator(long userId, Pageable pageable);

    //получение инициатором информации о своем событии
    @Query("select e from Event e " +
            "where e.id = ?2 and e.initiator.id = ?1 ")
    Optional<Event> getEventByInitiator(long userId, long eventId);
}
