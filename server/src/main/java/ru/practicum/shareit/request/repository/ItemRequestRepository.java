package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * Репозиторий для чтения и сохранения запросов вещей.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);

    /**
     * Возвращает запрос по идентификатору или выбрасывает исключение.
     *
     * @param requestId идентификатор запроса
     * @return найденный запрос
     * @throws NotFoundException если запрос не найден
     */
    default ItemRequest requireById(Long requestId) {
        return findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
    }
}
