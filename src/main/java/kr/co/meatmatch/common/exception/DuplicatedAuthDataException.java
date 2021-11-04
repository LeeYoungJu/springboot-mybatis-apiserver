package kr.co.meatmatch.common.exception;

// id나 email 등 중복으로 들어가면 안되는 데이터를 체크 시 중복인 경우 쓰면 됨
public class DuplicatedAuthDataException extends Exception {
    public DuplicatedAuthDataException() {
        super();
    }

    public DuplicatedAuthDataException(String msg) {
        super(msg);
    }
}
