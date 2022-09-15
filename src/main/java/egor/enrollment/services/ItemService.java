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
        LocalDateTime updateDate = getDate(request.getUpdateDate());
//        List<Item> itemsForSaveInDB = new ArrayList<>();
        Map<String, Integer> parentSizeMap = new HashMap<>();
        Map<String, LocalDateTime> parentDateMap = new HashMap<>();
        Set<String> elements = new HashSet<>();
        for (SystemItemImport item : items) {
            String workdeID = item.getId();
            String url = item.getUrl();
            Integer size = item.getSize();
            if (!elements.add(workdeID)) {
                System.out.println(" - в одном запросе не может быть двух элементов с одинаковым i");
                throw new BadRequestException("Validation Failed");
            }
            if (workdeID.isEmpty()) {
                System.out.println(workdeID + "  поле id не может быть равно null");
                throw new BadRequestException("Validation Failed");
            }
            SystemItemType type;
            if (item.getType().equals("FILE")) {
                type = SystemItemType.FILE;
                if (url.isEmpty()) {
                    System.out.println("- поле url при импорте папки всегда должно быть равно null");
                    throw new BadRequestException("Validation Failed");
                }
                if (size <= 0) {
                    System.out.println("- поле size для файлов всегда должно быть больше 0");
                    throw new BadRequestException("Validation Failed");
                }
            } else {
                if (item.getType().equals("FOLDER")) {
                    if (size != null) {
                        System.out.println("- поле size при импорте папки всегда должно быть равно null");
                        throw new BadRequestException("Validation Failed");
                    }
                    type = SystemItemType.FOLDER;
                } else {
                    System.out.println("Ошибка в типе не FOLDER и не FILE");
                    throw new BadRequestException("Validation Failed");
                }
            }
            Item workderItem = new Item(workdeID, url, type, updateDate, size);
            Optional<Item> oldItem = repository.findById(workdeID);
            String parentId = item.getParentId();
            Optional<Item> parentItem = Optional.empty();
            if (parentId != null) {
                parentItem = repository.findById(parentId);
            }
            if (oldItem.isPresent()) {
                Integer sizeOldItem = oldItem.get().getSize();
                if (!oldItem.get().getType().equals(type)) {
                    System.out.println("Изменение типа элемента с папки на файл и с файла на папку не допускается.");
                    throw new BadRequestException("Validation Failed");
                }
                if (parentItem.isPresent()) {
                    Item workedParentItem = parentItem.get();
                    workderItem.setParent(workedParentItem);
                    if (!workedParentItem.getType().equals(SystemItemType.FOLDER)) {
                        System.out.println("  родителем элемента может быть только папка");
                        throw new BadRequestException("Validation Failed");
                    }
                    Integer sizeParentFolder = parentSizeMap.get(parentId);
                    if (size == null && sizeParentFolder == null) {
                        parentSizeMap.put(parentId, null);
                    }
                    if (sizeParentFolder == null) {
                        parentSizeMap.put(parentId, size);
                    }
                    if (size == null) {
                        parentSizeMap.put(parentId, sizeParentFolder);
                    }
                    if (size != null && sizeParentFolder != null) {
                        Integer newSizeParent = sizeParentFolder + size - sizeOldItem;
                        parentSizeMap.put(parentId, newSizeParent);
                    }
                    parentDateMap.put(parentId, updateDate);
                } else {
                    // из условия не понятно что делать в случаи если в поле парнетИд есть занчения но они не валидны
                    //TODO  спросить в телеге
                }
            } else {
                if (parentItem.isPresent()) {
                    Item workedParentItem = parentItem.get();
                    workderItem.setParent(workedParentItem);
                    Integer sizeParentFolder = parentSizeMap.get(parentId);

                    if (size == null && sizeParentFolder == null) {
                        parentSizeMap.put(parentId, null);
                    }
                    if (sizeParentFolder == null) {
                        parentSizeMap.put(parentId, size);
                    }
                    if (size == null) {
                        parentSizeMap.put(parentId, sizeParentFolder);
                    }
                    if (size != null && sizeParentFolder != null) {
                        // так как не первая итерация то так
                        parentSizeMap.put(parentId, sizeParentFolder + size);
                    }
                    parentDateMap.put(parentId, updateDate);
                } else {
                    // из условия не понятно что делать в случаи если в поле парнетИд есть занчения но они не валидны
                    //TODO  спросить в телеге
                }
            }
            //TODO обдумать что будет если пришел родительно его нет в бд ??
            repository.save(workderItem);
//            itemsForSaveInDB.add(workderItem);
        }


        //  Элементы импортированные повторно обновляют текущие.
        //из задания точно не ясно могут ли в запросе быть два разных родителя
        for (Map.Entry<String, LocalDateTime> entry : parentDateMap.entrySet()) {
            String key = entry.getKey();
            LocalDateTime date = entry.getValue();
            Integer size = parentSizeMap.get(key);
            if (key != null) {
                updateDateAndSizeOnDB(key, date, size);
            }
        }
//        repository.saveAll(itemsForSaveInDB);
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
        Item parent = item.getParent();
        if (parent != null) {
            // и у родителя родителя обновить надо
            parent.setDate(updDate);
            Integer parentSize = parent.getSize();
            if (parentSize != null) {
                Integer newSize = parentSize - item.getSize();
                parent.setSize(newSize);
            }
            repository.save(parent);
        }
        deleteChildren(item);
        repository.delete(item);
    }

    @Transactional
    public void deleteChildren(Item item) {
        List<Item> childrenForDelete = item.getChildren();
        if (childrenForDelete.size() != 0) {
            for (Item deleteItem : childrenForDelete) {
                deleteChildren(deleteItem);
            }
            repository.deleteAll(childrenForDelete);
        } else {
            repository.delete(item);
        }

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

    private void updateDateAndSizeOnDB(String key, LocalDateTime date, Integer sizeChildren) {
        if (key.equals("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1")) {
            System.out.println("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1");
        }

        Optional<Item> item = repository.findById(key);
        if (item.isPresent()) {
            Item parent = item.get();

            Integer sizeParentFolder = parent.getSize();
            Integer newSizeForParent = null;
            if (sizeChildren == null && sizeParentFolder == null) {
                parent.setSize(null);
            }
            if (sizeParentFolder == null) {
                newSizeForParent = sizeChildren;
                parent.setSize(newSizeForParent);
            }
            if (sizeChildren == null) {
                newSizeForParent = sizeParentFolder;
                parent.setSize(newSizeForParent);
            }
            if (sizeChildren != null && sizeParentFolder != null) {
                newSizeForParent = sizeChildren + sizeParentFolder;
                parent.setSize(newSizeForParent);
            }
            parent.setDate(date);
            repository.save(parent);
            Item nextParent = parent.getParent();
            if (nextParent != null) {
                String nextKey = nextParent.getId();
                updateDateAndSizeOnDB(nextKey, date, newSizeForParent);
            }
        }
    }

}


