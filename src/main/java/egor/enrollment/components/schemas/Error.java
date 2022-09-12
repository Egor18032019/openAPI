package egor.enrollment.components.schemas;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Error extends ResponseAbs {
    private int code;
    private String message;
}
