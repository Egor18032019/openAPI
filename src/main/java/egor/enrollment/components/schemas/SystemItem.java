package egor.enrollment.components.schemas;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data //TODO обдумать тут
@AllArgsConstructor
public class SystemItem {
    private  String id;
    private  String url;
    private  String type;
    private  String parentId;
    private  String date;
    private Integer size;

    private  List<SystemItem> children;
}
