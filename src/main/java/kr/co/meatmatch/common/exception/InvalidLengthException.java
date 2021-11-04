package kr.co.meatmatch.common.exception;

// 데이터 자리수가 맞지 않는 경우 (ex: 사업자등록번호 10자리)
public class InvalidLengthException extends Exception {
    public InvalidLengthException() {
        super();
    }

    public InvalidLengthException(String msg) {
        super(msg);
    }
}
