package org.delcom.app.services;

import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CashFlowService {

    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional
    public CashFlow createCashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);
        return cashFlowRepository.save(cashFlow);
    }

    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search == null || search.trim().isEmpty()) {
            return cashFlowRepository.findAllByUserId(userId);
        } else {
            return cashFlowRepository.findByKeyword(userId, search);
        }
    }

    public CashFlow getCashFlowById(UUID id, UUID userId) {
        return cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
    }

    public List<String> getDistinctLabels(UUID userId) {
        return cashFlowRepository.findDistinctLabelsUser(userId);
    }

    @Transactional
    public CashFlow updateCashFlow(UUID id, UUID userId, String type, String source, String label, Integer amount, String description) { // ✅ PERBAIKAN: Parameter urutan id dulu, baru userId
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow != null) {
            cashFlow.setType(type);
            cashFlow.setSource(source);
            cashFlow.setLabel(label);
            cashFlow.setAmount(amount);
            cashFlow.setDescription(description);
            return cashFlowRepository.save(cashFlow);
        }
        return null;
    }

    @Transactional
    public boolean deleteCashFlow(UUID id, UUID userId) {
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow != null) {
            cashFlowRepository.delete(cashFlow);
            return true;
        }
        return false;
    }
}