package business.project.noodles.dto.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceCreateResponse {
    Boolean is_created;
    Instant created_at;
}
