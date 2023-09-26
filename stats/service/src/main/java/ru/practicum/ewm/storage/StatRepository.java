package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.StatOutDto;
import ru.practicum.ewm.model.Hit;

import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.practicum.ewm.StatOutDto(h.app as a, h.uri as u, count(h.ip) as c) from Hit h " +
            "where h.timestamp >= ?1 and h.timestamp <= ?2 " +
            "group by a, u " +
            "order by c desc ")
    List<StatOutDto> getAllHits(String start, String end);

    @Query("select new ru.practicum.ewm.StatOutDto(h.app as a, h.uri as u, count(h.ip) as c) from Hit h " +
            "where h.timestamp >= ?1 and h.timestamp <= ?2 and h.uri in ?3 " +
            "group by a, u " +
            "order by c desc ")
    List<StatOutDto> getHitsForUris(String start, String end, String[] uris);

    @Query("select new ru.practicum.ewm.StatOutDto(h.app as a, h.uri as u, count(distinct h.ip) as c) from Hit h " +
            "where h.timestamp >= ?1 and h.timestamp <= ?2 " +
            "group by a, u " +
            "order by c desc ")
    List<StatOutDto> getAllUniqueHits(String start, String end);

    @Query("select new ru.practicum.ewm.StatOutDto(h.app as a, h.uri as u, count(distinct h.ip) as c) from Hit h " +
            "where h.timestamp >= ?1 and h.timestamp <= ?2 and h.uri in ?3 " +
            "group by a, u " +
            "order by c desc ")
    List<StatOutDto> getUniqueHitsForUris(String start, String end, String[] uris);
}
