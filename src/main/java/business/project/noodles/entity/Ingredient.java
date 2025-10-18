package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ingredient", unique = true)
    String id_ingredient;

    @Column(name = "name_ingredient", nullable = false, unique = true)
    String name_ingredients; // tên nguyên liệu

    @Column(name = "prices")
    double prices;

    @Column(name = "quantity")
    int quantity; // hàng tồn kho hiện tại

    public enum UnitOfMeasurement {
        KG, GRAM, CÁI, BÓ, THÙNG, HỘP, TÚI, CHIẾC, VIÊN, ĐÔI, LỌ, BÌNH
    }
    @Column(name = "unit_of_measurement")
    @Enumerated(EnumType.STRING)
    UnitOfMeasurement unit_of_measurement; // đơn vị đo: cái, kg

    @Column(name = "description")
    String description;

    @Column(name = "supplier")
    String supplier;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    List<InventoryTransaction> inventory_transaction_list;

    @Column(name = "created_at", updatable = false)
    Instant created_at;

    @Column(name = "updated_at")
    Instant updated_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = Instant.now();
        this.updated_at = this.created_at;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = Instant.now();
    }
}
