package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventDtoMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.Set;

public class CompilationDtoMapper {
    public static Compilation toCompilation(NewCompilationDto dto, Set<Event> events) {
        Compilation compilation = new Compilation(null, dto.getTitle(), dto.getPinned(), events);
        if (dto.getPinned() == null) {
            compilation.setPinned(false); //значение по умолчанию
        }
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(EventDtoMapper.toEventShortDtoList(compilation.getEvents()),
                compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }
}
