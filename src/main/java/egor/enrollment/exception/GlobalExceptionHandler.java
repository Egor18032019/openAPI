package egor.enrollment.exception;

import egor.enrollment.components.schemas.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Error> handleException400(BadRequestException e) {
        System.out.println("handleException400");
        return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Error> handleException404(NotFoundException e) {
        System.out.println("handleException404");
        return new ResponseEntity<>(new Error(404, "Not Found"), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<?> handleException(HttpMessageNotReadableException e) {
//        System.out.println("Ошибка в JSONE");
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }
}