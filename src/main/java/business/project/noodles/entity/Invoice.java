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
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_invoice")
    String id_invoice;

    @ManyToOne
    @JoinColumn(name = "id_user")
    User user;

    @OneToOne
    @JoinColumn(name = "id_order")
    Orders orders;

    @ManyToOne
    @JoinColumn(name = "customer")
    Customer customer;

    @Column(name = "discount")
    double discount;

    public enum PaymentMethod {
        CASH, CARD, BANKING, DIGITAL_WALLET
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod payment_method;

    public enum PaymentStatus {
        PENDING, PAID, REFUNDED
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    PaymentStatus payment_status;

    @Column(name = "note")
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
