package kr.co.meatmatch.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseStatusObject extends ResponseStatus {
    private String code;
    private Object msg;

    @Builder
    public ResponseStatusObject(String code, Object msg) {
        this.code = code;
        this.msg = msg;
    }
}
