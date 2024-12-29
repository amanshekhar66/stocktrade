package stocktrade.stocktrade.advices;

import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import stocktrade.stocktrade.exceptions.*;
import stocktrade.stocktrade.repositories.UserDetailsRepository;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    private ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFound e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ApiError> handleInvalidMethodArgs(MethodArgumentNotValidException e){
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errMsg(e.getMessage())
                .subErrors(errors)
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(AuthenticationException.class)
    private ResponseEntity<ApiError> handleAuthenticationExceptions(AuthenticationException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(JwtException.class)
    private ResponseEntity<ApiError> handleJwtExceptions(JwtException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(MessagingException.class)
    private ResponseEntity<ApiError> handleEmailMessagingException(MessagingException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(InvalidOtp.class)
    private ResponseEntity<ApiError> handleInvalidOtpException(InvalidOtp e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(ExpiredOtp.class)
    private ResponseEntity<ApiError> handleExpiredOtpException(ExpiredOtp e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(InvalidRefreshToken.class)
    private ResponseEntity<ApiError> handleInvalidRefreshToken(InvalidRefreshToken e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(UserAlreadyRegistered.class)
    private ResponseEntity<ApiError> handleInvalidRefreshToken(UserAlreadyRegistered e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(UnverifiedUserException.class)
    private ResponseEntity<ApiError> handleUnverifiedUserException(UnverifiedUserException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiError> handleAllOtherExceptions(Exception e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errMsg(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,apiError.getStatus());
    }
}

