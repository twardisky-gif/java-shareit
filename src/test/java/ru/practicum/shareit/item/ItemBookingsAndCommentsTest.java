package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemBookingsAndCommentsTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void shouldReturnBookingDatesOnlyToOwner() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId());
        LocalDateTime now = LocalDateTime.now();
        saveBooking(item.getId(), booker.getId(), now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);
        saveBooking(item.getId(), booker.getId(), now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);

        mockMvc.perform(get("/items/" + item.getId()).header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking.bookerId").value(booker.getId()))
                .andExpect(jsonPath("$.nextBooking.bookerId").value(booker.getId()));

        JsonNode body = readItem(item.getId(), booker.getId());
        assertTrue(body.has("lastBooking") && body.get("lastBooking").isNull());
        assertTrue(body.has("nextBooking") && body.get("nextBooking").isNull());
        assertTrue(body.has("comments"));
    }

    @Test
    void shouldReturnBookingDatesInOwnerItemList() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId());
        LocalDateTime now = LocalDateTime.now();
        saveBooking(item.getId(), booker.getId(), now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);

        mockMvc.perform(get("/items").header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lastBooking.bookerId").value(booker.getId()))
                .andExpect(jsonPath("$[0].nextBooking").isEmpty());
    }

    @Test
    void shouldAddCommentAfterFinishedBooking() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId());
        LocalDateTime now = LocalDateTime.now();
        saveBooking(item.getId(), booker.getId(), now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);

        mockMvc.perform(commentRequest(booker.getId(), item.getId(), "Отличная вещь"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("Отличная вещь"))
                .andExpect(jsonPath("$.authorName").value(booker.getName()))
                .andExpect(jsonPath("$.created").isNotEmpty());

        mockMvc.perform(get("/items/" + item.getId()).header(USER_ID_HEADER, booker.getId()))
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.comments[0].text").value("Отличная вещь"));
        mockMvc.perform(get("/items").header(USER_ID_HEADER, owner.getId()))
                .andExpect(jsonPath("$[0].comments.length()").value(1));
    }

    @Test
    void shouldRejectCommentWhenBookingNotFinished() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId());
        LocalDateTime now = LocalDateTime.now();
        saveBooking(item.getId(), booker.getId(), now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);

        mockMvc.perform(commentRequest(booker.getId(), item.getId(), "Ещё не пользовался"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCommentFromUserWithoutBooking() throws Exception {
        UserDto owner = createUser();
        UserDto stranger = createUser();
        ItemDto item = createItem(owner.getId());

        mockMvc.perform(commentRequest(stranger.getId(), item.getId(), "Не арендовал"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCommentForRejectedBooking() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId());
        LocalDateTime now = LocalDateTime.now();
        saveBooking(item.getId(), booker.getId(), now.minusDays(2), now.minusDays(1), BookingStatus.REJECTED);

        mockMvc.perform(commentRequest(booker.getId(), item.getId(), "Бронь отклонена"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankComment() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId());
        LocalDateTime now = LocalDateTime.now();
        saveBooking(item.getId(), booker.getId(), now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);

        mockMvc.perform(commentRequest(booker.getId(), item.getId(), "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCommentForUnknownItem() throws Exception {
        UserDto booker = createUser();

        mockMvc.perform(commentRequest(booker.getId(), 9999L, "Нет такой вещи"))
                .andExpect(status().isNotFound());
    }

    private JsonNode readItem(Long itemId, Long userId) throws Exception {
        String response = mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(response);
    }

    private MockHttpServletRequestBuilder commentRequest(Long userId, Long itemId, String text) throws Exception {
        return post("/items/" + itemId + "/comment")
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CommentCreateDto(text)));
    }

    private void saveBooking(Long itemId, Long bookerId, LocalDateTime start, LocalDateTime end,
                             BookingStatus status) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        User booker = userRepository.findById(bookerId).orElseThrow();
        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(status)
                .build());
    }

    private UserDto createUser() throws Exception {
        UserCreateDto request = new UserCreateDto(
                "Участник" + COUNTER.incrementAndGet(),
                "user" + COUNTER.incrementAndGet() + "@comments.test");
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(response, UserDto.class);
    }

    private ItemDto createItem(Long ownerId) throws Exception {
        String response = mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemCreateDto(
                                "Вещь" + COUNTER.incrementAndGet(), "Описание вещи", true))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(response, ItemDto.class);
    }
}
