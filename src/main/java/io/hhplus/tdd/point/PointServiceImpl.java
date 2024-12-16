package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PointServiceImpl implements  PointService {


    private final PointHistoryTable pointHistoryRepository;
    private final UserPointTable userPointRepository;

    private final int MAX_CHARGE_AMOUNT = 50000;
    private final int MIN_CHARGE_AMOUNT = 100;

    private final int MAX_USE_AMOUNT = 50000;
    private final int MIN_USE_AMOUNT = 100;

    public PointServiceImpl(PointHistoryTable pointHistoryRepository, UserPointTable userPointRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
        this.userPointRepository = userPointRepository;
    }

    @Override
    public UserPoint chargePoint(long userId, long chargeAmount) {
        try {
            // userId 는 양수다
            if(userId <= 0) {
                // 실패
                throw new RuntimeException("userId는 양수입니다.");
            }

            // chargeAmount 는 양수다
            if(chargeAmount < MIN_CHARGE_AMOUNT || chargeAmount > MAX_CHARGE_AMOUNT ) {
                // 실패
                throw new RuntimeException("충전포인트는 는 "+MIN_CHARGE_AMOUNT+" 이상 "+MAX_CHARGE_AMOUNT+" 이하입니다.");
            }

            // 유저 조회
            UserPoint userPoint = userPointRepository.selectById(userId);


            // 생성되지 않은 유저도 amount= 0으로 생성된다.
            chargeAmount += userPoint.point();
            this.userPointRepository.insertOrUpdate(userId, chargeAmount);

            // 히스토리 생성한다.
            this.pointHistoryRepository.insert(userId, chargeAmount, TransactionType.CHARGE, userPoint.updateMillis());

            return userPoint;

        } catch (RuntimeException e ) {
            throw e;
        }
    }

    @Override
    public UserPoint usePoint(long userId, long useAmount) {
        try {

            // userId 는 양수다
            if(userId <= 0) {
                // 실패
                throw new RuntimeException("userId는 양수입니다.");
            }
            if(useAmount < 100 || useAmount > 50000) {
                // 실패
                throw new RuntimeException("사용포인트는 "+MIN_CHARGE_AMOUNT+" 이상 "+MAX_CHARGE_AMOUNT+" 이하입니다.");
            }

            // 유저 조회
            UserPoint userPoint = userPointRepository.selectById(userId);
            if(useAmount > userPoint.point() ) {
                // 실패
                throw new RuntimeException("사용포인트는 보유포인트 보다 더 많은 포인트를 사용할 수 없습니다.");
            }


            long amount = userPoint.point() - useAmount;
            this.userPointRepository.insertOrUpdate(userId, amount);

            // 히스토리 생성한다.
            this.pointHistoryRepository.insert(userId, amount, TransactionType.USE, userPoint.updateMillis());

            return userPoint;

        } catch (RuntimeException e) {
            throw e;
        }


    }

    @Override
    public UserPoint getUserPointByUserId(long userId) {
        return userPointRepository.selectById(userId);
    }

    @Override
    public List<PointHistory> getPointHistoryByUserId(long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }
}
