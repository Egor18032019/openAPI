package egor.enrollment.components.schemas;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SystemItemHistoryUnit {
    private String id;
    private String url;
    private String date;
    private String parentId;
    private Integer size;
    private String type;
}
