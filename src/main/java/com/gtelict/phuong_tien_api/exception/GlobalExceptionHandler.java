package com.gtelict.phuong_tien_api.exception;

import com.gtelict.phuong_tien_api.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        String errorMsg = ex.getMostSpecificCause().getMessage();
        String friendlyMessage = "Dữ liệu đã tồn tại hoặc vi phạm ràng buộc cơ sở dữ liệu.";

        if (errorMsg != null) {
            if (errorMsg.contains("so_bien_ban")) {
                friendlyMessage = "Số biên bản này đã tồn tại trong hệ thống. Vui lòng nhập số khác!";
            } else if (errorMsg.contains("ma_tai_san")) {
                friendlyMessage = "Mã tài sản này đã tồn tại trong hệ thống!";
            } else if (errorMsg.contains("ma_can_bo")) {
                friendlyMessage = "Mã cán bộ này đã tồn tại!";
            }
        }

        System.err.println("Database Constraint Error: " + errorMsg);

        ErrorResponseDto response = new ErrorResponseDto("Xung đột dữ liệu", "409", friendlyMessage, request.getRequestURI(), LocalDateTime.now(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PessimisticLockingFailureException.class, jakarta.persistence.LockTimeoutException.class, jakarta.persistence.PessimisticLockException.class, org.springframework.dao.CannotAcquireLockException.class})
    public ResponseEntity<ErrorResponseDto> handlePessimisticLockingFailureException(Exception ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto("Hệ thống đang bận", "409", "Tài sản đang được xử lý bởi một giao dịch khác. Vui lòng thử lại sau giây lát!", request.getRequestURI(), LocalDateTime.now(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto("Lỗi dữ liệu đầu vào", "400", "Dữ liệu đầu vào không hợp lệ: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), request.getRequestURI(), LocalDateTime.now(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto("Lỗi quy trình nghiệp vụ", "400", ex.getMessage(), request.getRequestURI(), LocalDateTime.now(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto("Không tìm thấy", "404", "Bản ghi không tồn tại hoặc đường dẫn không đúng", request.getRequestURI(), LocalDateTime.now(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto("Lỗi hệ thống", "500", "Đã xảy ra lỗi hệ thống: " + ex.getMessage(), request.getRequestURI(), LocalDateTime.now(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
