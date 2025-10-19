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
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    Long id_order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    User user;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    Invoice invoice;

    @Column(name = "note_order")
    String note_order;

    @Column(name = "sum_human")
    @Builder.Default
    Integer sum_human = 1;

    //	Tránh NullPointerException khi dùng builder hoặc thêm phần tử vào list.
//	Khi dùng builder của Lombok, nếu không thêm @Builder.Default, các list sẽ là null.
    @ManyToOne
    @JoinColumn(name = "id_table")
    GuestTable guest_table;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // xóa 1 phần tử khỏi order_item_list, Hibernate sẽ tự động DELETE bản ghi tương ứng trong DB.
    List<OrderItem> order_item_list;

    //	Tăng type safety, tránh nhập sai giá trị trạng thái.
//	Lưu vào DB dạng String, dễ đọc/tra cứu.
    public enum OrderStatus { PENDING, PREPARING, READY, SERVED, CANCELLED }
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    OrderStatus order_status;

    public enum OrderType { DELIVERY, TAKEAWAY, DINE_IN }
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    OrderType order_type; // "DINE_IN", "TAKEAWAY", "DELIVERY"

    @Column(name = "total_amount")
    double total_amount;

    @Column(name = "estimated_ready_time")
    int estimated_ready_time;

    @ManyToOne
    @JoinColumn(name = "id_customer")
    Customer customer;

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
