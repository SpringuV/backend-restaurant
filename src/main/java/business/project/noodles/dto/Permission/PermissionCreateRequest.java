package business.project.noodles.dto.Permission;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PermissionCreateRequest {
    String permission_name_req;
    String description_req;
}
