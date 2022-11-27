package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvestmentStats {
    private BigDecimal totalCost;
    private BigDecimal totalAmount;
    private BigDecimal totalFixedEarning;

    public InvestmentStats() {
        totalCost = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        totalFixedEarning = BigDecimal.ZERO;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalFixedEarning(BigDecimal totalFixedEarning) {
        this.totalFixedEarning = totalFixedEarning;
    }

    public BigDecimal getTotalFixedEarning() {
        return totalFixedEarning;
    }

    public BigDecimal getTotalEarning() {
        return totalAmount.subtract(totalCost).setScale(2, RoundingMode.HALF_DOWN);
    }

    public BigDecimal getEarningRate() {
        if (totalCost.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return getTotalEarning().divide(totalCost, 5, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
    }
}
