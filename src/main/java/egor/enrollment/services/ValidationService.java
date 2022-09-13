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