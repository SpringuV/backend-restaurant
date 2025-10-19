package business.project.noodles.controller;

import business.project.noodles.dto.ApiResponse;
import business.project.noodles.dto.food.FoodResponse;
import business.project.noodles.service.FoodService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/food")
public class FoodController {

    FoodService foodService;

    @GetMapping
    ApiResponse<List<FoodResponse>> getListFood(){
        return ApiResponse.<List<FoodResponse>>builder()
                .message("Get List Food")
                .result(foodService.getListFood())
                .build();
    }

}
