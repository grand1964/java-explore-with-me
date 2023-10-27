package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "likes")
@IdClass(LikeKey.class)
public class Like {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "rating")
    private Integer rating;

    @Override
    public int hashCode() {
        return Objects.hash(user.hashCode(), event.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return (this == obj) ||
                (obj.getClass() == Like.class)
                && ((Like) obj).getUser().equals(user)
                && ((Like) obj).getEvent().equals(event);
    }
}
