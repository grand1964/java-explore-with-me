package ru.practicum.ewm.service;

import ru.practicum.ewm.StatInDto;
import ru.practicum.ewm.StatOutDto;

import java.util.List;

public interface StatService {
    List<StatOutDto> getStat(String start, String end, String[] uris, boolean unique);
    void putHit(StatInDto stat);
}
