package egor.enrollment.components.schemas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
 public class SystemItemImport {
    private String id;
    private String type;
    private String parentId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String url;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer size;
}
//  {
//          "items": [
//          {
//          "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
//          "type": "FOLDER",
//          "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
//          },
//          {
//          "type": "FILE",
//          "url": "/file/url1",
//          "id": "863e1a7a-1304-42ae-943b-179184c077e3",
//          "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
//          "size": 128
//          },
//          {
//          "type": "FILE",
//          "url": "/file/url2",
//          "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
//          "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
//          "size": 256
//          }
//          ],
//          "updateDate": "2022-02-02T12:00:00Z"
//          }