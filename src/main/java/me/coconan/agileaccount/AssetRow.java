package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class AssetRow {
    private String code;
    private String name;
    private BigDecimal netPrice;
    private LocalDate netPriceLocalDate;

    private BigDecimal cost;
    private BigDecimal amount;
    private BigDecimal earning;
    private BigDecimal earningRate;
    private BigDecimal costPrice;
    private BigDecimal share;
    private BigDecimal fixedEarning;
    private BigDecimal accumEarning;
    private BigDecimal serviceFee;
    private BigDecimal weight;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public LocalDate getNetPriceLocalDate() {
        return netPriceLocalDate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getEarning() {
        return earning;
    }

    public BigDecimal getEarningRate() {
        return earningRate;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public BigDecimal getShare() {
        return share;
    }

    public BigDecimal getFixedEarning() {
        return fixedEarning;
    }

    public BigDecimal getAccumEarning() {
        return accumEarning;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public static AssetRow build(Asset asset, InvestmentStats investmentStats, LocalDate date) {
        AssetRow assetRow = new AssetRow();
        assetRow.code = asset.getFund().getCode();
        assetRow.name = asset.getFund().getName();
        assetRow.netPrice = asset.getFund().getLatestDailyRecord(date).getClosingPrice()
                .setScale(4, RoundingMode.HALF_DOWN);
        assetRow.netPriceLocalDate = asset.getFund().getLatestDailyRecord(date).getDate();
        assetRow.cost = asset.getCost().setScale(2, RoundingMode.HALF_DOWN);
        assetRow.amount = asset.getShare().multiply(asset.getFund().getLatestDailyRecord(date).getClosingPrice())
                .setScale(2, RoundingMode.HALF_DOWN);
        assetRow.earning = assetRow.amount.subtract(assetRow.cost).setScale(2, RoundingMode.HALF_DOWN);
        assetRow.earningRate = assetRow.cost.compareTo(BigDecimal.valueOf(0)) == 0
            ? BigDecimal.valueOf(0)
            : assetRow.earning.divide(assetRow.cost, 5, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
        assetRow.costPrice = asset.getCostPrice().setScale(4, RoundingMode.HALF_UP);
        assetRow.share = asset.getShare().setScale(2, RoundingMode.HALF_DOWN);
        assetRow.fixedEarning = asset.getFixedEarning().setScale(2, RoundingMode.HALF_DOWN);
        assetRow.accumEarning = asset.getFixedEarning().add(assetRow.amount.subtract(assetRow.cost)).setScale(2, RoundingMode.HALF_DOWN);
        assetRow.serviceFee = asset.getServiceFee().setScale(2, RoundingMode.HALF_DOWN);
        assetRow.weight = investmentStats.getTotalCost().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                asset.getCost().divide(investmentStats.getTotalCost(), 5, RoundingMode.HALF_DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
    
        return assetRow;
    }    
}
