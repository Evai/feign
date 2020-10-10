package com.heimdall.feign.demo.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

/**
 * Project:               ${PROJECT_NAME}
 * Author:                crh
 * Company:               Big Player Group
 * Created Date:          ${DATE}
 * Description:   {全局异常处理器}
 * Copyright @ 2017-${YEAR} BIGPLAYER.GROUP – Confidential and Proprietary
 * <p>
 * History:
 * ------------------------------------------------------------------------------
 * Date            |time        |Author    |Change Description
 */
@Slf4j
@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {

    }

    /**
     * 全局异常.
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(Exception e) {
        return e.toString();
    }

    private String printParamError(List<FieldError> fieldErrors) {
        FieldError fieldError = fieldErrors.get(0);
        log.error("请求参数错误: {} {}", fieldError.getField(), fieldError.getDefaultMessage());
        return fieldError.getField() + fieldError.getDefaultMessage();
    }

    /**
     * validation Exception
     *
     * @param exception
     * @return R
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String bodyValidExceptionHandler(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        return printParamError(fieldErrors);
    }

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validExceptionHandler(BindException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        return printParamError(fieldErrors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validExceptionHandler(ConstraintViolationException exception) {
        String message = exception.getMessage();
        log.error("请求参数错误: {}", message);
        return "请求参数错误: {}"+ message;
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String missingParamExceptionHandler(MissingServletRequestParameterException exception) {
        log.error("请求参数为空: [{}], {}", exception.getParameterName(), exception.getMessage());
        return "请求参数为空: [" + exception.getParameterName() + "], " + exception.getMessage();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String missingRequestBodyExceptionHandler(HttpMessageNotReadableException exception) {
        log.error("请求参数为空: {}", exception.getMessage());
        return "请求参数为空: " + exception.getMessage();
    }


}
