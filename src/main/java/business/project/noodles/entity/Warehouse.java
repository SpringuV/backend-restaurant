package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@Setter
@Table(name = "warehouse")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id_warehouse;

    @Column(name = "code_whs")
    String code_warehouse;

    @Column(name = "name_whs")
    String name_warehouse;

    @Column(name = "address_whs")
    String address_warehouse;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    List<InventoryTransaction> inventory_transactions;
}
