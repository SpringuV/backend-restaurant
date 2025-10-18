package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_transactions")
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trans")
    Long id;

    @Column(name="quantity")
    int quantity; // số lượng nhập/xuất

    public enum TypeInventoryTransaction {IMPORT, EXPORT}
    @Column(name="type")
    @Enumerated(EnumType.STRING)
    TypeInventoryTransaction type; // "IMPORT" hoặc "EXPORT"

    @Column(name="note")
    String note; // tùy chọn

    @ManyToOne
    @JoinColumn(name = "perform_by_user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "id_ingredient")
    Ingredient ingredient;

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
