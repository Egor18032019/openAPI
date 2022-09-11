package egor.enrollment.services;

import egor.enrollment.components.schemas.SystemItemImport;
import egor.enrollment.components.schemas.SystemItemImportRequest;
import egor.enrollment.components.schemas.SystemItemType;
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


    @Transactional
    public void saveProducts(SystemItemImportRequest request) {
        List<SystemItemImport> items = request.getItems();
        LocalDateTime date = getDate(request.getUpdateDate());
        List<Item> itemsForSaveInDB = new ArrayList<>();
        List<String> parentIds = new ArrayList<>();
        for (SystemItemImport item : items) {
            SystemItemType type;
            //TODO переделать енумы
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

    private LocalDateTime getDate(String strDate) {
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
        Item parent = repository.findByParentId(id);
        LocalDateTime updDate = getDate(date);
        parent.setDate(updDate);
        repository.save(parent);
        List<Item> childrenForDelete = item.getChildren();
        repository.deleteAll(childrenForDelete);
        repository.deleteById(id);

    }
}
