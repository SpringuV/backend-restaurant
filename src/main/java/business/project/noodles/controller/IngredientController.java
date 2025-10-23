package business.project.noodles.controller;

import business.project.noodles.dto.ApiResponse;
import business.project.noodles.dto.ingredient.*;
import business.project.noodles.service.IngredientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/ingredients")
public class IngredientController {

    IngredientService ingredientService;

    @PostMapping
    ApiResponse<IngredientCreateResponse> createIngredient(@RequestBody IngredientCreateRequest request) {
        return ApiResponse.<IngredientCreateResponse>builder()
                .message("Create Ingredient")
                .result(ingredientService.createIngredient(request))
                .build();
    }


    @PatchMapping("/{id}")
    ApiResponse<IngredientUpdateResponse> updateIngredient(@PathVariable("id") String id, @RequestBody IngredientUpdateRequest request){
        return ApiResponse.<IngredientUpdateResponse>builder()
                .message("Update Ingredient")
                .result(ingredientService.updateIngredient(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<IngredientResponse>> loadAllIngredient(){
        return ApiResponse.<List<IngredientResponse>>builder()
                .message("Load All Ingredient")
                .result(ingredientService.loadAllIngredientByCodeWarehouse())
                .build();
    }


    @DeleteMapping("/{id_ingredient}")
    ApiResponse<DeleteIngredientResponse> deleteIngredient(@PathVariable("id_ingredient") String id_ingre){
        return ApiResponse.<DeleteIngredientResponse>builder()
                .message("Delete Ingredient")
                .result(ingredientService.deleteIngredient(id_ingre))
                .build();
    }

    @GetMapping("/supplier/{code_warehouse}")
    ApiResponse<List<IngredientOfSupplierResponse>> loadNameSupplierAndIngredientResponseApi(@PathVariable("code_warehouse") String code_house){
        return ApiResponse.<List<IngredientOfSupplierResponse>>builder()
                .message("Get list supplier and ingredient")
                .result(ingredientService.loadSupplierAndIngredientInItByCodeWarehouse(code_house))
                .build();
    }
}
