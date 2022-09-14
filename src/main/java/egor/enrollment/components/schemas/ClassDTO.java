package egor.enrollment.components.schemas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassDTO {
    private SystemItemImportRequest systemItemImportRequest;

}
