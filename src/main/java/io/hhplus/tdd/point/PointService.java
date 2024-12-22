package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {
    int MAX_CHARGE_AMOUNT = 50000;
    int MIN_CHARGE_AMOUNT = 100;

    int MAX_USE_AMOUNT = 50000;
    int MIN_USE_AMOUNT = 100;

    // 포인트 충전
    UserPoint chargePoint(long userId, long chargeAmount);

    // 포인트 사용
    UserPoint usePoint(long userId, long useAmount);

    // 포인트 조회
    UserPoint getUserPointByUserId(long userId);

    // 포인트 내역 조회
    List<PointHistory> getPointHistoryByUserId(long userId);
}
