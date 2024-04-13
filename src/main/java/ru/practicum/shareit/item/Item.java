package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = {"id"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
