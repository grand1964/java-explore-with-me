package ru.practicum.ewm.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    //получение инициатором информации обо всех заявках на участие в своем событии
    @Query("select r from Request r " +
            "inner join r.event e " +
            "where e.id = ?2 and e.initiator.id = ?1 " +
            "order by r.id asc ")
    List<Request> getRequestsForEvent(long userId, long eventId);

    //получение инициатором информации о заданных заявках на участие в своем событии
    @Query("select r from Request r " +
            "inner join r.event e " +
            "where r.id in ?3 and e.id = ?2 and e.initiator.id = ?1 ")
    List<Request> getRequestsForEventWithIds(long userId, long eventId, List<Long> requestIds);

    //получение пользователем информации о своих заявках на участие в чужих событиях
    @Query("select r from Request r " +
            "inner join r.requester u " +
            "where u.id = ?1 and r.event.initiator.id <> ?1 ")
    List<Request> getUserRequestsForForeignEvents(long userId);

    //запрос на обновление статуса
    @Modifying(clearAutomatically = true)
    @Query("update Request r set r.status = ?2 where r.id in ?1 ")
    int updateStatus(List<Long> ids, RequestStatus status);

    //количество заявок на данное событие от данного пользователя
    @Query("select count(r) from Request r " +
            "where r.event.id = ?1 and r.requester.id = ?2  ")
    int getRequestCountForEventAndRequester(long eventId, long requesterId);
}
