package business.project.noodles.dto.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAttributeResponse {
    String id_user;
    String full_name;
}
