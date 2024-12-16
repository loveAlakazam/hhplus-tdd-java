package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.hhplus.tdd.point.PointService.MAX_CHARGE_AMOUNT;
import static io.hhplus.tdd.point.PointService.MIN_CHARGE_AMOUNT;

/**
 * 2. 실제 서비스 코드를 만들기 전에 테스트케이스를 만든다.
 */
public class PointServiceTest {
    private PointHistoryTable pointHistoryRepository;
    private UserPointTable userPointRepository;
    private PointService pointService;

    @BeforeEach
    public void setUp() {
        this.pointHistoryRepository = new PointHistoryTable();
        this.userPointRepository = new UserPointTable();
        this.pointService = new PointServiceImpl(pointHistoryRepository, userPointRepository);
    }


    @Test
    @DisplayName("포인트 충전/사용/조회/내역조회 에서 모두 사용된다.")
    public void 유저아이디가_음수면_실패한다 () {
        // given
        long userId = -1; // 음수 유저 아이디
        String expectedErrorMessage = "userId는 양수여야 합니다.";

        // when
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> pointService.chargePoint(userId, 500));
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }


    @Test
    @DisplayName("포인트 충전")
    public void 충전포인트는_100원_미만이면_실패한다() {
        // given
        long userId = 1;
        long chargeAmount = 50; // 100 원 미만
        String expectedErrorMessage = "충전포인트는 " + MIN_CHARGE_AMOUNT + " 이상 " + MAX_CHARGE_AMOUNT + " 이하입니다.";

        // when
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> pointService.chargePoint(userId, chargeAmount));
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("포인트 충전")
    public void 충전포인트는_50000원_초과하면_실패한다() {
        // given
        long userId = 1;
        long chargeAmount = 100000; // 50000 원 미만
        String expectedErrorMessage = "충전포인트는 " + MIN_CHARGE_AMOUNT + " 이상 " + MAX_CHARGE_AMOUNT + " 이하입니다.";

        // when
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> pointService.chargePoint(userId, chargeAmount));
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("포인트 충전")
    public void 포인트충전_성공() {
        // given
        long userId = 1;
        long chargeAmount = 4500;
        long expectedChargeAmount = 4500;

        // when
        UserPoint user =pointService.chargePoint(userId, chargeAmount);

        // then
        Assertions.assertEquals(expectedChargeAmount ,user.point());
    }


}
