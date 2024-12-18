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
        String expectedErrorMessage = "userId는 양수입니다.";

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
        String expectedErrorMessage = "포인트 값은 최소 " + MIN_CHARGE_AMOUNT + " 이상 " + MAX_CHARGE_AMOUNT + " 이하입니다.";

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
        long chargeAmount = 100000; // 50000 원 초과
        String expectedErrorMessage = "포인트 값은 최소 " + MIN_CHARGE_AMOUNT + " 이상 " + MAX_CHARGE_AMOUNT + " 이하입니다.";

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

    @Test
    @DisplayName("포인트 사용")
    public void 사용포인트는_100원_미만이면_실패한다() {
        // given
        long userId = 1;
        long useAmount = 50; // 100 원 미만
        String expectedErrorMessage = "포인트 값은 최소 " + MIN_CHARGE_AMOUNT + " 이상 " + MAX_CHARGE_AMOUNT + " 이하입니다.";

        // when
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> pointService.usePoint(userId, useAmount));
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("포인트 사용")
    public void 사용포인트는_50000원_초과하면_실패한다() {
        // given
        long userId = 1;
        long useAmount = 100000; // 50000 원 초과
        String expectedErrorMessage = "포인트 값은 최소 " + MIN_CHARGE_AMOUNT + " 이상 " + MAX_CHARGE_AMOUNT + " 이하입니다.";

        // when
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> pointService.usePoint(userId, useAmount));
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("포인트 사용")
    public void 사용포인트가_보유포인트보다_초과하면_실패한다() {
        // given
        long userId = 1;
        long saveAmount = 8000; // 보유포인트
        long useAmountOverThanSaveAmount = 9500; // 사용포인트: 보유포인트 보다 많은 사용포인트
        pointService.chargePoint(userId, saveAmount); // 보유포인트만큼 충전
        String expectedErrorMessage = "보유포인트 보다 더 많은 포인트를 사용할 수 없습니다.";

        // when
        // then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> pointService.usePoint(userId, useAmountOverThanSaveAmount));
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("포인트 사용")
    public void 포인트사용_성공() {
        // given
        long userId = 1;
        long saveAmount = 10000; // 보유포인트
        long useAmount = 5000; // 사용포인트
        pointService.chargePoint(userId, saveAmount); // 보유포인트만큼 충전
        long expectedChargeAmount = saveAmount - useAmount;


        // when
        UserPoint user =pointService.usePoint(userId, useAmount);

        // then
        Assertions.assertEquals(expectedChargeAmount ,user.point());
    }

    @Test
    @DisplayName("포인트 조회")
    public void 충전후_포인트_조회성공() {
        // given
        long userId = 1;
        long chargeAmount = 4500;
        long expectedChargeAmount = 9000;
        pointService.chargePoint(userId, chargeAmount); // 포인트 충전: 0 -> 4500
        pointService.chargePoint(userId, chargeAmount); // 포인트 충전: 4500 -> 9000

        // when
        UserPoint userPoint = pointService.getUserPointByUserId(userId);

        // then
        Assertions.assertEquals(expectedChargeAmount ,userPoint.point());
    }

    @Test
    @DisplayName("포인트 조회")
    public void 충전후_포인트_내역조회_성공() {
        // given
        long userId = 1;
        long chargeAmount = 4500;
        long expectedChargeAmount = 9000;
        pointService.chargePoint(userId, chargeAmount); // 포인트 충전: 0 -> 4500
        pointService.chargePoint(userId, chargeAmount); // 포인트 충전: 4500 -> 9000

        // when
        List<PointHistory> list = pointService.getPointHistoryByUserId(userId);

        // then
        Assertions.assertEquals(2, list.size());
    }


    @Test
    @DisplayName("포인트 조회")
    public void 충전2번_사용1번으로_포인트_내역조회_성공() {
        // given
        long userId = 1;
        long chargeAmount = 4500;
        long expectedChargeAmount = 9000;
        pointService.chargePoint(userId, chargeAmount); // 포인트 충전: 0 -> 4500
        pointService.chargePoint(userId, chargeAmount); // 포인트 충전: 4500 -> 9000
        pointService.usePoint(userId, 5000); // 포인트 사용: 9000 -> 4000

        // when
        List<PointHistory> list = pointService.getPointHistoryByUserId(userId);

        // then
        Assertions.assertEquals(3, list.size());
    }


}
