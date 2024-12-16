package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // 스프링부트 애플리케이션 전체를 로딩하여 통합테스트를 수행하는 어노테이션. 컨트롤러를 테스트하기 위한 필수적인 요소
@AutoConfigureMockMvc // @AutoConfigureMockMvc : MockMvc를 자동으로 설정하여 HTTP 요청을 시뮬레이션 함.
public class PointControllerTest {
    /**
     * MockMvc
     * 그냥 실행시켜주는 척 해주는 가짜객체
     * 실제 웹서버 없이 HTTP 요청을 보내고 응답을 받는 객체다.
     */
    @Autowired
    private MockMvc mockMvc;


    // requestBody에 정의한 json데이터를 객체로 시리얼라이즈함.
    // json -> 객체
    // 객체 -> json 로 변경
    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private PointService pointService;
    private PointController controller;

    @Test
    void 특정_유저의_포인트_조회가_가능하다 () throws Exception {
        // given
        long userId = 1;

        // when
        // then
        /**
         * MockMvcRequestBuilders: HTTP 요청을 생성하는 클래스
         */
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/point/" + userId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(0));
    }

    @Test
    void 특정_유저의_포인트_내역조회가_가능하다 () throws Exception {
        // given
        long userId = 1;

        // when
        // then
         mockMvc.perform(MockMvcRequestBuilders.get("/point/" + userId + "/histories")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());

    }

    @Test
    void 특정_유저의_포인트_충전이_가능하다 () throws Exception {
        // given
        long userId = 1;
        long amount = 1000;

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount))
                )
                .andExpect(status().isOk());
    }

    @Test
    void 특정_유저의_포인트_사용이_가능하다 () throws Exception {
        // given
        long userId = 1;
        long amount = 10000;
        // amount = 10000원 충전
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount))
                )
                .andExpect(status().isOk());
        // when
        // then
        // 500원 사용
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(500))
                )
                .andExpect(status().isOk());
    }
}
