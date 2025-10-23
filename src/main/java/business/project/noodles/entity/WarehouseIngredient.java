package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "warehouse_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id_warehouse_ingredient;

    @ManyToOne
    @JoinColumn(name = "id_warehouse", nullable = false)
    Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "id_ingredient", nullable = false)
    Ingredient ingredient;

    @Column(name = "quantity", nullable = false)
    int quantity; // tồn kho của nguyên liệu đó tại kho này
}
