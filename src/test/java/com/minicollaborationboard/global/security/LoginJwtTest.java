package com.minicollaborationboard.global.security;

import com.minicollaborationboard.domain.auth.repository.UserRepository;
import com.minicollaborationboard.domain.auth.entity.Role;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.domain.auth.entity.UserStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginJwtTest {

    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "password";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    TransactionTemplate transactionTemplate;

    @BeforeEach
    void 테스트유저_등록() {
        transactionTemplate.executeWithoutResult(status -> {
            userRepository.deleteAllByEmail(TEST_EMAIL);
            userRepository.save(User.builder()
                    .email(TEST_EMAIL)
                    .password(passwordEncoder.encode(TEST_PASSWORD))
                    .name("테스트")
                    .uuid(UUID.randomUUID().toString())
                    .status(UserStatus.ACTIVE)
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void 정상_로그인() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_EMAIL)
                        .param("password", TEST_PASSWORD))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(status().isOk());
    }

    @Test
    void 비회원_로그인() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "test1234@test.com")
                        .param("password", TEST_PASSWORD))
                .andExpect(header().doesNotExist("Authorization"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 비밀번호오입력_로그인() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_EMAIL)
                        .param("password", "tngus683"))
                .andExpect(header().doesNotExist("Authorization"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 정상접근() throws Exception {

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_EMAIL)
                        .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization");
        assertNotNull(token, "로그인 응답에 Authorization이 없습니다.");

        mockMvc.perform(get("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void 비정상접근() throws Exception {

        mockMvc.perform(get("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer 123"))
                .andExpect(status().isUnauthorized());
    }
}

