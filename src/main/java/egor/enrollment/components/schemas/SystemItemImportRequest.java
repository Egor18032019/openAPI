package egor.enrollment.components.schemas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SystemItemImportRequest {
    @NotNull
    private List<SystemItemImport> items;
    private String updateDate;

    @Override
    public String toString() {
        return "SystemItemImportRequest{" +
                "items=" + items.size() +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }
}
