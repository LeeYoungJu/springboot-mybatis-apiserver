package kr.co.meatmatch.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseStatusObject extends ResponseStatus {
    private int code;
    private Object msg;

    @Builder
    public ResponseStatusObject(int code, Object msg) {
        this.code = code;
        this.msg = msg;
    }
}
