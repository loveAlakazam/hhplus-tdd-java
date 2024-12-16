package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PointServiceImpl implements  PointService {


    private final PointHistoryTable pointHistoryRepository;
    private final UserPointTable userPointRepository;



    public PointServiceImpl(PointHistoryTable pointHistoryRepository, UserPointTable userPointRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
        this.userPointRepository = userPointRepository;
    }


    /**
     * [포인트 충전 서비스 로직 설계]
     * 1. 유저가 존재하는지 확인한다.
     * 2. 1에서 유저가 존재하지 않으면 자동으로 amount(보유포인트)가 0인 유저포인트를 자동으로 생성한다.
     * 3. 유저의 amount(보유포인트값)을 amount+충전량 만큼 업데이트한다
     * 4. 포인트 히스토리를 insert 한다
     */
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
                throw new RuntimeException("충전포인트는 "+MIN_CHARGE_AMOUNT+" 이상 "+MAX_CHARGE_AMOUNT+" 이하입니다.");
            }

            // 충전전 유저 조회
            UserPoint userPoint = userPointRepository.selectById(userId);

            // 충전후 유저포인트
            long amount = userPoint.point() + chargeAmount;
            userPoint = this.userPointRepository.insertOrUpdate(userId, amount);

            // 히스토리 생성한다.
            this.pointHistoryRepository.insert(userId, chargeAmount, TransactionType.CHARGE, userPoint.updateMillis());

            return userPoint;

        } catch (RuntimeException e ) {
            throw e;
        }
    }


    /**
     * [ 포인트 사용 서비스 로직 설계 ]
     * 1. 유저가 존재하는지 확인한다
     * 2. 1에서 유저가 존재하지 않으면 자동으로 amount(보유포인트)가 0 인 유저포인트를 자동으로 생성한다
     * 3. 보유 포인트가 사용양보다 적으면 실패한다.
     * 4. 보유한 포인트가 사용양보다 많으면, 유저의 amount(보유포인트값)을 amount-사용량 만큼 업데이트를 한다.
     * 5. 포인트 히스토리를 Insert 한다.
     */
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
                throw new RuntimeException("보유포인트 보다 더 많은 포인트를 사용할 수 없습니다.");
            }


            long amount = userPoint.point() - useAmount;
            userPoint = this.userPointRepository.insertOrUpdate(userId, amount);

            // 히스토리 생성한다.
            this.pointHistoryRepository.insert(userId, useAmount, TransactionType.USE, userPoint.updateMillis());

            return userPoint;

        } catch (RuntimeException e) {
            throw e;
        }


    }


    /**
     * [ 포인트 조회 로직 설계]
     * 1. 유저가 존재하는지 확인한다.
     * 2. 존재하지 않으면 자동으로 amount(보유포인트)가 0인 유저포인트를 자동으로 생성한다.
     * 3. 유저의 포인트를 조회한다.
     */
    @Override
    public UserPoint getUserPointByUserId(long userId) {
        return userPointRepository.selectById(userId);
    }


    /**
     * [ 포인트 내역 조회 로직 설계]
     * 1. 유저가 존재하는지 확인한다.
     * 2. 존재하지 않으면 자동으로 amount(보유포인트)가 0인 유저포인트를 자동으로 생성한다.
     * 3. 포인트 내역을 조회한다
     */
    @Override
    public List<PointHistory> getPointHistoryByUserId(long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }
}
