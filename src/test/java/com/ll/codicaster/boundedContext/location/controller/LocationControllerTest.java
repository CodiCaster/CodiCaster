package com.ll.codicaster.boundedContext.location.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class LocationControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("메인 업데이트 시 리다이렉션")
    @WithUserDetails("user1")
    void updateMainTest() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/location/update/main")
                        .with(csrf()) // CSRF 키 생성
                        .param("latitude", "37.39480276122528")
                        .param("longitude", "127.1111922202697")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LocationController.class))
                .andExpect(handler().methodName("updateMain"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    @DisplayName("마이페이지 업데이트 시 리다이렉션")
    @WithUserDetails("user1")
    void updateMeTest() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/location/update/me")
                        .with(csrf()) // CSRF 키 생성
                        .param("latitude", "37.39480276122528")
                        .param("longitude", "127.1111922202697")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LocationController.class))
                .andExpect(handler().methodName("updateMe"))
                .andExpect(status().is3xxRedirection())
        ;
    }
}