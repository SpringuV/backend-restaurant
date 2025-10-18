package business.project.noodles.dto.invoice;

import business.project.noodles.dto.User.UserAttributeResponse;
import business.project.noodles.dto.customer.CustomerAttributeResponse;
import business.project.noodles.dto.order.OrdersAttributeResponse;
import business.project.noodles.entity.Invoice;
import jakarta.persistence.Entity;
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
public class InvoiceCreateResponse {
    UserAttributeResponse userAttributeResponse;
    OrdersAttributeResponse ordersAttributeResponse;
    CustomerAttributeResponse customerAttributeResponse;
    double discount;
    Invoice.PaymentMethod payment_method;
    Invoice.PaymentStatus payment_status;
    String note;
    Instant created_at;
}
