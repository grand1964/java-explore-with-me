package ru.practicum.ewm.compilation.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findByPinnedIs(boolean pinned, Pageable pageable);

    /*@Query("select c from Compilation c where c.pinned = ?1 ")
    List<Compilation> getCompilationsWithPinned(boolean pinned);*/
}
