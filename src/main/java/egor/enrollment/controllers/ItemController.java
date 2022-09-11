package egor.enrollment.controllers;

import egor.enrollment.components.schemas.Error;
import egor.enrollment.components.schemas.SystemItem;
import egor.enrollment.components.schemas.SystemItemHistoryResponse;
import egor.enrollment.components.schemas.SystemItemImportRequest;
import egor.enrollment.model.Item;
import egor.enrollment.services.ItemService;
import egor.enrollment.services.ValidationService;
import egor.enrollment.utility.ConverterItemToSystemItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
public class ItemController {
    private final ItemService service;
    private final ValidationService validationService;

    @Autowired
    public ItemController(ItemService service, ValidationService validationService) {
        this.service = service;
        this.validationService = validationService;
    }

    @PostMapping(value = "/imports")
    public ResponseEntity<Error> addItems(@RequestBody SystemItemImportRequest request) {
        System.out.println(request.toString());
        ResponseEntity<Error> response;
        //TODO так ли ??
        boolean isValidRequest = validationService.isSystemItemImportRequest(request);
        if (isValidRequest) {
            service.saveProducts(request);
            response = new ResponseEntity<>(new Error(200, "Success"), HttpStatus.OK);
        } else response = new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Error> deleteItems(@PathVariable String id,
                                             @RequestParam String date) {
        ResponseEntity<Error> response;
        Item item = service.findItemInDB(id);

        if (!validationService.isISO8601(date)) {
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        if (item != null) {
            service.deleteItemInDB(item, date);
            response = new ResponseEntity<>(new Error(200, "OK"), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new Error(404, "Item not found"), HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<SystemItem> getItems(@PathVariable String id) {
        ResponseEntity<SystemItem> response;
        if (true) {
// TODO когда выдавать 404 ?
            Item item = service.findItemInDB(id);
            if (item!=null) {
// Item in SystemItem
                SystemItem systemItem = ConverterItemToSystemItem.toShopUnit(item);
                // TODO dodelat
                response = new ResponseEntity<>(systemItem, HttpStatus.OK);
            } else {
                // TODO ошибки по другому
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping(value = "updates")
    public ResponseEntity<SystemItemHistoryResponse> getFiles(
            @RequestParam String date
    ) {
        if (true) {

            return new ResponseEntity<>(new SystemItemHistoryResponse(null), HttpStatus.OK);
        } else {
            // TODO ошибки по другому
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "node/{id}/history")
    public ResponseEntity<SystemItemHistoryResponse> getStatistic(@PathVariable String id,
                                                                  @RequestParam(required = false) String dateStart,
                                                                  @RequestParam(required = false) String dateEnd) {
        System.out.println("Get history from: " + dateStart + " to: " + dateEnd);
        ResponseEntity<SystemItemHistoryResponse> response;
        ZonedDateTime dateStartZ = ZonedDateTime.parse(dateStart);
        ZonedDateTime dateEndZ = ZonedDateTime.parse(dateEnd);
        if (true) {

            if (true) {

                response = new ResponseEntity<>(new SystemItemHistoryResponse(null), HttpStatus.OK);
            } else {
                // TODO ошибки по другому
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping(value = "test")
    public ResponseEntity<Error> getTest() {
        System.out.println(" Что пришло ! ураааа!!!!!");
        return new ResponseEntity<>(new Error(200, "Success"), HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleException(HttpMessageNotReadableException e) {
        System.out.println("Ошибка в JSONE");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
/*
{
        "items": [
            {
                "type": "FOLDER",
                "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                "parentId": null
            }
        ],
        "updateDate": "2022-02-01T12:00:00Z"
    },
    {
        "items": [
            {
                "type": "FOLDER",
                "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            },
            {
                "type": "FILE",
                "url": "/file/url1",
                "id": "863e1a7a-1304-42ae-943b-179184c077e3",
                "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "size": 128
            },
            {
                "type": "FILE",
                "url": "/file/url2",
                "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "size": 256
            }
        ],
        "updateDate": "2022-02-02T12:00:00Z"
    },
    {
        "items": [
            {
                "type": "FOLDER",
                "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            },
            {
                "type": "FILE",
                "url": "/file/url3",
                "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
                "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "size": 512
            },
            {
                "type": "FILE",
                "url": "/file/url4",
                "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "size": 1024
            }
        ],
        "updateDate": "2022-02-03T12:00:00Z"
    },
    {
        "items": [
            {
                "type": "FILE",
                "url": "/file/url5",
                "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "size": 64
            }
        ],
        "updateDate": "2022-02-03T15:00:00Z"
    }


 */