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
            "where r.requester.id = ?1 and r.event.id = ?2 and r.status = ?3")
    int countOfEventParticipationByUser(long userId, long eventId, RequestStatus status);

    //получение событий по убыванию рейтингов
    @Query("select new ru.practicum.ewm.rating.dto.EventWithRating(l.event as e, sum(l.rating) as s) from Like l " +
            "group by e " +
            "order by s desc ")
    Page<EventWithRating> getEventsWithPositiveRating(Pageable pageable);

    //получение событий по возрастанию рейтингов (антирейтинг)
    @Query("select new ru.practicum.ewm.rating.dto.EventWithRating(l.event as e, sum(l.rating) as s) from Like l " +
            "group by e " +
            "order by s asc ")
    Page<EventWithRating> getEventsWithNegativeRating(Pageable pageable);

    //получение пользователей по убыванию рейтингов
    @Query("select new ru.practicum.ewm.rating.dto.UserWithRating(l.event.initiator as u, sum(l.rating) as s) " +
            "from Like l " +
            "group by u " +
            "order by s desc ")
    Page<UserWithRating> getUsersWithPositiveRating(Pageable pageable);

    //получение пользователей по возрастанию рейтингов (антирейтинг)
    @Query("select new ru.practicum.ewm.rating.dto.UserWithRating(l.event.initiator as u, sum(l.rating) as s) " +
            "from Like l " +
            "group by u " +
            "order by s asc ")
    Page<UserWithRating> getUsersWithNegativeRating(Pageable pageable);
}
