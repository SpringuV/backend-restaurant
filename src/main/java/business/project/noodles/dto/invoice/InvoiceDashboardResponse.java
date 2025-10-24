package business.project.noodles.dto.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDashboardResponse {

    String id_invoice;
    Double discount;
    String payment_method; // CASH, BANKING
    String payment_status; // PENDING, PAID, REFUNDED
    String note;
    Instant created_at;
    Instant updated_at;

    // User info
    UserInfo user;

    // Order info
    OrderInfo orders;

    // Customer info
    CustomerInfo customer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserInfo {
        String full_name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CustomerInfo {
        String phone_number_cus;
        String name_cus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderInfo {

        Long id_order;
        String note_order;
        String order_status; // PENDING, READY, COMPLETED, CANCELLED
        Double total_amount;
        List<OrderItemInfo> order_item_list;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderItemInfo {

        Long id_order_item;
        String name_food;
        Integer quantity;
        Double price;
        String note;
    }
}
