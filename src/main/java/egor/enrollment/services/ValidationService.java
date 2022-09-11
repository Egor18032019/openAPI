package egor.enrollment.services;

import egor.enrollment.components.schemas.SystemItemImport;
import egor.enrollment.components.schemas.SystemItemImportRequest;
import egor.enrollment.components.schemas.SystemItemType;
import egor.enrollment.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ValidationService {
    @Autowired
    private final ItemService itemService;

    public ValidationService(ItemService itemService) {
        this.itemService = itemService;
    }


    public boolean isISO8601(String date) {
        return date.matches("^([+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24:?00)([.,]\\d+(?!:))?)?(\\17[0-5]\\d([.,]\\d+)?)?([zZ]|([+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$");
    }

    private boolean isValidParent(String parentId, List<SystemItemImport> items) {

        for (SystemItemImport item : items)
            if (item.getId().equals(parentId))
//                - родителем элемента может быть только папка
                return item.getType().equals("FOLDER");
        List<Item> itemsInDB = itemService.findAllItems();
        for (Item product : itemsInDB)
//            принадлежность к папке определяется полем parentId
            if (product.getId().equals(parentId))
                return product.getType() == SystemItemType.FOLDER;
        return false;
    }


    private boolean isImport(SystemItemImport systemItemImport, List<SystemItemImport> items) {
        String id = systemItemImport.getId();
        String url = systemItemImport.getUrl();
        String parentId = systemItemImport.getParentId();
        SystemItemType type = SystemItemType.valueOf(systemItemImport.getType());
        Integer size = systemItemImport.getSize();
        switch (type) {
            case FILE: // файл
                return (null != id &&
//                        - поле url при импорте папки всегда должно быть равно null
            (null != url && url.length() <= 256) &&
                        (null == parentId || isValidParent(parentId, items)) &&
                        (size >= 0));
            case FOLDER: // Папка
                return (null != id &&
                        (null == url)&&
                         (null == parentId || isValidParent(parentId, items)) &&
                        (size == null));
            default:
                return false;
        }
    }

    @Transactional
    public boolean isSystemItemImportRequest(SystemItemImportRequest request) {
        if (isISO8601(request.getUpdateDate())) {
            List<SystemItemImport> items = request.getItems();
            for (SystemItemImport item : items) {
                if (!isImport(item, items)) return false;
            }
            return true;
        }
        return false;
    }
}
/*

          - id каждого элемента является уникальным среди остальных элементов
          - поле id не может быть равно null
          - родителем элемента может быть только папка
          - принадлежность к папке определяется полем parentId
          - элементы могут не иметь родителя (при обновлении parentId на null элемент остается без родителя)
          - поле url при импорте папки всегда должно быть равно null
          - размер поля url при импорте файла всегда должен быть меньше либо равным 255
          - поле size при импорте папки всегда должно быть равно null
          - поле size для файлов всегда должно быть больше 0
          - при обновлении элемента обновленными считаются **все** их параметры
          - при обновлении параметров элемента обязательно обновляется поле **date** в соответствии с временем обновления
          - в одном запросе не может быть двух элементов с одинаковым id
          - дата обрабатывается согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, ответом будет код 400.

 */