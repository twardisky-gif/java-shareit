package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerImplTest {

    private static final AtomicInteger EMAIL_COUNTER = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {
        UserCreateDto request = new UserCreateDto("Пётр", uniqueEmail());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }

    @Test
    void shouldRejectUserWithoutEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Пётр\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUserWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Пётр\",\"email\":\"user.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateEmailOnCreate() throws Exception {
        String email = uniqueEmail();
        createUser("Первый", email);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCreateDto("Второй", email))))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateOnlyNameAndKeepEmail() throws Exception {
        String email = uniqueEmail();
        UserDto created = createUser("Старое имя", email);

        mockMvc.perform(patch("/users/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateDto("Новое имя", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void shouldUpdateOnlyEmailAndKeepName() throws Exception {
        UserDto created = createUser("Неизменное имя", uniqueEmail());
        String newEmail = uniqueEmail();

        mockMvc.perform(patch("/users/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateDto(null, newEmail))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Неизменное имя"))
                .andExpect(jsonPath("$.email").value(newEmail));
    }

    @Test
    void shouldRejectUpdateWithEmailOfAnotherUser() throws Exception {
        String takenEmail = uniqueEmail();
        createUser("Занял почту", takenEmail);
        UserDto other = createUser("Претендент", uniqueEmail());

        mockMvc.perform(patch("/users/" + other.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateDto(null, takenEmail))))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldKeepOwnEmailOnUpdate() throws Exception {
        String email = uniqueEmail();
        UserDto created = createUser("Тот же", email);

        mockMvc.perform(patch("/users/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateDto("Другое имя", email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void shouldReturnNotFoundForUnknownUser() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUserAndFreeEmail() throws Exception {
        String email = uniqueEmail();
        UserDto created = createUser("Уходящий", email);

        mockMvc.perform(delete("/users/" + created.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/" + created.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCreateDto("Новый", email))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectBlankEmailOnUpdate() throws Exception {
        String email = uniqueEmail();
        UserDto created = createUser("Имя", email);

        mockMvc.perform(patch("/users/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/users/" + created.getId()))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void shouldRejectBlankNameOnUpdate() throws Exception {
        UserDto created = createUser("Имя", uniqueEmail());

        mockMvc.perform(patch("/users/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/users/" + created.getId()))
                .andExpect(jsonPath("$.name").value("Имя"));
    }

    @Test
    void shouldReturnBadRequestForNonNumericUserId() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest());
    }

    private UserDto createUser(String name, String email) throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCreateDto(name, email))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, UserDto.class);
    }

    private String uniqueEmail() {
        return "user" + EMAIL_COUNTER.incrementAndGet() + "@shareit.test";
    }
}
