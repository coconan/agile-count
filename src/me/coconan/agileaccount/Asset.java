package me.coconan.agileaccount;

import java.math.BigDecimal;

public class Asset {
    private Fund fund;
    private BigDecimal share;
    private BigDecimal costPrice;
    private String platform;

    public Asset(Fund fund, String share, String costPrice, String platform) {
        this.fund = fund;
        this.share = new BigDecimal(share);
        this.costPrice = new BigDecimal(costPrice);
        this.platform = platform;
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

    public BigDecimal getAmount() {
        return getShare().multiply(fund.getNetUnitValue());
    }

    public BigDecimal getCost() {
        return share.multiply(costPrice);
    }
}
