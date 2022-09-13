package egor.enrollment.utility;

import egor.enrollment.components.schemas.SystemItemHistoryUnit;
import egor.enrollment.model.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    public static SystemItemHistoryUnit statisticUnitCreate(Item item) {
        SystemItemHistoryUnit shopUnitStatisticUnit = new SystemItemHistoryUnit();
        shopUnitStatisticUnit.setId(item.getId());
        shopUnitStatisticUnit.setUrl(item.getUrl());
        if (item.getParent() != null) {
            shopUnitStatisticUnit.setParentId(item.getParent().getId());// ??
        }

        shopUnitStatisticUnit.setType(item.getType().toString());
        shopUnitStatisticUnit.setSize(item.getSize());
        shopUnitStatisticUnit.setDate(item.getDate().toString());
        return shopUnitStatisticUnit;
    }
    public static LocalDateTime getDate(String strDate) {
        return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH));
    }
}
