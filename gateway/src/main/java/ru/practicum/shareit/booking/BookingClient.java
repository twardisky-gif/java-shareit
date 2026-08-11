package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

/**
 * REST-клиент основного сервиса для операций с бронированиями.
 */
@Service
public class BookingClient extends BaseClient {

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(long userId, Object body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> approve(long ownerId, long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", ownerId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getByBooker(long userId, BookingState state) {
        return get("?state={state}", userId, Map.of("state", state.name()));
    }

    public ResponseEntity<Object> getByOwner(long ownerId, BookingState state) {
        return get("/owner?state={state}", ownerId, Map.of("state", state.name()));
    }
}
