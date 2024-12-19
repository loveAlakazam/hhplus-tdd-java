package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

// 동시성제어 통합테스트 만들기
@SpringBootTest
@AutoConfigureMockMvc
public class PointServiceConcurrencyTest {

    /**
     * MockMvc
     * 그냥 실행시켜주는 척 해주는 가짜객체
     * 실제 웹서버 없이 HTTP 요청을 보내고 응답을 받는 객체다.
     */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointRepository;

    @Test
    @DisplayName("동일 유저에 대한 동시성 테스트")
    public void 동일한_유저가_동시에_포인트를_여러번_충전하는_시나리오를_성공한다() throws InterruptedException {
        //given
        // 새로운 유저 데이터 생성 및 저장
        long userId = 3L;
        long 초기충전량 = 1000L;
        userPointRepository.insertOrUpdate(userId, 초기충전량);


        // 스레드 풀 생성 (10개의 스레드)
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // CountDownLatch를 사용해 10개의 스레드를 동기화
        /**
         * CountDownLatch
         * - 멀티스레드 프로그래밍에서 스레드들이 모든 작업을 마친후 특정한 작업을 할 때
         *      다른 스레드들에서 일련의 작업이 완료될 때까지 대기하도록 Sync를 맞춰주는 기능이다.
         */
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    pointService.usePoint(userId, 100); // 100 포인트 감소
                } finally {
                    latch.countDown(); // 작업 완료 시 카운트 감소
                }
            });
        }

        // when
        latch.await(); // 모든 스레드가 작업 완료할 때까지 대기
        UserPoint updatedUser = userPointRepository.selectById(userId);

        // then
        assertThat(updatedUser.point()).isEqualTo(0); // 남은 포인트가 0이어야 함
    }


    @Test
    public void 서로다른_유저들이_동시에_호출했을_경우_순서대로_진행하여_성공한다() throws InterruptedException {
        // 서로 다른 유저에 대한 동시성 테스트

        // 두 유저 데이터 생성 및 저장
        long userOneId = 1L;
        long userOneSavedPoint = 1000L;
        UserPoint user1 = userPointRepository.insertOrUpdate(userOneId, userOneSavedPoint);

        long userTwoId = 2L;
        long userTwoSavedPoint = 1000L;
        UserPoint user2 = userPointRepository.insertOrUpdate(userTwoId, userTwoSavedPoint);

        // 스레드 풀 생성 (10개의 스레드)
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // CountDownLatch를 사용해 10개의 스레드를 동기화
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    if (finalI % 2 == 0) {
                        pointService.usePoint(user1.id(), 100); // User1 포인트 감소
                    } else {
                        pointService.usePoint(user2.id(), 100); // User2 포인트 감소
                    }
                } finally {
                    latch.countDown(); // 작업 완료 시 카운트 감소
                }
            });
        }

        latch.await(); // 모든 스레드가 작업 완료할 때까지 대기

        UserPoint updatedUser1 = userPointRepository.selectById(user1.id());
        UserPoint updatedUser2 = userPointRepository.selectById(user2.id());


        // 남은 포인트가 각각 500이어야 함
        assertThat(updatedUser1.point()).isEqualTo(500);
        assertThat(updatedUser2.point()).isEqualTo(500);
    }

}