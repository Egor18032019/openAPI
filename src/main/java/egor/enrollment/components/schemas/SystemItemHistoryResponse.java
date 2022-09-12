package egor.enrollment.components.schemas;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SystemItemHistoryResponse extends ResponseAbs {
    @NonNull
    private SystemItemHistoryUnit[] systemItemHistoryUnits;
}

