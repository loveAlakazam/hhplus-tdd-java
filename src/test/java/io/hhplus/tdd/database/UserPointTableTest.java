package io.hhplus.tdd.database;


import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class UserPointTableTest {
    // 테스트에서만 사용되는 임시테이블이다.
    private final Map<Long, UserPoint> table = new HashMap<>();

    // UserPointTable 객체내의 public 메소드를 테스트하기위해서 UserPointTable 인스턴스를 호출했다.
    private UserPointTable database = new UserPointTable();

    // 테스트가 끝나면 table의 데이터를 비운다.
    @AfterEach
    private void tearDown() {
        table.clear();
    }



    @Test
    @DisplayName("신규 유저포인트를 생성할 수 있다")
    public void 신규_유저포인트_생성 () {
        // given
        long inputUserId = 1;
        long inputAmount = 1000;
        UserPoint newUserPoint = database.insertOrUpdate(inputUserId, inputAmount);

        // when
        table.put(newUserPoint.id(), newUserPoint);

        // then - assertEquals( 좌: expected, 우: result )
        Assertions.assertEquals(inputUserId, newUserPoint.id());
        Assertions.assertEquals(inputAmount, newUserPoint.point());
        Assertions.assertEquals(1, table.size());
    }


    @Test
    @DisplayName("userId에 해당되는 유저포인트가 존재하지 않으면 amount: 0 인 유저포인트를 생성한다")
    public void 유저가_존재하지_않을경우() {
        // given
        long userId = 1;

        // when
        UserPoint result = database.selectById(userId);

        // then
        Assertions.assertEquals(userId, result.id());
        Assertions.assertEquals(0, result.point());
    }


    @Test
    @DisplayName("userId에 해당되는 유저포인트가 존재한다")
    public void 유저가_존재할_경우() {
        // given
        long targetUserId = 2;
        for(int i=1 ; i<=3; i++) {
            long userId = i;
            long amount = userId * 1000;
            table.put(userId, database.insertOrUpdate(userId, amount));
        }


        // when
        UserPoint expectTargetUser = table.get(targetUserId);
        UserPoint result = database.selectById(targetUserId);

        // then
        Assertions.assertEquals(expectTargetUser.point(), result.point());
        Assertions.assertTrue(result.point() != 0);
    }

}
