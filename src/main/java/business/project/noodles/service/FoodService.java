package business.project.noodles.service;

import business.project.noodles.dto.food.FoodResponse;
import business.project.noodles.repository.FoodRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FoodService {
    FoodRepository foodRepository;
    public List<FoodResponse> getListFood(){
        return foodRepository.findAll().stream().map(item ->
                FoodResponse.builder()
                        .id_food(item.getId_food())
                        .name_food(item.getName_food())
                        .type_food(item.getType_food())
                        .price(item.getPrice())
                        .image_url(item.getImage_url())
                        .description(item.getDescription())
                        .build())
                .toList();
    }
}
