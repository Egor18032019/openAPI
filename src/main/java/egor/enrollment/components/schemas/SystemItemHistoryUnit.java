package egor.enrollment.components.schemas;

import lombok.*;

@Data
@AllArgsConstructor
public class SystemItemHistoryUnit {
    private String id;
    private String url;
    private String date;
    private String parentId;
    private Integer size;
    private String type;
}
