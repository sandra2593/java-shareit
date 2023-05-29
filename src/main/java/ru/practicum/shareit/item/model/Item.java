package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Table(name = "items", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String description;
    @Column(name = "is_available",nullable = false)
    Boolean available;
    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    User owner;
    @Column(name = "request_id")
    int request;
}
