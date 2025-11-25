package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class CashFlowControllerTests {
    @Test
    @DisplayName("Pengujian untuk controller CashFlow")
    void testCashFlowController() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();

        // Membuat dummy data
        CashFlow cashFlow = new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 400000, "Menerima gaji bulanan dari perusahaan.");
        cashFlow.setId(cashFlowId);

        // Membuat mock Service
        CashFlowService cashFlowService = Mockito.mock(CashFlowService.class);

        // Atur perilaku mock
        when(cashFlowService.createCashFlow(any(UUID.class), any(String.class), any(String.class), 
             any(String.class), any(Integer.class), any(String.class))).thenReturn(cashFlow);

        // Membuat instance controller
        CashFlowController cashFlowController = new CashFlowController(cashFlowService);
        assert (cashFlowController != null);

        cashFlowController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Menguji method createCashFlow
        {
            // Data tidak valid
            {
                List<CashFlow> invalidCashFlows = List.of(
                        // Type Null
                        new CashFlow(userId, null, "Source valid", "label-valid", 1000, "Description valid"),
                        // Type Kosong
                        new CashFlow(userId, "", "Source valid", "label-valid", 1000, "Description valid"),
                        // Source Null
                        new CashFlow(userId, "Type valid", null, "label-valid", 1000, "Description valid"),
                        // Source Kosong
                        new CashFlow(userId, "Type valid", "", "label-valid", 1000, "Description valid"),
                        // Label Null
                        new CashFlow(userId, "Type valid", "Source valid", null, 1000, "Description valid"),
                        // Label Kosong
                        new CashFlow(userId, "Type valid", "Source valid", "", 1000, "Description valid"),
                        // Amount Null
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", null, "Description valid"),
                        // Amount <= 0
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", 0, "Description valid"),
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", -100, "Description valid"),
                        // Description Null
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", 1000, null),
                        // Description Kosong
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", 1000, ""));

                ResponseEntity<ApiResponse<Map<String, UUID>>> result;
                for (CashFlow itemCashFlow : invalidCashFlows) {
                    result = cashFlowController.createCashFlow(itemCashFlow);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk menambahkan cash flow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.createCashFlow(cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil menambahkan cash flow
            {
                cashFlowController.authContext.setAuthUser(authUser);
                var result = cashFlowController.createCashFlow(cashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getAllCashFlows
        {
            // Tidak terautentikasi untuk getAllCashFlows
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji getAllCashFlows dengan search null
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<CashFlow> dummyResponse = List.of(cashFlow);
                when(cashFlowService.getAllCashFlows(any(UUID.class), any(String.class))).thenReturn(dummyResponse);
                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                // ✅ PERBAIKAN: Cek key yang sesuai dengan controller
                assert (result.getBody().getData().get("cashFLows") != null);
            }

            // Menguji getAllCashFlows dengan search terisi
            {
                var result = cashFlowController.getAllCashFlows("gaji");
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getCashFlowById
        {
            // Tidak terautentikasi untuk getCashFlowById
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji getCashFlowById dengan ID yang ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(cashFlow);
                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("cashFlow").getId().equals(cashFlowId));
            }

            // Menguji getCashFlowById dengan ID yang tidak ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(null);
                var result = cashFlowController.getCashFlowById(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Menguji method getCashFlowLabels
        {
            // Tidak terautentikasi untuk getCashFlowLabels
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getCashFlowLabels();
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil mendapatkan labels
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<String> dummyLabels = List.of("gaji-bulanan", "alat-mandi", "alat-elektronik");
                when(cashFlowService.getDistinctLabels(any(UUID.class))).thenReturn(dummyLabels);
                
                var result = cashFlowController.getCashFlowLabels();
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("labels").size() == 3);
            }
        }

        // Menguji method updateCashFlow
        {
            // Data tidak valid
            {
                List<CashFlow> invalidCashFlows = List.of(
                        // Type Null
                        new CashFlow(userId, null, "Source valid", "label-valid", 1000, "Description valid"),
                        // Type Kosong
                        new CashFlow(userId, "", "Source valid", "label-valid", 1000, "Description valid"),
                        // Source Null
                        new CashFlow(userId, "Type valid", null, "label-valid", 1000, "Description valid"),
                        // Source Kosong
                        new CashFlow(userId, "Type valid", "", "label-valid", 1000, "Description valid"),
                        // Label Null
                        new CashFlow(userId, "Type valid", "Source valid", null, 1000, "Description valid"),
                        // Label Kosong
                        new CashFlow(userId, "Type valid", "Source valid", "", 1000, "Description valid"),
                        // Amount Null
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", null, "Description valid"),
                        // Amount <= 0
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", 0, "Description valid"),
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", -100, "Description valid"),
                        // Description Null
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", 1000, null),
                        // Description Kosong
                        new CashFlow(userId, "Type valid", "Source valid", "label-valid", 1000, ""));

                for (CashFlow itemCashFlow : invalidCashFlows) {
                    var result = cashFlowController.updateCashFlow(cashFlowId, itemCashFlow);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk updateCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.updateCashFlow(cashFlowId, cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Memperbarui cash flow dengan ID tidak ada
            {
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), 
                     any(String.class), any(String.class), any(Integer.class), any(String.class)))
                        .thenReturn(null);
                CashFlow updatedCashFlow = new CashFlow(userId, "Outflow", "Belanja", "belanja-bulanan", 500000, "Belanja kebutuhan bulanan");
                updatedCashFlow.setId(nonexistentCashFlowId);

                var result = cashFlowController.updateCashFlow(nonexistentCashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Memperbarui cash flow dengan ID ada
            {
                CashFlow updatedCashFlow = new CashFlow(userId, "Outflow", "Belanja", "belanja-bulanan", 500000, "Belanja kebutuhan bulanan");
                updatedCashFlow.setId(cashFlowId);
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), 
                     any(String.class), any(String.class), any(Integer.class), any(String.class)))
                        .thenReturn(updatedCashFlow);

                var result = cashFlowController.updateCashFlow(cashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method deleteCashFlow
        {
            // Tidak terautentikasi untuk deleteCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji deleteCashFlow dengan ID yang tidak ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(false);
                var result = cashFlowController.deleteCashFlow(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji deleteCashFlow dengan ID yang ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(true);
                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }
    }
}