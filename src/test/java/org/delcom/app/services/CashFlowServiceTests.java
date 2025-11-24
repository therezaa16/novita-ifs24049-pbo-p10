package org.delcom.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CashFlowServiceTests {
    @Test
    @DisplayName("Pengujian untuk service CashFlow")
    void testCashFlowService() throws Exception {
        // Fake Cash Flow
        UUID fakeUserId = UUID.randomUUID();
        UUID fakeCashFlowId = UUID.randomUUID();

        // Fake cash flow
        CashFlow cashFlow = new CashFlow();
        cashFlow.setId(fakeCashFlowId);
        cashFlow.setUserId(fakeUserId);
        cashFlow.setSource("Test Source");
        cashFlow.setType("INCOME");
        cashFlow.setLabel("Test Label");
        cashFlow.setDescription("Test description");
        cashFlow.setAmount(1000);

        // Fake list cash flow
        List<CashFlow> cashFlowList = new ArrayList<>();
        cashFlowList.add(cashFlow);

        // Fake labels
        List<String> fakeLabels = new ArrayList<>();
        fakeLabels.add("Label 1");
        fakeLabels.add("Label 2");

        // Mock
        CashFlowRepository cashFlowRepository = Mockito.mock(CashFlowRepository.class);

        // Atur perilaku mock
        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);
        when(cashFlowRepository.findByKeyword(fakeUserId, "test"))
                .thenReturn(cashFlowList);
        when(cashFlowRepository.findAllByUserId(fakeUserId))
                .thenReturn(cashFlowList);

        when(cashFlowRepository.findByUserIdAndId(fakeUserId, fakeCashFlowId))
    .thenReturn(java.util.Optional.of(cashFlow));
        when(cashFlowRepository.findDistinctLabelsUser(fakeUserId))
                .thenReturn(fakeLabels);
        doNothing().when(cashFlowRepository).delete(cashFlow);

        // Intance service
        CashFlowService cashFlowService = new CashFlowService(cashFlowRepository);

        // Menguji method createCashFlow
        {
            CashFlow createdCashFlow = cashFlowService.createCashFlow(fakeUserId, "INCOME", "Test Source",
                    "Test Label", 1000, "Test description");
            assert createdCashFlow != null;
            assert createdCashFlow.getId().equals(fakeCashFlowId);
        }

        // Menguji method getAllCashFlows dengan search null
        {
            List<CashFlow> retrievedCashFlows = cashFlowService.getAllCashFlows(fakeUserId, null);
            assert retrievedCashFlows.size() == 1;
            assert retrievedCashFlows.get(0).getId().equals(fakeCashFlowId);
        }

        // Menguji method getAllCashFlows dengan search kosong
        {
            List<CashFlow> retrievedCashFlows = cashFlowService.getAllCashFlows(fakeUserId, "");
            assert retrievedCashFlows.size() == 1;
            assert retrievedCashFlows.get(0).getId().equals(fakeCashFlowId);
        }

        // Menguji method getAllCashFlows dengan search terisis
        {
            List<CashFlow> retrievedCashFlows = cashFlowService.getAllCashFlows(fakeUserId, "test");
            assert retrievedCashFlows.size() == 1;
            assert retrievedCashFlows.get(0).getId().equals(fakeCashFlowId);
        }

        // Menguji method getCashFlowById
        {
            CashFlow retrievedCashFlow = cashFlowService.getCashFlowById(fakeCashFlowId, fakeUserId);
            assert retrievedCashFlow != null;
            assert retrievedCashFlow.getId().equals(fakeCashFlowId);
        }

        // Menguji method getDistinctLabels
        {
            List<String> retrievedLabels = cashFlowService.getDistinctLabels(fakeUserId);
            assert retrievedLabels.size() == 2;
            assert retrievedLabels.contains("Label 1");
            assert retrievedLabels.contains("Label 2");
        }

        // Menguji method updateCashFlow
        {
            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(fakeCashFlowId, fakeUserId, "EXPENSE",
                    "Updated Source", "Updated Label", 500, "Updated description");
            assert updatedCashFlow != null;
            assert updatedCashFlow.getType().equals("EXPENSE");
            assert updatedCashFlow.getSource().equals("Updated Source");
            assert updatedCashFlow.getLabel().equals("Updated Label");
            assert updatedCashFlow.getAmount() == 500;
            assert updatedCashFlow.getDescription().equals("Updated description");
        }

        // Menguji method updateCashFlow dengan id tidak ditemukan
        {
            UUID nonExistentId = UUID.randomUUID();
            when(cashFlowRepository.findByUserIdAndId(nonExistentId, fakeUserId))
                    .thenReturn(java.util.Optional.ofNullable(null));
            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(nonExistentId, fakeUserId, "EXPENSE",
                    "Updated Source", "Updated Label", 500, "Updated description");
            assert updatedCashFlow == null;
        }

        // Menguji method deleteCashFlow
        {
            boolean isDeleted = cashFlowService.deleteCashFlow(fakeCashFlowId, fakeUserId);
            assert isDeleted;
        }

        // Menguji method deleteCashFlow dengan id tidak ditemukan
        {
            UUID nonExistentId = UUID.randomUUID();
            when(cashFlowRepository.findByUserIdAndId(nonExistentId, fakeUserId))
                    .thenReturn(java.util.Optional.ofNullable(null));
            boolean isDeleted = cashFlowService.deleteCashFlow(nonExistentId, fakeUserId);
            assert !isDeleted;
        }
    }
}