package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

/**
 * Репозиторий для чтения и изменения вещей.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long ownerId);

    @Query("select i from Item i "
            + "where i.available = true "
            + "and (upper(i.name) like upper(concat('%', :text, '%')) escape '\\' "
            + "or upper(i.description) like upper(concat('%', :text, '%')) escape '\\')")
    List<Item> searchAvailableByText(@Param("text") String text);

    List<Item> findByRequestIdInOrderById(Collection<Long> requestIds);

    /**
     * Возвращает вещь по идентификатору или выбрасывает исключение.
     *
     * @param itemId идентификатор вещи
     * @return найденная вещь
     * @throws NotFoundException если вещь не найдена
     */
    default Item requireById(Long itemId) {
        return findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }
}
