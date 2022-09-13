package egor.enrollment.components.schemas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SystemItem extends ResponseAbs {
    private  String id;
    private  String url;
    private  String type;
    private  String parentId;
    private  String date;
    private Integer size;

    private  List<SystemItem> children;
}
