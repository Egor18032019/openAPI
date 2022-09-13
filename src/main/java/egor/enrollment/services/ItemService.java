package egor.enrollment.services;

import egor.enrollment.components.schemas.*;
import egor.enrollment.exception.BadRequestException;
import egor.enrollment.model.Item;
import egor.enrollment.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ItemService {
    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

//          - в одном запросе не может быть двух элементов с одинаковым id
    @Transactional
    public void saveItems(SystemItemImportRequest request) {
        List<SystemItemImport> items = request.getItems();
        System.out.println(items);
        LocalDateTime date = getDate(request.getUpdateDate());
        List<Item> itemsForSaveInDB = new ArrayList<>();
        for (SystemItemImport item : items) {
            String url = item.getUrl();

//            - поле id не может быть равно null
            String workdeID = item.getId();
            if (!workdeID.isBlank()) {
                throw new BadRequestException("Validation Failed");
            }
            SystemItemType type;
            if (item.getType().equals("FILE")) {
                type = SystemItemType.FILE;
//                        - поле url при импорте папки всегда должно быть равно null
                if (!url.isBlank()) {
                    throw new BadRequestException("Validation Failed");
                }
//                - поле size при импорте папки всегда должно быть равно null

            } else {
//                - поле size для файлов всегда должно быть больше 0

                type = SystemItemType.FOLDER;
            }
            Item workderItem = new Item(workdeID, url, type, date, item.getSize());
            Optional<Item> oldItem = repository.findById(workdeID);
            /*
             Изменение типа элемента с папки на файл и с файла на папку не допускается.
        TODO  надо ли выдавать клиенту информаци об этом ?? Жду ответа в телеге
             */
            String parentId = item.getParentId();

            Optional<Item> parentItem = repository.findById(parentId);
            if (oldItem.isPresent()) {
                if (oldItem.get().getType() != workderItem.getType()) {
                    throw new BadRequestException("Validation Failed");
                }
//            Импортирует элементы файловой системы. Элементы импортированные повторно обновляют текущие.


                if (parentItem.isPresent()) {
                    Item workedParentItem = parentItem.get();
                    if (workedParentItem.getType() != SystemItemType.FOLDER) {
                        //                - родителем элемента может быть только папка
                        throw new BadRequestException("Validation Failed");
                    }
                    Integer size = workedParentItem.getSize();
                    System.out.println("size");
                    System.out.println(size);
                    if (size == null) {
                        size = 0;
                    }
                    Integer newSize = workderItem.getSize() - oldItem.get().getSize();
                    workedParentItem.setSize(newSize + size);
                    workedParentItem.setDate(workderItem.getDate());
                    repository.save(workedParentItem);
                }
            } else {


                if (parentItem.isPresent()) {
                    Item workedParentItem = parentItem.get();
                    Integer size = workedParentItem.getSize();
                    System.out.println("size");
                    System.out.println(size);
                    if (size == null) {
                        size = 0;
                    }
                    workedParentItem.setSize(workderItem.getSize() + size);
                    workedParentItem.setDate(workderItem.getDate());
                    repository.save(workedParentItem);
                } else {
                    workderItem.setParent(null);
                }
            }
            itemsForSaveInDB.add(workderItem);
        }
        repository.saveAll(itemsForSaveInDB);
    }

    public LocalDateTime getDate(String strDate) {
        return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH));
    }

    @Transactional
    public List<Item> findAllItems() {
        return repository.findAll();
    }


    @Transactional
    public Item findItemInDB(String id) {
        Optional<Item> item = repository.findById(id);
        return item.orElse(null);
    }

    @Transactional
    public void deleteItemInDB(Item item, LocalDateTime updDate) {
        String id = item.getId();
        Item parent = repository.findByParentId(id);
        if (parent != null) {
            parent.setDate(updDate);
            Integer newSize = parent.getSize() - item.getSize();
            parent.setSize(newSize);
            repository.save(parent);
        }
        List<Item> childrenForDelete = item.getChildren();
        repository.deleteAll(childrenForDelete);
        repository.deleteById(id);

    }

    public SystemItemHistoryResponse getStatisticItems(LocalDateTime dateEndUnit) {

        LocalDateTime dateStartUnit = dateEndUnit.minusHours(24);
//
//        List<Item> stat = repository.findAllByDateBetween(dateStartUnit, dateEndUnit);
//        SystemItemHistoryUnit[] units = new SystemItemHistoryUnit[stat.size()];
//        for (int i = 0; i < stat.size(); i++) {
//            SystemItemHistoryUnit s = Utils.statisticUnitCreate(stat.get(i));
//            units[i] = s;
//            //TODO сделать в билдер
//        }
//        SystemItemHistoryResponse systemItemHistoryResponse = new SystemItemHistoryResponse();
//        systemItemHistoryResponse.setSystemItemHistoryUnits(units);
        return null;
    }

    public SystemItemHistoryResponse getStatisticItems(String id, LocalDateTime dateStartUnit, LocalDateTime
            dateEndTime) {
        Item node = repository.findById(id).orElse(null);
        if (node == null) {
            System.out.println("не в БД ничего");
            return null;
        }
//        // один вроде должен быть же
//        // почему тогда ответ массивом ?
//        // TODO спросить в телеге
//        System.out.println(node.toString());
//
//        if (dateEndTime == null && dateStartUnit == null) {
//            SystemItemHistoryUnit s = Utils.statisticUnitCreate(node);
//            SystemItemHistoryUnit[] units = new SystemItemHistoryUnit[1];
//            units[0] = s;
//            SystemItemHistoryResponse systemItemHistoryResponse = new SystemItemHistoryResponse();
//            systemItemHistoryResponse.setSystemItemHistoryUnits(units);
//            return systemItemHistoryResponse;
//        } else {
//            if (dateStartUnit == null) {
//                if (node.getDate().isBefore(dateEndTime)) {
//                    SystemItemHistoryUnit s = Utils.statisticUnitCreate(node);
//                    SystemItemHistoryUnit[] units = new SystemItemHistoryUnit[1];
//                    units[0] = s;
//                    SystemItemHistoryResponse systemItemHistoryResponse = new SystemItemHistoryResponse();
//                    systemItemHistoryResponse.setSystemItemHistoryUnits(units);
//                    return systemItemHistoryResponse;
//                }
//            }
//            if (dateEndTime == null) {
//                if (node.getDate().isAfter(dateStartUnit)) {
//                    SystemItemHistoryUnit s = Utils.statisticUnitCreate(node);
//                    SystemItemHistoryUnit[] units = new SystemItemHistoryUnit[1];
//                    units[0] = s;
//                    SystemItemHistoryResponse systemItemHistoryResponse = new SystemItemHistoryResponse();
//                    systemItemHistoryResponse.setSystemItemHistoryUnits(units);
//                    return systemItemHistoryResponse;
//                }
//            }
//            if(dateEndTime!=null&dateStartUnit!=null){
//                if (node.getDate().isAfter(dateStartUnit)) {
//                    SystemItemHistoryUnit s = Utils.statisticUnitCreate(node);
//                    SystemItemHistoryUnit[] units = new SystemItemHistoryUnit[1];
//                    units[0] = s;
//                    SystemItemHistoryResponse systemItemHistoryResponse = new SystemItemHistoryResponse();
//                    systemItemHistoryResponse.setSystemItemHistoryUnits(units);
//                    return systemItemHistoryResponse;
//                }
//            }
//            List<Item> stat = repository.findAllByDateBetween(dateStartUnit, dateEndTime);
//            System.out.println(stat.toString());
//            List<Item> filterStat = stat.stream().filter(f -> f.getId().equals(id)).collect(Collectors.toList());
//            System.out.println(filterStat);
//            SystemItemHistoryUnit s = Utils.statisticUnitCreate(filterStat.get(0));
//            SystemItemHistoryUnit[] units = new SystemItemHistoryUnit[1];
//            units[0] = s;
//            SystemItemHistoryResponse systemItemHistoryResponse = new SystemItemHistoryResponse();
//            systemItemHistoryResponse.setSystemItemHistoryUnits(units);
        return null;

    }


}

//}
