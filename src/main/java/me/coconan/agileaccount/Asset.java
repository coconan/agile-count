package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Asset {
    private final Fund fund;
    private BigDecimal share;
    private BigDecimal cost;
    private BigDecimal costPrice;
    private final String platform;
    private BigDecimal fixedEarning;
    private BigDecimal serviceFee;

    public Asset(Fund fund, String platform) {
        this.fund = fund;
        this.platform = platform;
        this.share = BigDecimal.valueOf(0);
        this.cost = BigDecimal.valueOf(0);
        this.costPrice = BigDecimal.valueOf(0);
        this.fixedEarning = BigDecimal.valueOf(0);
        this.serviceFee = BigDecimal.valueOf(0);
    }

    public void apply(Operation operation) {
        BigDecimal updatedShare = share.add(operation.getShare());
        if (operation.getShare().compareTo(BigDecimal.valueOf(0)) > 0) {
            cost = cost.add(operation.getCost());
            costPrice = cost.divide(updatedShare,  5, RoundingMode.HALF_UP);
            serviceFee = serviceFee.add(operation.getServiceFee());
        } else {
            BigDecimal earning = operation.getNetUnitValue().subtract(costPrice).multiply(operation.getShare().negate());
            fixedEarning = fixedEarning.add(earning).subtract(operation.getServiceFee());
            cost = costPrice.multiply(updatedShare);
        }
        share = updatedShare;
    }

    public Fund getFund() {
        return fund;
    }

    public BigDecimal getShare() {
        return share;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public BigDecimal getFixedEarning() {
        return fixedEarning;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Asset copy() {
        Asset newAsset = new Asset(fund, platform);
        newAsset.share = this.share;
        newAsset.cost = this.cost;
        newAsset.costPrice = this.costPrice;
        newAsset.fixedEarning = this.fixedEarning;
        newAsset.serviceFee = this.serviceFee;
        
        return newAsset;
    }

    public boolean isNullAsset() {
        return this.share.equals(BigDecimal.ZERO) &&
            this.cost.equals(BigDecimal.valueOf(0)) &&
            this.costPrice.equals(BigDecimal.valueOf(0)) &&
            this.fixedEarning.equals(BigDecimal.valueOf(0)) &&
            this.serviceFee.equals(BigDecimal.valueOf(0));
    }

    @Override
    public String toString() {
        return "Asset{" +
                "fund=" + fund +
                ", share=" + share +
                ", cost=" + cost +
                ", costPrice=" + costPrice +
                ", platform='" + platform + '\'' +
                ", fixedEarning=" + fixedEarning +
                ", serviceFee=" + serviceFee +
                '}';
    }
}
