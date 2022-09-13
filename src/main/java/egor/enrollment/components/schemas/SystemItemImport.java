package egor.enrollment.components.schemas;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SystemItemImport {
    private  String id;
    private  String type;
    private  String url;
    private  String parentId;
    private  Integer size;
}
