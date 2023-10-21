package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDtoMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository compilationRepository;
    EventRepository eventRepository;

    //////////////////////////// Публичные запросы ///////////////////////////

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        if (pinned != null) { //нужно учитывать закрепленные/незакрепленные
            return compilationRepository.findByPinnedIs(pinned, pageable)
                    .map(CompilationDtoMapper::toCompilationDto)
                    .getContent();
        } else { //выводим все
            return compilationRepository.findAll(pageable)
                    .map(CompilationDtoMapper::toCompilationDto)
                    .getContent();
        }
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с идентификатором " + compId + " не найдена")
        );
        return CompilationDtoMapper.toCompilationDto(compilation);
    }

    ////////////////////////// Создание и обновление /////////////////////////

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Set<Long> eventIds;
        if (dto.getEvents() != null) {
            eventIds = new HashSet<>(dto.getEvents());
        } else {
            eventIds = new HashSet<>();
        }
        Set<Event> events = eventRepository.findByIdIn(eventIds);
        if (events.size() < eventIds.size()) { //не все события найдены
            throw new BadRequestException("Не все события найдены");
        }
        Compilation compilation = CompilationDtoMapper.toCompilation(dto, events);
        Compilation newCompilation = compilationRepository.save(compilation);
        return CompilationDtoMapper.toCompilationDto(newCompilation);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с идентификатором " + compId + " не найдена")
        );
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            compilation.setEvents(getEventsFromIds(dto.getEvents()));
        }
        Compilation newCompilation = compilationRepository.save(compilation);
        return CompilationDtoMapper.toCompilationDto(newCompilation);
    }

    //////////////////////////////// Удаление ////////////////////////////////

    public void deleteCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с идентификатором " + compId + " не найдена")
        );
        compilationRepository.delete(compilation);
    }

    /////////////////////////////// Конвертация //////////////////////////////

    private Set<Event> getEventsFromIds(List<Long> ids) {
        Set<Long> eventIds = new HashSet<>(ids);
        Set<Event> events = eventRepository.findByIdIn(eventIds);
        if (events.size() < eventIds.size()) { //не все события найдены
            throw new BadRequestException("Не все события найдены");
        }
        return events;
    }
}