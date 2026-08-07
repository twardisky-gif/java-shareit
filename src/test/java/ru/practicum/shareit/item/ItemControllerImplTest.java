package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerImplTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateItem() throws Exception {
        UserDto owner = createUser();
        ItemCreateDto request = new ItemCreateDto("Дрель", "Ударная дрель", true);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldRejectCreateWithoutOwnerHeader() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemCreateDto("Дрель", "Описание", true))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateForUnknownOwner() throws Exception {
        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemCreateDto("Дрель", "Описание", true))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectCreateWithoutAvailable() throws Exception {
        UserDto owner = createUser();

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Дрель\",\"description\":\"Описание\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        UserDto owner = createUser();

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"Описание\",\"available\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithoutDescription() throws Exception {
        UserDto owner = createUser();

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Дрель\",\"available\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateSingleFieldAndKeepOthers() throws Exception {
        UserDto owner = createUser();
        ItemDto item = createItem(owner.getId(), "Дрель", "Ударная дрель", true);

        mockMvc.perform(patch("/items/" + item.getId())
                        .header(USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemUpdateDto(null, null, false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void shouldRejectUpdateByNonOwner() throws Exception {
        UserDto owner = createUser();
        UserDto stranger = createUser();
        ItemDto item = createItem(owner.getId(), "Дрель", "Ударная дрель", true);

        mockMvc.perform(patch("/items/" + item.getId())
                        .header(USER_ID_HEADER, stranger.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemUpdateDto("Чужая правка", null, null))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnItemToAnyUser() throws Exception {
        UserDto owner = createUser();
        UserDto reader = createUser();
        ItemDto item = createItem(owner.getId(), "Дрель", "Ударная дрель", true);

        mockMvc.perform(get("/items/" + item.getId()).header(USER_ID_HEADER, reader.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
    }

    @Test
    void shouldReturnOnlyItemsOfRequestedOwner() throws Exception {
        UserDto owner = createUser();
        UserDto otherOwner = createUser();
        createItem(owner.getId(), "Дрель", "Ударная дрель", true);
        createItem(owner.getId(), "Пила", "Острая пила", true);
        createItem(otherOwner.getId(), "Молоток", "Тяжёлый молоток", true);

        mockMvc.perform(get("/items").header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/items").header(USER_ID_HEADER, otherOwner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldFindAvailableItemIgnoringCase() throws Exception {
        UserDto owner = createUser();
        String marker = "Перфоратор" + COUNTER.incrementAndGet();
        ItemDto item = createItem(owner.getId(), marker, "Мощный инструмент", true);

        mockMvc.perform(get("/items/search").param("text", marker.toUpperCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(item.getId()));
    }

    @Test
    void shouldFindItemByDescription() throws Exception {
        UserDto owner = createUser();
        String marker = "шуруповёрт" + COUNTER.incrementAndGet();
        ItemDto item = createItem(owner.getId(), "Инструмент", "Аккумуляторный " + marker, true);

        mockMvc.perform(get("/items/search").param("text", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(item.getId()));
    }

    @Test
    void shouldNotFindUnavailableItem() throws Exception {
        UserDto owner = createUser();
        String marker = "Граммофон" + COUNTER.incrementAndGet();
        createItem(owner.getId(), marker, "Редкая вещь", false);

        mockMvc.perform(get("/items/search").param("text", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnEmptyListForBlankSearch() throws Exception {
        UserDto owner = createUser();
        createItem(owner.getId(), "Лестница", "Стремянка", true);

        mockMvc.perform(get("/items/search").param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldRejectBlankNameOnUpdate() throws Exception {
        UserDto owner = createUser();
        ItemDto item = createItem(owner.getId(), "Дрель", "Ударная дрель", true);

        mockMvc.perform(patch("/items/" + item.getId())
                        .header(USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/items/" + item.getId()))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void shouldReturnBadRequestForNonNumericItemId() throws Exception {
        mockMvc.perform(get("/items/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSearchTextMissing() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRemoveItemsWhenOwnerDeleted() throws Exception {
        UserDto owner = createUser();
        String marker = "Сироталампа" + COUNTER.incrementAndGet();
        ItemDto item = createItem(owner.getId(), marker, "Останется без владельца", true);

        mockMvc.perform(delete("/users/" + owner.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/items/" + item.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/items/search").param("text", marker))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private UserDto createUser() throws Exception {
        UserCreateDto request = new UserCreateDto(
                "Владелец" + COUNTER.incrementAndGet(),
                "owner" + COUNTER.incrementAndGet() + "@items.test");
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, UserDto.class);
    }

    private ItemDto createItem(Long ownerId, String name, String description, boolean available) throws Exception {
        String response = mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ItemCreateDto(name, description, available))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, ItemDto.class);
    }
}
