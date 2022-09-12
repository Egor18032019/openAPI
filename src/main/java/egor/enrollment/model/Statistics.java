package egor.enrollment.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "statistics")
public class Statistics {
    @Id
    @GeneratedValue
    int id;
    @Column
    int size;
    @Column
    @NotNull
    ZonedDateTime date;
}
