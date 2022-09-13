package egor.enrollment.components.schemas;

import lombok.*;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SystemItemImportRequest {
    private  List<SystemItemImport> items;
    private  String updateDate;

    @Override
    public String toString() {
        return "SystemItemImportRequest{" +
                "items=" + items.size() +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }
}
