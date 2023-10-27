package ru.practicum.ewm.rating.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.rating.dto.EventWithRating;
import ru.practicum.ewm.rating.dto.UserWithRating;
import ru.practicum.ewm.rating.model.Like;
import ru.practicum.ewm.rating.model.LikeKey;
import ru.practicum.ewm.request.model.RequestStatus;

public interface LikeRepository extends JpaRepository<Like, LikeKey> {
    //число одобренных заявок пользователя на событие
    @Query("select count(r) from Request r " +
            "where r.requester.id = ?1 and r.event.id = ?2 and r.status = ?3" )
    int countOfEventParticipationByUser(long userId, long eventId, RequestStatus status);

    /*//получение событий с неотрицательными рейтингами (по убыванию рейтингов)
    @Query("select l.event as e, sum(l.rating) as s from Like l " +
            "group by e " +
            "order by s desc "
    )
    Page<PairToReturn<Event, Long>> getEventsWithPositiveRating(Pageable pageable);*/

    @Query("select new ru.practicum.ewm.rating.dto.EventWithRating(l.event as e, sum(l.rating) as s) from Like l " +
            "group by e " +
            "order by s desc ")
    Page<EventWithRating> getEventsWithPositiveRating(Pageable pageable);

    /*@Query("select e, sum(l.rating) as s from Like l " +
            "inner join l.event e " +
            "where s >= 0 " +
            "group by e " +
            "order by s desc ")
    Page<PairToReturn<Event, Long>> getEventsWithPositiveRating(Pageable pageable);*/

    //получение событий с неположительными рейтингами (по возрастанию рейтингов)
    /*@Query("select e, sum(l.rating) as s from Like l " +
            "inner join l.event e " +
            "where s <= 0 " +
            "group by e " +
            "order by s asc ")
    Page<PairToReturn<Event, Long>> getEventsWithNegativeRating(Pageable pageable);*/

    @Query("select new ru.practicum.ewm.rating.dto.EventWithRating(l.event as e, sum(l.rating) as s) from Like l " +
            "group by e " +
            "order by s asc ")
    Page<EventWithRating> getEventsWithNegativeRating(Pageable pageable);

    //получение пользователей с неотрицательными рейтингами (по убыванию рейтингов)
    /*@Query("select u, sum(l.rating) as s from Like l " +
            "inner join l.event.initiator u " +
            "where s >= 0 " +
            "group by u " +
            "order by s desc ")
    Page<PairToReturn<User, Long>> getUsersWithPositiveRating(Pageable pageable);*/
    @Query("select new ru.practicum.ewm.rating.dto.UserWithRating(l.event.initiator as u, sum(l.rating) as s) " +
            "from Like l " +
            "group by u " +
            "order by s desc ")
    Page<UserWithRating> getUsersWithPositiveRating(Pageable pageable);

    //получение пользователей с неотрицательными рейтингами (по убыванию рейтингов)
    /*@Query("select u, sum(l.rating) as s from Like l " +
            "inner join l.event.initiator u " +
            "where s <= 0 " +
            "group by u " +
            "order by s desc ")*/
    @Query("select new ru.practicum.ewm.rating.dto.UserWithRating(l.event.initiator as u, sum(l.rating) as s) " +
            "from Like l " +
            "group by u " +
            "order by s asc ")
    Page<UserWithRating> getUsersWithNegativeRating(Pageable pageable);
}
