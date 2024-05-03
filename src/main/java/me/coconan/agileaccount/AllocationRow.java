/* CoconanBY (C)2024 */
package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class AllocationRow {
    private final Allocation allocation;
    private BigDecimal cost;
    private BigDecimal amount;
    private BigDecimal fixedEarning;
    private BigDecimal earning;
    private BigDecimal accumulatedEarning;
    private BigDecimal serviceFee;
    private BigDecimal earningRate;
    private BigDecimal weight;
    private BigDecimal hold;

    public AllocationRow(Allocation allocation) {
        this.allocation = allocation;
        this.cost = BigDecimal.ZERO;
        this.amount = BigDecimal.ZERO;
        this.fixedEarning = BigDecimal.ZERO;
        this.earning = BigDecimal.ZERO;
        this.accumulatedEarning = BigDecimal.ZERO;
        this.serviceFee = BigDecimal.ZERO;
        this.weight = BigDecimal.ZERO;
        this.hold = BigDecimal.ZERO;
    }

    public static AllocationRow build(
            Allocation allocation,
            List<Asset> assetList,
            InvestmentStats investmentStats,
            LocalDate date) {
        AllocationRow allocationRow = new AllocationRow(allocation);
        for (Asset asset : assetList) {
            allocationRow.cost =
                    allocationRow.cost.add(asset.getCost()).setScale(2, RoundingMode.HALF_DOWN);
            allocationRow.amount =
                    allocationRow
                            .amount
                            .add(
                                    asset.getShare()
                                            .multiply(
                                                    asset.getInvestmentTarget()
                                                            .getLatestDailyRecord(date)
                                                            .getClosingPrice()))
                            .setScale(2, RoundingMode.HALF_DOWN);
            allocationRow.fixedEarning =
                    allocationRow
                            .fixedEarning
                            .add(asset.getFixedEarning())
                            .setScale(2, RoundingMode.HALF_DOWN);
            allocationRow.serviceFee =
                    allocationRow
                            .serviceFee
                            .add(asset.getServiceFee())
                            .setScale(2, RoundingMode.HALF_DOWN);
        }
        allocationRow.earning =
                allocationRow
                        .amount
                        .subtract(allocationRow.cost)
                        .setScale(2, RoundingMode.HALF_DOWN);
        allocationRow.accumulatedEarning =
                allocationRow
                        .earning
                        .add(allocationRow.fixedEarning)
                        .setScale(2, RoundingMode.HALF_DOWN);
        allocationRow.earningRate =
                (allocationRow.cost.compareTo(BigDecimal.ZERO) == 0
                                ? BigDecimal.ZERO
                                : allocationRow.earning.divide(
                                        allocationRow.cost, 5, RoundingMode.HALF_DOWN))
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_DOWN);
        allocationRow.weight =
                (investmentStats.getTotalCost().compareTo(BigDecimal.ZERO) == 0
                                ? BigDecimal.ZERO
                                : allocationRow
                                        .getCost()
                                        .divide(
                                                investmentStats.getTotalCost(),
                                                5,
                                                RoundingMode.HALF_DOWN))
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_DOWN);
        allocationRow.hold =
                (investmentStats.getTotalAmount().compareTo(BigDecimal.ZERO) == 0
                                ? BigDecimal.ZERO
                                : allocationRow.amount.divide(
                                        investmentStats.getTotalAmount(),
                                        5,
                                        RoundingMode.HALF_DOWN))
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_DOWN);

        return allocationRow;
    }
}
