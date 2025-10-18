package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", indexes = {
        @Index(name = "idx_user_phone", columnList = "phone_number")
})
@ToString(exclude = { "order_list", "inventory_transaction_list" }) // Tránh vòng lặp toString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id_user;

    @Column(name = "username")
    String username;

    @Column(name = "phone_number", unique = true)
    String phone_number;

    @Column(name = "full_name")
    String full_name;

    @Column(name = "pass")
    String pass;

    @ManyToOne
    @JoinColumn(name = "user_role")
    Role role;

    public enum UserStatus {
        ACTIVE,      // đang làm
        INACTIVE,    // tạm nghỉ, không hoạt động
        RESIGNED     // đã nghỉ việc
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Orders> order_list;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<InventoryTransaction> inventory_transaction_list;

    @Column(name = "created_at", updatable = false)
    Instant created_at;


    @PrePersist
    protected void onCreate() {
        this.created_at = Instant.now();
    }

}
