package org.delcom.app.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, UUID> {

    // Search cashflow by keyword & userId
    @Query("SELECT t FROM CashFlow t WHERE " +
       "(LOWER(t.source) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND t.userId = :userId")
    List<CashFlow> findByKeyword(UUID userId, String keyword);

    // Get all cashflow data for a user
    @Query("SELECT t FROM CashFlow t WHERE t.userId = :userId")
    List<CashFlow> findAllByUserId(UUID userId);

    // Get single cashflow by userId & id
    @Query("SELECT t FROM CashFlow t WHERE t.id = :id AND t.userId = :userId")
    Optional<CashFlow> findByUserIdAndId(UUID userId, UUID id); 
    // Get distinct labels for a user
    @Query("SELECT DISTINCT t.label FROM CashFlow t WHERE t.userId = :userId ORDER BY t.label ASC")
    List<String> findDistinctLabelsUser(UUID userId);
}