package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(name = "bookings", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "start_date", nullable = false)
    LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    LocalDateTime end;
    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    Item item;
    @OneToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    User booker;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
