package business.project.noodles.dto.ingredient;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeleteIngredientResponse {
    Boolean is_deleted;
    String message;
}
