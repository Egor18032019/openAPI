package egor.enrollment.utility;

import egor.enrollment.components.schemas.SystemItem;
import egor.enrollment.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ConverterItemToSystemItem {
    public static SystemItem toShopUnit(Item item) {
        String id = item.getId();
        String url = item.getUrl();
        String date = item.getDate().toString();
//        String date = item.getDate().toString() + ":00.Z";
        String parentId = null;
        if (null != item.getParent()) parentId = item.getParent().getId();
        String type = item.getType().toString();
        Integer size = item.getSize();
        List<SystemItem> childrenToSystemItemList = new ArrayList<>();
        if (!item.getChildren().isEmpty()) {
            // если есть дети то
            childrenToSystemItemList = toSystemItemList(item.getChildren());
        } else {
            if (type.equals("FILE")) {
                //- для пустой папки поле children равно пустому массиву, а для файла равно null
                return new SystemItem(id, url, type, parentId, date, size, null);
            }
        }

        return new SystemItem(id, url, type, parentId, date, size, childrenToSystemItemList);
    }

    public static List<SystemItem> toSystemItemList(List<Item> childrentoSystemItemList) {
        List<SystemItem> shopUnitList = new ArrayList<>();
        for (Item it : childrentoSystemItemList) shopUnitList.add(toShopUnit(it));
        return shopUnitList;
    }
}
