package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/cash-flows")
public class CashFlowController {
    private final CashFlowService cashFlowService;

    @Autowired
    protected AuthContext authContext;

    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createCashFlow(@RequestBody CashFlow cashFlow) {
        // Validasi input
        if (cashFlow.getType() == null || cashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tipe tidak valid", null));
        } else if (cashFlow.getSource() == null || cashFlow.getSource().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        } else if (cashFlow.getLabel() == null || cashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        } else if (cashFlow.getAmount() == null || cashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        } else if (cashFlow.getDescription() == null || cashFlow.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow newCashFlow = cashFlowService.createCashFlow(
                authUser.getId(),
                cashFlow.getType(),
                cashFlow.getSource(),
                cashFlow.getLabel(),
                cashFlow.getAmount(),
                cashFlow.getDescription());
        return ResponseEntity.ok(new ApiResponse<Map<String, UUID>>(
                "success",
                "Cash flow berhasil dibuat",
                Map.of("id", newCashFlow.getId())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<CashFlow>>>> getAllCashFlows(
            @RequestParam(required = false) String search) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Daftar cash flow berhasil diambil",
                Map.of("cashFLows", cashFlows)));
    }

    // Mendapatkan CashFlow berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, CashFlow>>> getCashFlowById(@PathVariable UUID id) {

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow cashFlow = cashFlowService.getCashFlowById(id, authUser.getId());
        if (cashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(
                    "fail",
                    "Data cash flow tidak ditemukan",
                    null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data cash flow berhasil diambil",
                Map.of("cashFlow", cashFlow)));
    }

    // Mendapatkan semua labels CashFlow
    // -------------------------------
    @GetMapping("/labels")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getCashFlowLabels() {

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<String> labels = cashFlowService.getDistinctLabels(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Daftar label cash flow berhasil diambil",
                Map.of("labels", labels)));
    }

    // Memperbarui cash flow berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CashFlow>> updateCashFlow(@PathVariable UUID id, @RequestBody CashFlow cashFlow) {
        if (cashFlow.getType() == null || cashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tipe tidak valid", null));
        } else if (cashFlow.getSource() == null || cashFlow.getSource().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data source tidak valid", null));
        } else if (cashFlow.getLabel() == null || cashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data label tidak valid", null));
        } else if (cashFlow.getAmount() == null || cashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data jumlah tidak valid", null));
        } else if (cashFlow.getDescription() == null || cashFlow.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow updatedCashFlow = cashFlowService.updateCashFlow(id, authUser.getId(), cashFlow.getType(),
                cashFlow.getSource(),
                cashFlow.getLabel(), cashFlow.getAmount(), cashFlow.getDescription());
        if (updatedCashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Data cash flow berhasil diperbarui", null));
    }

    // Menghapus cash flow berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCashFlow(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = cashFlowService.deleteCashFlow(id, authUser.getId());
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>(
                    "fail",
                    "Data cash flow tidak ditemukan",
                    null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data cash flow berhasil dihapus",
                null));
    }

}
