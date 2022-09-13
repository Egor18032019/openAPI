package egor.enrollment.services;

import egor.enrollment.components.schemas.*;
import egor.enrollment.model.Item;
import egor.enrollment.repository.ItemRepository;
import egor.enrollment.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public void saveItems(SystemItemImportRequest request) {
        List<SystemItemImport> items = request.getItems();
        LocalDateTime date = getDate(request.getUpdateDate());
        List<Item> itemsForSaveInDB = new ArrayList<>();
        List<String> parentIds = new ArrayList<>();
        for (SystemItemImport item : items) {
            SystemItemType type;
            if (item.getType().equals("FILE")) type = SystemItemType.FILE;
            else type = SystemItemType.FOLDER;

            itemsForSaveInDB.add(new Item(item.getId(), item.getUrl(), type, date, item.getSize()));
            parentIds.add(item.getParentId());
            for (int i = 0; i < itemsForSaveInDB.size(); i++) {
                Item element = itemsForSaveInDB.get(i);
                String parentId = parentIds.get(i);
                if (null == parentId) element.setParent(null);
                else {
                    boolean found = false;
                    for (int j = 0; j < parentIds.size(); j++) {
                        Item potentialParent = itemsForSaveInDB.get(j);
                        if (potentialParent.getId().equals(parentId)) {
                            element.setParent(potentialParent);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Optional<Item> potentialParent = repository.findById(parentId);
                        if (potentialParent.isPresent()) element.setParent(potentialParent.get());
                        else element.setParent(null);
                    }
                }
                itemsForSaveInDB.set(i, element);
            }
        }
        if (itemsForSaveInDB.size() != 0) updateDate(itemsForSaveInDB.get(0));
        repository.saveAll(itemsForSaveInDB);
    }

    public LocalDateTime getDate(String strDate) {
        return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH));
    }

    private void updateDate(Item item) {
        Item parent = item.getParent();
        if (null != parent) {
            parent.setDate(item.getDate());
            updateDate(parent);
        }
    }

    @Transactional
    public Item findItemOrNull(String id) {
        Optional<Item> product = repository.findById(id);
        return product.orElse(null);
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
    public void deleteItemInDB(Item item, String date) {
        String id = item.getId();
        LocalDateTime updDate = getDate(date);
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

    public SystemItemHistoryResponse getStatisticItems(String id, LocalDateTime dateStartUnit, LocalDateTime dateEndTime) {
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
