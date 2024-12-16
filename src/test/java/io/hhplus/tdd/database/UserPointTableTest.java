package io.hhplus.tdd.database;


import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class UserPointTableTest {


    // UserPointTable 객체내의 public 메소드를 테스트하기위해서 UserPointTable 인스턴스를 호출했다.
    private UserPointTable repository = new UserPointTable();

    @AfterEach
    private void tearDown() {

    }


    @Test
    @DisplayName("신규 유저포인트를 생성할 수 있다")
    public void 신규_유저포인트_생성 () {
        // given
        long inputUserId = 1;
        long inputAmount = 1000;

        // when
        UserPoint newUserPoint = repository.insertOrUpdate(inputUserId, inputAmount);

        // then
        Assertions.assertEquals(inputUserId, newUserPoint.id());
        Assertions.assertEquals(inputAmount, newUserPoint.point());
    }


    @Test
    @DisplayName("userId에 해당되는 유저포인트가 존재하지 않으면 amount: 0 인 유저포인트를 생성한다")
    public void 유저가_존재하지_않을_경우() {
        // given
        long userId = 1;

        // when
        UserPoint result = repository.selectById(userId);

        // then
        Assertions.assertEquals(userId, result.id());
        Assertions.assertEquals(0, result.point());
    }


    @Test
    @DisplayName("userId에 해당되는 유저포인트가 존재한다")
    public void 유저가_존재할_경우() {
        // given
        // 조회대상 유저아이디
        long targetUserId = 2;

        // targetUserId를 포함한 유저포인트 데이터를 적재한다
        for (int i = 1; i <= 3; i++) {
            long userId = i;
            long amount = userId * 1000;
            repository.insertOrUpdate(userId, amount);
        }

        // when
        UserPoint result = repository.selectById(targetUserId);

        // then
        Assertions.assertEquals(2000 , result.point());
        Assertions.assertTrue(result.point() != 0);
    }

}
