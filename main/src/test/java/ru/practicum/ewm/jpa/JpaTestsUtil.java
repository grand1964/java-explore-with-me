package ru.practicum.ewm.jpa;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.common.convert.TimeConverter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

public class JpaTestsUtil {

    ///////////////////////////// Создание событий ///////////////////////////

    public static Event createEvent(User initiator, Category category) {
        Event event = createVoidEvent();
        event.setInitiator(initiator);
        event.setCategory(category);
        return event;
    }

    public static Event createEvent(User initiator, Category category, EventState state) {
        Event event = createEvent(initiator, category);
        event.setState(state);
        return event;
    }

    public static Event createVoidEvent() {
        Event event = new Event();
        event.setId(null);
        event.setTitle("t");
        event.setAnnotation("a");
        event.setDescription("d");
        event.setEventDate(TimeConverter.formatNow());
        event.setPaid(false);
        event.setLocationLat(0.0f);
        event.setLocationLon(0.0f);
        return event;
    }

    //////////////////////////// Создание запросов ///////////////////////////

    private static Request createVoidRequest() {
        Request request = new Request();
        request.setId(null);
        request.setStatus(RequestStatus.PENDING);
        return request;
    }

    public static Request createRequest(Event event, User requester) {
        Request request = createVoidRequest();
        request.setEvent(event);
        request.setRequester(requester);
        return request;
    }
}
