package kr.co.meatmatch.common.exception.advice;

import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.common.exception.DuplicatedAuthDataException;
import kr.co.meatmatch.common.exception.InvalidDataException;
import kr.co.meatmatch.common.exception.InvalidRegCodeException;
import kr.co.meatmatch.common.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto> defaultException(Exception e) {
        return ResponseDto.bad(STATUS_CODE.BAD, e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers
                                                                  , HttpStatus status, WebRequest request
    ) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        for(ObjectError error: allErrors) {

        }
        return super.handleMethodArgumentNotValid(e, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstaintViolation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(e, ResponseDto.bad(STATUS_CODE.BAD, e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
