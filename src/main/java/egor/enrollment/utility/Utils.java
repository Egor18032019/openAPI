package egor.enrollment.utility;

import egor.enrollment.components.schemas.SystemItemHistoryUnit;
import egor.enrollment.model.Item;

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

}
