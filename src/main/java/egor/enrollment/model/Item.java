package egor.enrollment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import egor.enrollment.components.schemas.SystemItemType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column
    @NonNull
    private String id;
    @Column
    // размер поля url при импорте файла всегда должен быть меньше либо равным 255
    // длина строки ?
    private String url;
    @Column
    private SystemItemType type;
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Item parent;
    @Column
    private LocalDateTime date;
    @Column
    private Integer size;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Item> children = new ArrayList<>();

    public Item(@NonNull String id, String url, SystemItemType type, LocalDateTime date, Integer size) {
        this.id = id;
        this.url = url;
        this.type = type;

        this.date = date;
        this.size = size;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Statistics> statistics = new ArrayList<>();

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", type=" + type +
                ", parent=" + parent +
                ", date=" + date +
                ", size=" + size +
                ", children=" + children.size() +
                '}';
    }
}
