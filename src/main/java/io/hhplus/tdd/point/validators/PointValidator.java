package io.hhplus.tdd.point.validators;

public interface PointValidator {
    // userId를 유효성 검증하는 함수
    public void validateUserId(long userId);

    // 충전/사용 amount 의 유효성을 검증하는 함수
    public void validateAmountValue(long amount);

}
