package kr.co.meatmatch.common.dto;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

/*
 * 기존 php의 JSON 리턴값에 맞춰서 동일하게 리턴할 수 있도록 만들었음
 */
@Getter
public class ResponseDto {
    private ResponseStatus status;

    private Object data;

    public ResponseDto(ResponseStatusString status, Object data) {
        this.status = status;
        this.data = data;
    }
    public ResponseDto(ResponseStatusObject status) {
        this.status = status;
    }

    public static ResponseEntity<ResponseDto> ok(Object data) {
        return ResponseEntity.ok(new ResponseDto(
                ResponseStatusString.builder().code(STATUS_CODE.OK).msg("OK").build(),
                data
        ));
    }

    public static ResponseEntity<ResponseDto> bad(int code, String msg) {
        return ResponseEntity.badRequest().body(
                new ResponseDto(
                        ResponseStatusString.builder().code(code).msg(msg).build(),
                        null
                )
        );
    }

    public static ResponseEntity<ResponseDto> bad(int code, Object data) {
        return ResponseEntity.badRequest().body(
                new ResponseDto(
                        ResponseStatusObject.builder().code(code).msg(data).build()
                )
        );
    }
}
