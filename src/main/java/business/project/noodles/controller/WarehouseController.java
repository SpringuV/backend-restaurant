package business.project.noodles.controller;

import business.project.noodles.dto.ApiResponse;
import business.project.noodles.dto.inventory_transaction.InventoryTransCreateRequest;
import business.project.noodles.dto.inventory_transaction.InventoryTransCreateResponse;
import business.project.noodles.dto.inventory_transaction.LoadInventoryTransactionResponse;
import business.project.noodles.dto.warehouse.WarehouseResponse;
import business.project.noodles.service.WarehouseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {

    WarehouseService warehouseService;

    @GetMapping
    ApiResponse<List<WarehouseResponse>> getListWarehouse(){
        return ApiResponse.<List<WarehouseResponse>>builder()
                .message("Get list ware house")
                .result(warehouseService.getListWareHouse())
                .build();
    }

    @PostMapping
    ApiResponse<InventoryTransCreateResponse> createImportExport(@RequestBody InventoryTransCreateRequest request){
        return ApiResponse.<InventoryTransCreateResponse>builder()
                .message("Create import/export")
                .result(warehouseService.createImportExport(request))
                .build();
    }

    @GetMapping("/load-transaction/{code_warehouse}")
    ApiResponse<List<LoadInventoryTransactionResponse>> loadAllTransactionByWarehouse(@PathVariable("code_warehouse") String code_warehouse){
        return ApiResponse.<List<LoadInventoryTransactionResponse>>builder()
                .message("Load all transaction by warehouse")
                .result(warehouseService.loadInventoryTransactionResponseListByWarehouse(code_warehouse))
                .build();
    }


}
