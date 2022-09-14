package egor.enrollment.exception;

import egor.enrollment.components.schemas.Error;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;

@EnableWebMvc
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<Error> handleException400(HttpServletRequest request,
                                                    NoHandlerFoundException exception) {
        System.out.println("       handleException400");
        return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleException(BadRequestException exception) {
        return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleException(HttpMessageNotReadableException e) {
        System.out.println(e.getHttpInputMessage().toString());
        System.out.println("              Ошибка в JSONE");
        return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
    }
}
