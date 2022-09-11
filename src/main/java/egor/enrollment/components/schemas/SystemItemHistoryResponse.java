package egor.enrollment.components.schemas;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
@Data
public class SystemItemHistoryResponse {
    @NonNull
    private List<SystemItemHistoryUnit> items;
}

