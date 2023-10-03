package ru.practicum.ewm.model;

import ru.practicum.ewm.StatInDto;

public class StatDtoMapper {
    public static Hit toHit(StatInDto statInDto) {
        return new Hit(null, statInDto.getApp(), statInDto.getUri(), statInDto.getIp(), statInDto.getTimestamp());
    }

}
