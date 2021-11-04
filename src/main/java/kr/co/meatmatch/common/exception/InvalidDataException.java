package kr.co.meatmatch.common.exception;

// 사용자 아이디 등 형식이 맞지 않는 경우
public class InvalidDataException extends Exception {
    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String msg) {
        super(msg);
    }
}
