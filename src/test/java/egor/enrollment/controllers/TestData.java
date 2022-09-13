package egor.enrollment.controllers;

import egor.enrollment.components.schemas.SystemItemType;
import egor.enrollment.model.Item;
import egor.enrollment.utility.Utils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestData {
    public static String id1 = "73bc3b36-02d1-4245-ab35-3106c9ee1c65";
    public static String json = " {\n" +
            "        \"items\": [\n" +
            "            {\n" +
            "                \"type\": \"FILE\",\n" +
            "                \"url\": \"/file/url5\",\n" +
            "                \"id\": \"73bc3b36-02d1-4245-ab35-3106c9ee1c65\",\n" +
            "                \"parentId\": \"1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2\",\n" +
            "                \"size\": 64\n" +
            "            }\n" +
            "        ],\n" +
            "        \"updateDate\": \"2022-02-03T15:00:00Z\"\n" +
            "    }";
    static LocalDateTime date = Utils.getDate("2022-02-03T15:00:00Z");
    public static Item item = new Item(id1, "/file/url5", SystemItemType.FILE, date, 64);

    public static <T> void assertEquals(T actual, T expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
