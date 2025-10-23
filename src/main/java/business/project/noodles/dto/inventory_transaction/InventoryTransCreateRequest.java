package business.project.noodles.dto.inventory_transaction;

import business.project.noodles.entity.InventoryTransaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransCreateRequest {
    int quantity; // số lượng nhập/xuất
    InventoryTransaction.TypeInventoryTransaction type; // "IMPORT" hoặc "EXPORT"
    String note; // tùy chọn
    String id_user;
    String name_ingredients;
    String name_supplier;
    String code_warehouse;
}
