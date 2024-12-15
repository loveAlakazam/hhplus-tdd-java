package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PointHistoryTableTest {

    // PointHistoryTable 내의 public 접근제어자 메소드를 테스트한다.
    private final PointHistoryTable repository = new PointHistoryTable();


    @AfterEach
    private void tearDown() {
    }

    @Test
    @DisplayName("맨처음에 PointHistory 를 생성한다")
    public void 포인트_내역_생성 () {
        // given
        long inputUserId = 1;
        long inputAmount = 1000;
        TransactionType inputTransactionType = TransactionType.CHARGE;
        long inputUpdateMillis = 500;

        // when
        PointHistory firstPointHistory = repository.insert(inputUserId, inputAmount, inputTransactionType, inputUpdateMillis);

        // then
        Assertions.assertEquals(1, firstPointHistory.id());
        Assertions.assertEquals(inputUserId, firstPointHistory.userId());
        Assertions.assertEquals(inputAmount, firstPointHistory.amount());
        Assertions.assertEquals(inputTransactionType, firstPointHistory.type());
    }


    @Test
    @DisplayName("PointHistory 를 userId로 조회하면 빈리스트를 리턴한다")
    public void 유저아이디로_포인트내역_조회결과_없음() {
        // given
        long userId = 1;

        // when
        List<PointHistory> result = repository.selectAllByUserId(userId);

        // then
        Assertions.assertEquals(0 , result.size());
    }

    @Test
    @DisplayName("PointHistory 를 userId로 조회가 가능하다")
    public void 유저아이디로_포인트내역_조회() {
        // given
        // 조회 대상 유저아이디
        long targetUserId = 2;

        // 조회 대상 유저아이디를 포함한 포인트히스토리 데이터를 셋팅한다
        for (int i = 1; i <= 3; i++) {
            long userId = i;
            long amount = i * 1000;
            TransactionType transactionType = TransactionType.CHARGE;
            if(i % 2 ==0)
                transactionType = TransactionType.USE;
            repository.insert(userId, amount, transactionType, 1000 * i);
        }

        // when
        List<PointHistory> result = repository.selectAllByUserId(targetUserId);

        // then
        Assertions.assertEquals(1, result.size());
    }

}
