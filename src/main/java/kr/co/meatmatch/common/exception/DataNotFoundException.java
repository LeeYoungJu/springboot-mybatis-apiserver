package kr.co.meatmatch.common.exception;

// DB 데이터 조회 시 결과 size가 0일 때 예외처리 해야하면 쓰면 됨
public class DataNotFoundException extends Exception {
    public DataNotFoundException() {
        super();
    }
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
