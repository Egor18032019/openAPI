package egor.enrollment.controllers;

import egor.enrollment.model.Item;
import egor.enrollment.services.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerTest extends AbstractRequestControllerTest {

    private final ItemService service;

    @Autowired
    public ItemControllerTest(ItemService service) {
        this.service = service;
    }

    @Test
    void getTest() throws Exception {

        perform(MockMvcRequestBuilders.get("/" + "test"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    void addProductsOK() throws Exception {
        String testJson1 = "{\n" +
                "        \"items\": [\n" +
                "            {\n" +
                "                \"type\": \"FOLDER\",\n" +
                "                \"id\": \"d515e43f-f3f6-4471-bb77-6b455017a2d2\",\n" +
                "                \"parentId\": \"069cb8d7-bbdd-47d3-ad8f-82ef4c269df1\",\n" +
                "            },\n" +
                "            {\n" +
                "                \"type\": \"FILE\",\n" +
                "                \"url\": \"/file/url1\",\n" +
                "                \"id\": \"863e1a7a-1304-42ae-943b-179184c077e3\",\n" +
                "                \"parentId\": \"d515e43f-f3f6-4471-bb77-6b455017a2d2\",\n" +
                "                \"size\": 128\n" +
                "            },\n" +
                "            {\n" +
                "                \"type\": \"FILE\",\n" +
                "                \"url\": \"/file/url2\",\n" +
                "                \"id\": \"b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4\",\n" +
                "                \"parentId\": \"d515e43f-f3f6-4471-bb77-6b455017a2d2\",\n" +
                "                \"size\": 256\n" +
                "            }\n" +
                "        ],\n" +
                "        \"updateDate\": \"2022-02-02T12:00:00Z\"\n" +
                "    }";
        perform(MockMvcRequestBuilders.post("/imports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

    }

    @Test
    void addProductsFailed() throws Exception {
        String testJson1 = "{\n" +
                "        \"items\": [\n" +
                "            {\n" +
                "                \"type\": \"FILE\",\n" +
                "                \"url\": \"/file/url5\",\n" +
                "                \"id\": \"73bc3b36-02d1-4245-ab35-3106c9ee1c65\",\n" +
                "                \"parentId\": \"1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2\",\n" +
                "                \"size\": 64\n" +
                "            }\n" +
                "        ],\n" +
                "        \"updateDate\": \"2022-02-03\"\n" +
                "    }";
        perform(MockMvcRequestBuilders.post("/imports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson1))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

    }

    @Test
    void addProductsAndGet() throws Exception {
        perform(
                MockMvcRequestBuilders.post("/imports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestData.json))
                .andExpect(status().isOk())

                .andDo(print())
                .andReturn();
        Item item = service.findItemInDB(TestData.id1);
        TestData.assertEquals(item, TestData.item);
    }

}
