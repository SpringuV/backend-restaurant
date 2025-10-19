package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "foods")
public class Food {
    @Id
    @Column(name = "id_food", unique = true)
    String id_food;

    @Column(name= "name_food")
    String name_food;

    @Column(name= "price")
    Double price;

    @Column(name="description")
    String description;

    @Column(name="image_url")
    String image_url;

    public enum FoodType {
        APPETIZER,      // món khai vị
        MAIN_COURSE,    // món chính
        DESSERT,        // tráng miệng
        DRINK,          // đồ uống
        SPICY,          // cay
        SWEET,          // ngọt
        VEGETARIAN,     // chay
        SEAFOOD,        // hải sản
        FAST_FOOD       // đồ ăn nhanh
    }
    @Enumerated(EnumType.STRING)
    @Column(name="type_food")
    FoodType type_food;

    @Builder.Default
    @Column(name = "is_available")
    boolean is_available = true; // daily availability

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    List<OrderItem> order_item_list;
}
