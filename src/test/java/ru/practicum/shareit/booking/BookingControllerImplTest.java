package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerImplTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBookingInWaitingStatus() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = start.plusDays(1);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookingCreateDto(item.getId(), start, end))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.item.name").value(item.getName()));
    }

    @Test
    void shouldKeepSecondsInBookingDates() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime end = start.plusDays(1);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookingCreateDto(item.getId(), start, end))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value(start.toString() + ":00"))
                .andExpect(jsonPath("$.end").value(end.toString() + ":00"));
    }

    @Test
    void shouldRejectBookingOfUnavailableItem() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), false);

        mockMvc.perform(bookingRequest(booker.getId(), item.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBookingByUnknownUser() throws Exception {
        UserDto owner = createUser();
        ItemDto item = createItem(owner.getId(), true);

        mockMvc.perform(bookingRequest(9999L, item.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectBookingOfUnknownItem() throws Exception {
        UserDto booker = createUser();

        mockMvc.perform(bookingRequest(booker.getId(), 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectBookingOfOwnItem() throws Exception {
        UserDto owner = createUser();
        ItemDto item = createItem(owner.getId(), true);

        mockMvc.perform(bookingRequest(owner.getId(), item.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectBookingWithEndBeforeStart() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        LocalDateTime start = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = start.plusDays(1);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookingCreateDto(item.getId(), end, start))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBookingWithEqualStartAndEnd() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookingCreateDto(item.getId(), start, start))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBookingWithStartInPast() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        LocalDateTime start = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookingCreateDto(item.getId(), start, start.plusDays(2)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBookingWithoutDates() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":" + item.getId() + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldApproveBookingByOwner() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(USER_ID_HEADER, owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldRejectBookingByOwner() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(USER_ID_HEADER, owner.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void shouldForbidApproveByNotOwner() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(USER_ID_HEADER, booker.getId())
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidApproveByUnknownUser() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(USER_ID_HEADER, 9999)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectRepeatedApprove() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(USER_ID_HEADER, owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(USER_ID_HEADER, owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBookingToBookerAndOwner() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(get("/bookings/" + booking.getId()).header(USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
        mockMvc.perform(get("/bookings/" + booking.getId()).header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void shouldHideBookingFromStranger() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        UserDto stranger = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(get("/bookings/" + booking.getId()).header(USER_ID_HEADER, stranger.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBookerBookingsByState() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(booking.getId()));
        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, booker.getId()).param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, booker.getId()).param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, booker.getId()).param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, booker.getId()).param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnOwnerBookings() throws Exception {
        UserDto owner = createUser();
        UserDto booker = createUser();
        ItemDto item = createItem(owner.getId(), true);
        BookingDto booking = createBooking(booker.getId(), item.getId());

        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(booking.getId()));
        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldRejectUnknownState() throws Exception {
        UserDto booker = createUser();

        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, booker.getId()).param("state", "UNSUPPORTED"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBookingListsForUnknownUser() throws Exception {
        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, 9999))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectBookingWithoutUserHeader() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder bookingRequest(Long userId, Long itemId) throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        return post("/bookings")
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new BookingCreateDto(itemId, start, start.plusDays(1))));
    }

    private BookingDto createBooking(Long bookerId, Long itemId) throws Exception {
        String response = mockMvc.perform(bookingRequest(bookerId, itemId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(response, BookingDto.class);
    }

    private UserDto createUser() throws Exception {
        UserCreateDto request = new UserCreateDto(
                "Пользователь" + COUNTER.incrementAndGet(),
                "user" + COUNTER.incrementAndGet() + "@bookings.test");
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(response, UserDto.class);
    }

    private ItemDto createItem(Long ownerId, boolean available) throws Exception {
        String response = mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemCreateDto(
                                "Вещь" + COUNTER.incrementAndGet(), "Описание вещи", available))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(response, ItemDto.class);
    }
}
