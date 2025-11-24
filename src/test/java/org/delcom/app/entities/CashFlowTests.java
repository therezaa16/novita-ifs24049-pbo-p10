package org.delcom.app.entities;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CashFlowTests {

    @Test
    @DisplayName("Membuat instance dari kelas CashFlow")
    void testMembuatInstanceCashFlow() throws Exception {

        // 1. Instance Dengan Constructor Kosong
        {
            // Act
            CashFlow cashFlow = new CashFlow();

            // Assert
            assert (cashFlow.getId() == null);
            assert (cashFlow.getUserId()== null);
            assert (cashFlow.getType()== null);
            assert (cashFlow.getSource()== null);
            assert (cashFlow.getLabel()== null);
            assert (cashFlow.getAmount()== null);
            assert (cashFlow.getDescription()== null);

            assert (cashFlow.getCreatedAt()== null);
            assert (cashFlow.getUpdatedAt()== null);
        }

        // 2. Instance Dengan Constructor Parameter
        {
            UUID userId = UUID.randomUUID();
            String type = "income";
            String source = "salary";
            String label = "monthly salary";
            Integer amount = 5000;
            String description = "Salary for June";

            // Act
            CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);

            // Assert
            assert (cashFlow.getId() == null);
            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals(type));
            assert (cashFlow.getSource().equals(source));
            assert (cashFlow.getLabel().equals(label));
            assert (cashFlow.getAmount().equals(amount));
            assert (cashFlow.getDescription().equals(description));
            assert (cashFlow.getCreatedAt() == null);
            assert (cashFlow.getUpdatedAt() == null);

        }

        // 3. Instance Dengan Setter

        {
            UUID id = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            String type = "expense";
            String source = "groceries";
            String label = "weekly";
            Integer amount = 150;
            String description = "Grocery Shopping";

            // Act
            CashFlow cashFlow = new CashFlow();
            cashFlow.setId(id);
            cashFlow.setUserId(userId);
            cashFlow.setType(type);
            cashFlow.setSource(source);
            cashFlow.setLabel(label);
            cashFlow.setAmount(amount);
            cashFlow.setDescription(description);

            cashFlow.onCreate();
            cashFlow.onUpdate();

            // Assert
            assert (cashFlow.getId() .equals(id));
            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals(type));
            assert (cashFlow.getSource().equals(source));
            assert (cashFlow.getLabel().equals(label));
            assert (cashFlow.getAmount().equals(amount));
            assert (cashFlow.getDescription().equals(description));


            assertNotNull(cashFlow.getCreatedAt());
            assertNotNull(cashFlow.getUpdatedAt());
        }
        

    }
}
    