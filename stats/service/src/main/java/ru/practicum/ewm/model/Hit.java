package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    private String app;

    @Column(name = "uri", length = 32)
    private String uri;

    @Column(name = "ip", length = 16)
    private String ip;

    @Column(name = "timestamp", length = 32)
    private String timestamp;

    @Override
    public int hashCode() {
        return id.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return (this == obj) || (obj.getClass() == Hit.class) && ((Hit) obj).getId().equals(id);
    }
}
