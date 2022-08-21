package me.coconan.agileaccount;

import java.math.BigDecimal;

public class Operation {
    private Fund fund;
    private BigDecimal cost;
    private BigDecimal share;
    private BigDecimal netUnitValue;
    private BigDecimal serviceFee;
    private String platform;
    
    public Operation(Fund fund, String cost, String share, String netUnitValue, String serviceFee, String platform) {
        this.fund = fund;
        this.cost = new BigDecimal(cost);
        this.share = new BigDecimal(share);
        this.netUnitValue = new BigDecimal(netUnitValue);
        this.serviceFee = new BigDecimal(serviceFee);
        this.platform = platform;
    }

    public Fund getFund() {
        return fund;
    }

    public BigDecimal getShare() {
        return share;
    }

    public BigDecimal getNetUnitValue() {
        return netUnitValue;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public String getPlatform() {
        return platform;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
