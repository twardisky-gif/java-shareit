package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemRequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TestItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        itemRequestClient = new TestItemRequestClient();
        mockMvc = MockMvcBuilders.standaloneSetup(new ItemRequestController(itemRequestClient))
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void shouldForwardValidRequest() throws Exception {
        ItemRequestCreateDto request = new ItemRequestCreateDto("Нужна дрель");

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(1L, itemRequestClient.userId);
        assertEquals(request, itemRequestClient.body);
    }

    @Test
    void shouldRejectBlankDescriptionBeforeServerCall() throws Exception {
        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"   \"}"))
                .andExpect(status().isBadRequest());

        assertNull(itemRequestClient.body);
    }

    private static class TestItemRequestClient extends ItemRequestClient {

        private Long userId;
        private Object body;

        TestItemRequestClient() {
            super("http://localhost:9090", new RestTemplateBuilder());
        }

        @Override
        public ResponseEntity<Object> create(long userId, Object body) {
            this.userId = userId;
            this.body = body;
            return ResponseEntity.ok().build();
        }
    }
}
