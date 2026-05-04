package com.melon.commonservice.config;

import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpResult<String> globalException(Exception e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode())
                .message(HttpResponseStatus.INTERNAL_SERVER_ERROR.getMessage())
                .data("INTERNAL_SERVER_ERROR")
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.BAD_REQUEST.getCode())
                .message(HttpResponseStatus.BAD_REQUEST.getMessage())
                .data(e.getName() + ": " + e.getMessage())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult<String> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.BAD_REQUEST.getCode())
                .message(HttpResponseStatus.BAD_REQUEST.getMessage())
                .data("The " + e.getParameterName() + " cannot be empty!")
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpResult<String> noResourceFoundException(NoResourceFoundException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.NOT_FOUND.getCode())
                .message(HttpResponseStatus.NOT_FOUND.getMessage())
                .data(e.getMessage())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public HttpResult<String> accessDeniedException(AccessDeniedException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.FORBIDDEN.getCode())
                .message(HttpResponseStatus.FORBIDDEN.getMessage())
                .data("Forbidden")
                .build();
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public HttpResult<String> authorizationDeniedException(AuthorizationDeniedException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.UNAUTHORIZED.getCode())
                .message(HttpResponseStatus.UNAUTHORIZED.getMessage())
                .data(e.getMessage())
                .build();
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult<String> missingServletRequestPartException(MissingServletRequestPartException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.BAD_REQUEST.getCode())
                .message(HttpResponseStatus.BAD_REQUEST.getMessage())
                .data("The " + e.getRequestPartName() + " cannot be empty!")
                .build();
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpResult<String> serverException(ServerException e) {
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode())
                .message(HttpResponseStatus.INTERNAL_SERVER_ERROR.getMessage())
                .data(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return HttpResult.<String>builder()
                .code(HttpResponseStatus.BAD_REQUEST.getCode())
                .message(HttpResponseStatus.BAD_REQUEST.getMessage())
                .data(defaultMessage)
                .build();
    }
}