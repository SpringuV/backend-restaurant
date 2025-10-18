package business.project.noodles.dto.invoice;

import business.project.noodles.entity.Invoice;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceCreateRequest {
    String id_user;
    Long id_order;
    String phone_number_cus;
    double discount;
    Invoice.PaymentMethod payment_method;
    Invoice.PaymentStatus payment_status;
    String note;
}
