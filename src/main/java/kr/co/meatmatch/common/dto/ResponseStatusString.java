package kr.co.meatmatch.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseStatusString extends ResponseStatus {
    private int code;
    private String msg;

    @Builder
    public ResponseStatusString(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
