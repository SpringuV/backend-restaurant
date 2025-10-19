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
@Table(name = "order_item")
public class OrderItem {

    @EmbeddedId
    KeyOrderItem keyOrderItem;

    @ManyToOne
    @MapsId("id_order")
    @JoinColumn(name = "id_order")
    Orders order;

    @ManyToOne
    @MapsId("id_food")
    @JoinColumn(name = "id_food")
    Food food;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "special_requests")
    String special_requests; // customer notes

    @Column(name="note")
    String note;

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
