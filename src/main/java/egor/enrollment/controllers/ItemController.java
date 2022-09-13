package egor.enrollment.controllers;

import egor.enrollment.components.schemas.*;
import egor.enrollment.components.schemas.Error;
import egor.enrollment.exception.BadRequestException;
import egor.enrollment.exception.NotFoundException;
import egor.enrollment.model.Item;
import egor.enrollment.services.ItemService;
import egor.enrollment.services.ValidationService;
import egor.enrollment.utility.ConverterItemToSystemItem;
import egor.enrollment.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class ItemController {
    private final ItemService service;


    @Autowired
    public ItemController(ItemService service) {
        this.service = service;

    }

    @PostMapping(value = "/imports")
    public ResponseEntity<Error> addItems(@RequestBody SystemItemImportRequest request) {
        ResponseEntity<Error> response;
        try {
            LocalDateTime date = Utils.getDate(request.getUpdateDate());
        } catch (Exception e) {
            System.out.println("dateEnd ");
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        service.saveItems(request);
        response = new ResponseEntity<>(new Error(200, "Success"), HttpStatus.OK);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Error> deleteItems(@PathVariable String id,
                                             @RequestParam String date) {
        ResponseEntity<Error> response;
        Item item = service.findItemInDB(id);
        LocalDateTime localDateTime = null;
        try {
            localDateTime = Utils.getDate(date);
        } catch (Exception e) {
            System.out.println("dateEnd ");
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        if (item != null) {
            service.deleteItemInDB(item, localDateTime);
            response = new ResponseEntity<>(new Error(200, "OK"), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new Error(404, "Item not found"), HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<ResponseAbs> getItems(@PathVariable String id) {
        System.out.println("пришло " + id);
        if (id.isEmpty() || id.trim().isBlank()) {
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        Item item = service.findItemInDB(id);
        System.out.println(item);
        if (item != null) {
            SystemItem systemItem = ConverterItemToSystemItem.toShopUnit(item);
            return new ResponseEntity<>(systemItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Error(404, "Item not found"), HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping(value = "/updates")
    public ResponseEntity<ResponseAbs> getFiles(
            @RequestParam String date
    ) {
        LocalDateTime dateTime = null;
        try {
            dateTime = service.getDate(date);
        } catch (Exception e) {
            System.out.println("catch ");
            return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);

        }

        System.out.println(date);
        SystemItemHistoryResponse response = service.getStatisticItems(dateTime);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(value = "node/{id}/history")
    public ResponseEntity<ResponseAbs> getStatistic(@PathVariable String id,
                                                    @RequestParam(required = false) Optional<String> dateStart,
                                                    @RequestParam(required = false) Optional<String> dateEnd) {
        LocalDateTime dateStartTime = null;
        LocalDateTime dateEndTime = null;
        if (dateEnd.isPresent()) {
            try {
                dateEndTime = service.getDate(dateEnd.get());
            } catch (Exception e) {
                System.out.println("dateEnd ");
                return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);

            }
        }

        if (dateStart.isPresent()) {
            try {
                dateStartTime = service.getDate(dateStart.get());
            } catch (Exception e) {
                System.out.println("dateStartTime ");
                return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);

            }
        }
        System.out.println("Get history from: " + dateStart + " to: " + dateEnd);

        SystemItemHistoryResponse response = service.getStatisticItems(id, dateStartTime, dateEndTime);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Error(404, "Item not found"), HttpStatus.NOT_FOUND);

        }


    }

    @GetMapping(value = "test")
    public ResponseEntity<Error> getTest() {
        System.out.println(" Что пришло ! ураааа!!!!!");
        return new ResponseEntity<>(new Error(200, "Success"), HttpStatus.OK);
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
        "updateDate": "2022-09-09T12:00:00Z"
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