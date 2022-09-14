package egor.enrollment.components.schemas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;


@Data
public class SystemItemImportRequest {
      List<SystemItemImport> items;
      String updateDate;

    @Override
    public String toString() {
        return "SystemItemImportRequest{" +
                "items=" + items.size() +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }
}
