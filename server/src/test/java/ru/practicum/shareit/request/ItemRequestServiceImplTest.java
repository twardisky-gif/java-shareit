package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateRequest() {
        User requestor = createUser("requestor-create@test.test");

        ItemRequestDto created = itemRequestService.create(
                requestor.getId(), new ItemRequestCreateDto("Нужна дрель"));

        assertNotNull(created.getId());
        assertNotNull(created.getCreated());
        assertEquals("Нужна дрель", created.getDescription());
        assertTrue(created.getItems().isEmpty());
    }

    @Test
    void shouldReturnOwnRequestsFromNewestToOldestWithItems() {
        User requestor = createUser("requestor-own@test.test");
        User owner = createUser("owner-own@test.test");
        ItemRequestDto older = itemRequestService.create(
                requestor.getId(), new ItemRequestCreateDto("Нужна отвёртка"));
        ItemRequestDto newer = itemRequestService.create(
                requestor.getId(), new ItemRequestCreateDto("Нужен шуруповёрт"));
        itemService.create(owner.getId(), new ItemCreateDto(
                "Шуруповёрт", "Аккумуляторный", true, newer.getId()));

        List<ItemRequestDto> requests = itemRequestService.getOwn(requestor.getId());

        assertEquals(List.of(newer.getId(), older.getId()),
                requests.stream().map(ItemRequestDto::getId).toList());
        assertEquals(1, requests.getFirst().getItems().size());
        assertEquals(owner.getId(), requests.getFirst().getItems().getFirst().getOwnerId());
    }

    @Test
    void shouldReturnOnlyOtherUsersRequests() {
        User viewer = createUser("viewer-all@test.test");
        User other = createUser("requestor-all@test.test");
        ItemRequestDto own = itemRequestService.create(
                viewer.getId(), new ItemRequestCreateDto("Собственный запрос"));
        ItemRequestDto foreign = itemRequestService.create(
                other.getId(), new ItemRequestCreateDto("Чужой запрос"));

        List<ItemRequestDto> requests = itemRequestService.getOtherUsersRequests(viewer.getId());

        assertTrue(requests.stream().noneMatch(request -> request.getId().equals(own.getId())));
        assertTrue(requests.stream().anyMatch(request -> request.getId().equals(foreign.getId())));
    }

    @Test
    void shouldReturnRequestByIdToAnyUserWithResponseItems() {
        User requestor = createUser("requestor-by-id@test.test");
        User viewer = createUser("viewer-by-id@test.test");
        User owner = createUser("owner-by-id@test.test");
        ItemRequestDto created = itemRequestService.create(
                requestor.getId(), new ItemRequestCreateDto("Нужна камера"));
        itemService.create(owner.getId(), new ItemCreateDto(
                "Камера", "Цифровая", true, created.getId()));

        ItemRequestDto found = itemRequestService.getById(viewer.getId(), created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(1, found.getItems().size());
        assertEquals("Камера", found.getItems().getFirst().getName());
        assertEquals(owner.getId(), found.getItems().getFirst().getOwnerId());
    }

    private User createUser(String email) {
        return userRepository.save(User.builder()
                .name("Пользователь")
                .email(email)
                .build());
    }
}
