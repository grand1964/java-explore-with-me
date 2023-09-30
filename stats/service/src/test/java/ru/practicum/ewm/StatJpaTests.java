package ru.practicum.ewm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.StatDtoMapper;
import ru.practicum.ewm.storage.StatRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class StatJpaTests {
    private static final String start = "2023-09-06 17:00:17";
    private static final String end = "2023-09-06 18:00:17";
    private static List<StatInDto> hits;
    @Autowired
    private TestEntityManager em;

    @Autowired
    private StatRepository statRepository;

    @BeforeAll
    public static void setUp() {
        hits = new ArrayList<>();
        hits.add(new StatInDto(
                "ewm-main-service", "/events", "192.168.1.1", "2023-09-06 17:00:17"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events", "192.168.1.2", "2023-09-06 17:00:55"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events", "192.168.1.1", "2023-09-06 18:00:17"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events", "192.168.10.1", "2023-09-06 18:00:23"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events/1", "192.168.10.1", "2023-09-06 17:00:23"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events/1", "192.168.10.1", "2023-09-06 18:00:17"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events/1", "192.168.1.1", "2023-09-06 18:00:23"));
        hits.add(new StatInDto(
                "ewm-main-service", "/events/2", "192.168.0.1", "2023-09-06 17:00:23"));
    }

    @BeforeEach
    public void initAll() {
        for (StatInDto hit : hits) {
            statRepository.save(StatDtoMapper.toHit(hit));
        }
    }

    @AfterEach
    public void clearAll() {
        statRepository.deleteAll();
    }

    @Test
    public void getAllTest() {
        List<Hit> hits = statRepository.findAll();
        assertEquals(hits.size(), 8);
        for (Hit hit : hits) {
            System.out.println(hit.getApp());
            System.out.println(hit.getUri());
            System.out.println(hit.getIp());
            System.out.println(hit.getTimestamp());
        }

    }

    @Test
    public void getAllHitsTest() {
        List<StatOutDto> dtos = statRepository.getAllHits(start, end);
        assertEquals(dtos.size(), 3);
        assertEquals(dtos.get(0).getUri(), "/events");
        assertEquals(dtos.get(0).getHits(), 3);
        assertEquals(dtos.get(1).getUri(), "/events/1");
        assertEquals(dtos.get(1).getHits(), 2);
        assertEquals(dtos.get(2).getUri(), "/events/2");
        assertEquals(dtos.get(2).getHits(), 1);
    }

    @Test
    public void getHitsForUrisTest() {
        String[] uris = {"/events", "/events/2"};
        List<StatOutDto> dtos = statRepository.getHitsForUris(start, end, uris);
        assertEquals(dtos.size(), 2);
        assertEquals(dtos.get(0).getUri(), "/events");
        assertEquals(dtos.get(0).getHits(), 3);
        assertEquals(dtos.get(1).getUri(), "/events/2");
        assertEquals(dtos.get(1).getHits(), 1);
    }

    @Test
    public void getAllUniqueHitsTest() {
        List<StatOutDto> dtos = statRepository.getAllUniqueHits(start, end);
        assertEquals(dtos.size(), 3);
        assertEquals(dtos.get(0).getUri(), "/events");
        assertEquals(dtos.get(0).getHits(), 2);
        assertEquals(dtos.get(1).getUri(), "/events/1");
        assertEquals(dtos.get(1).getHits(), 1);
        assertEquals(dtos.get(2).getUri(), "/events/2");
        assertEquals(dtos.get(2).getHits(), 1);
    }

    @Test
    public void getUniqueHitsForUrisTest() {
        String[] uris = {"/events", "/events/1"};
        List<StatOutDto> dtos = statRepository.getUniqueHitsForUris(start, end, uris);
        assertEquals(dtos.size(), 2);
        assertEquals(dtos.get(0).getUri(), "/events");
        assertEquals(dtos.get(0).getHits(), 2);
        assertEquals(dtos.get(1).getUri(), "/events/1");
        assertEquals(dtos.get(1).getHits(), 1);
    }
}
