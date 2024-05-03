/* CoconanBY (C)2024 */
package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvestmentStats {
    private BigDecimal totalCost;
    private BigDecimal totalAmount;
    private BigDecimal totalFixedEarning;
    private BigDecimal totalServiceFee;

    public InvestmentStats() {
        totalCost = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        totalFixedEarning = BigDecimal.ZERO;
    }

    public BigDecimal getTotalEarning() {
        return totalAmount.subtract(totalCost);
    }

    public BigDecimal getTotalAccumEarning() {
        return getTotalEarning().add(getTotalFixedEarning());
    }

    public BigDecimal getEarningRate() {
        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return getTotalEarning()
                .divide(totalCost, 5, RoundingMode.HALF_DOWN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_DOWN);
    }
}
