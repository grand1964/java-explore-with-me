package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //описания
    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation;

    @Column(name = "description", length = 7000, nullable = false)
    private String description;

    //даты
    @Column(name = "created_on", length = 32)
    private String createdOn;

    @Column(name = "published_on", length = 32)
    private String publishedOn;

    @Column(name = "event_date", length = 32, nullable = false)
    private String eventDate;

    //свойства
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "location_lat", nullable = false)
    private Float locationLat;

    @Column(name = "location_lon", nullable = false)
    private Float locationLon;

    //состояние
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    //инициатор
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Override
    public int hashCode() {
        return id.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return (this == obj) || (obj.getClass() == Event.class) && ((Event) obj).getId().equals(id);
    }
}
