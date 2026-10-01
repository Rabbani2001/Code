package com.schooltech;

import com.schooltech.sms.exception.InvalidOtpException;
import com.schooltech.sms.exception.TeacherErrorResponse;
import com.schooltech.sms.exception.TeacherNotFoundException;
import com.schooltech.sms.exception.dto.ApiErrorResponse;
import com.schooltech.sms.exception.dto.StudentNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequestMapping(produces = "application/json")
public class STExceptionHandler {

    @ExceptionHandler //handle our custom exception and return with correct error response wrapped in ResponseEntity
    public ResponseEntity<ApiErrorResponse> handleException(StudentNotFoundException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }


//    @ExceptionHandler
//    public ResponseEntity<TeacherErrorResponse> handleTeacherException(TeacherNotFoundException e){
//        TeacherErrorResponse teacherErrorResponse = new TeacherErrorResponse();
//        teacherErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        teacherErrorResponse.setMessage(e.getMessage());
//        teacherErrorResponse.setTimeStamp(System.currentTimeMillis());
//
//        return new ResponseEntity<>(teacherErrorResponse, HttpStatus.BAD_REQUEST);
//    }


    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<TeacherErrorResponse> handleTeacherNotFoundException(TeacherNotFoundException e) {
        TeacherErrorResponse teacherErrorResponse = new TeacherErrorResponse();
        teacherErrorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        teacherErrorResponse.setMessage(e.getMessage());
        teacherErrorResponse.setTimeStamp(System.currentTimeMillis());
        //System.out.println(new ResponseEntity<>(teacherErrorResponse, HttpStatus.NOT_FOUND));

        return new ResponseEntity<>(teacherErrorResponse, HttpStatus.NOT_FOUND);
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGenericException(Exception ex) {
//        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(Map.of(
                "status", 400,
                "errors", errors
        ));
    }

    /**
     * Handles method-level validation (Spring Boot 3)
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerMethodValidation(
            HandlerMethodValidationException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getAllValidationResults().forEach(result -> {
            result.getResolvableErrors().forEach(error ->
                    errors.put(result.getMethodParameter().getParameterName(),
                            error.getDefaultMessage())
            );
        });

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(Map.of(
                "status", 400,
                "errors", errors
        ));
    }


    ///////////////////////////NEW////////////////////////////////////
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOtpException(InvalidOtpException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                500,
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        if (errorMessage.contains("Duplicate entry")) {
            ApiErrorResponse response = new ApiErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    errorMessage,
                    LocalDateTime.now()
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        if (errorMessage.contains("Data truncation")) {
            ApiErrorResponse response = new ApiErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    errorMessage,
                    LocalDateTime.now()
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Data integrity violation occurred.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
