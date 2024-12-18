package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.validators.PointValidator;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PointServiceImpl implements  PointService, PointValidator {


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
    public UserPoint chargePoint(long userId, long chargeAmount) throws RuntimeException {
        try {
            // 유효성 검증
            validateUserId(userId); // 유저아이디
            validateAmountValue(chargeAmount); // 충전포인트

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
            // 유효성검사
            validateUserId(userId); // 유저아이디
            validateAmountValue(useAmount); // 충전포인트

            // 유저 포인트 조회
            UserPoint userPoint = userPointRepository.selectById(userId);

            // 포인트 사용후 현재포인트
            long currentPointAfterUse = subtractPoint(userPoint.point(), useAmount);

            // 유저 포인트 업데이트
            userPoint = this.userPointRepository.insertOrUpdate(userId, currentPointAfterUse);

            // 히스토리 생성
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

    // userId 의 유효성검증 - 만일 유효성검증로직이 외부에서도 사용된다면?
    @Override
    public void validateUserId(long userId) throws RuntimeException {
        // 정책 userId 는 0보다 큰 양수여야한다.
        if(userId <= 0)
            throw new RuntimeException("userId는 양수입니다.");

    }

    // amount 값의 유효성검증
    @Override
    public void validateAmountValue(long amount) throws RuntimeException {
        // 정책: MIN_CHARGE_AMOUNT <= amount <= MAX_CHARGE_AMOUNT
        if(amount < MIN_CHARGE_AMOUNT || amount > MAX_CHARGE_AMOUNT )
            throw new RuntimeException("포인트 값은 최소 "+MIN_CHARGE_AMOUNT+" 이상 "+MAX_CHARGE_AMOUNT+" 이하입니다.");
    }

    // 포인트 충전
    // userSavedPoint : 유저보유포인트
    // amount: 충전포인트
    private long addPoint(long userSavedPoint, long amount ) {
        return userSavedPoint + amount;
    }

    // 포인트 사용
    // userSavedPoint : 유저보유포인트
    // amount: 사용포인트

    /**
     * 포인트 사용
     *
     * @param userSavedPoint : 유저보유포인트
     * @param chargeAmount : 사용포인트
     * @return long
     * @throws RuntimeException
     */
    private long subtractPoint(long userSavedPoint, long chargeAmount )throws RuntimeException {
        // 사용포인트(chargeAmount) > 유저보유포인트(userSavedPoint) 이면 에러를 발생시킨다.
        if(chargeAmount  > userSavedPoint) {
            throw new RuntimeException("보유포인트 보다 더 많은 포인트를 사용할 수 없습니다.");
        }

        return userSavedPoint - chargeAmount;
    }
}
