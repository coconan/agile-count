package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Asset {
    private Fund fund;
    private BigDecimal share;
    private BigDecimal costPrice;
    private String platform;
    private BigDecimal fixedEarning;
    private BigDecimal serviceFee;

    public Asset(Fund fund, String platform) {
        this.fund = fund;
        this.platform = platform;
        this.share = BigDecimal.valueOf(0);
        this.costPrice = BigDecimal.valueOf(0);
        this.fixedEarning = BigDecimal.valueOf(0);
        this.serviceFee = BigDecimal.valueOf(0);
    }

    public void apply(Operation operation) {
        BigDecimal updatedShare = share.add(operation.getShare());
        serviceFee = serviceFee.add(operation.getServiceFee());
        if (operation.getShare().compareTo(BigDecimal.valueOf(0)) > 0) {
            costPrice = costPrice.multiply(share)
                .add(operation.getNetUnitValue().multiply(operation.getShare()))
                .divide(updatedShare,  5, RoundingMode.HALF_UP);
        } else {
            BigDecimal earning = operation.getNetUnitValue().subtract(costPrice).multiply(operation.getShare());
            fixedEarning = fixedEarning.add(earning);
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

    public String getPlatform() {
        return platform;
    }

    public BigDecimal getFixedEarning() {
        return fixedEarning;
    }

    public BigDecimal serviceFee() {
        return serviceFee;
    }

    public BigDecimal getAmount() {
        return getShare().multiply(fund.getNetUnitValue());
    }

    public BigDecimal getCost() {
        return share.multiply(costPrice).add(serviceFee);
    }
}
