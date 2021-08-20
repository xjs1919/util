package com.github.xjs.util.exception;

import com.github.xjs.util.CollectionUtil;
import com.github.xjs.util.ExceptionUtil;
import com.github.xjs.util.result.CodeMsg;
import com.github.xjs.util.result.ICodeMsg;
import com.github.xjs.util.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author jiashuai.xujs
 * @date 2021/8/20 11:13
 */
@ResponseBody
@ControllerAdvice
public class CommonExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

    @ExceptionHandler(BindException.class)
    public Result BindException(BindException bindException){
        BindingResult bindingResult = bindException.getBindingResult();
        return error(bindingResult);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result BindException(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        return error(bindingResult);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result exception(HttpRequestMethodNotSupportedException exception){
        String message = exception.getMessage();
        return Result.error(CodeMsg.VALIDATE_ERROR.withArgs(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result exception(ConstraintViolationException exception){
        Set<ConstraintViolation<?>>  violations = exception.getConstraintViolations();
        ConstraintViolation violation = violations.iterator().next();
        String defaultMessage = violation.getMessage();
        List<String> names = new ArrayList<>(2);
        for(Iterator<Path.Node> it = violation.getPropertyPath().iterator(); it.hasNext(); ){
            Path.Node pathNode = it.next();
            names.add(pathNode.getName());
        }
        String objectName = names.get(names.size()-1);
        return Result.error(CodeMsg.VALIDATE_ERROR.withArgs("["+objectName+"]"+defaultMessage));
    }

    @ExceptionHandler(BusinessException.class)
    public Result exception(BusinessException exception){
        log.error(ExceptionUtil.getStackTrace(exception));
        ICodeMsg codeMsg = exception.getCodeMsg();
        if(codeMsg != null){
            return Result.error(codeMsg);
        }else{
            String msg = exception.getMessage();
            return Result.error(CodeMsg.BUSINESS_ERROR.withArgs(msg));
        }
    }

    @ExceptionHandler(Exception.class)
    public Result exception(Exception exception){
        log.error(ExceptionUtil.getStackTrace(exception));
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    private Result error(BindingResult bindResult){
        String objectName = "";
        String defaultMessage = "";
        List<FieldError> fieldErrors =  bindResult.getFieldErrors();
        if(!CollectionUtil.isEmpty(fieldErrors)){
            FieldError fieldError = fieldErrors.get(0);
            objectName =  fieldError.getField();
            defaultMessage = fieldError.getDefaultMessage();
        }else{
            ObjectError objectError = bindResult.getAllErrors().get(0);
            objectName = objectError.getObjectName();
            defaultMessage = objectError.getDefaultMessage();
        }
        return Result.error(CodeMsg.VALIDATE_ERROR.withArgs("["+objectName+"]"+defaultMessage));
    }

}
