package egor.enrollment.components.schemas;

import lombok.Data;

@Data
public class SystemItemImport {
    private  String id;
    private  String type;
    private  String url;
    private  String parentId;
    private  Integer size;
}
