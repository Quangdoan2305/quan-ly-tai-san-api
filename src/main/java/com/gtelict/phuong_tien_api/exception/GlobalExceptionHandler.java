package com.gtelict.phuong_tien_api.exception;

import com.gtelict.phuong_tien_api.dto.ResponseData;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseData<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ResponseData<Void> response = new ResponseData<>(null);
        response.setCode("DUPLICATE_OR_CONSTRAINT_ERROR");
        response.setMessage("Dữ liệu đã tồn tại hoặc vi phạm ràng buộc cơ sở dữ liệu: " + ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        ResponseData<Void> response = new ResponseData<>(null);
        response.setCode("VALIDATION_ERROR");
        response.setMessage("Dữ liệu đầu vào không hợp lệ: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Void>> handleGeneralException(Exception ex) {
        ResponseData<Void> response = new ResponseData<>(null);
        response.setCode("INTERNAL_SERVER_ERROR");
        response.setMessage("Đã xảy ra lỗi hệ thống: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
