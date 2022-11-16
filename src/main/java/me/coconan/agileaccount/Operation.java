package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Operation {
    private Fund fund;
    private LocalDate submittedDate;
    private LocalDate confirmedDate;
    private BigDecimal cost;
    private BigDecimal share;
    private BigDecimal netUnitValue;
    private BigDecimal serviceFee;
    private String platform;
    
    public Operation(Fund fund, String submittedDate, String confirmedDate, String cost, String share, String netUnitValue, String serviceFee, String platform) {
        this.fund = fund;
        this.submittedDate = LocalDate.parse(submittedDate);
        this.confirmedDate = LocalDate.parse(confirmedDate);
        this.cost = new BigDecimal(cost);
        this.share = new BigDecimal(share);
        this.netUnitValue = new BigDecimal(netUnitValue);
        this.serviceFee = new BigDecimal(serviceFee);
        this.platform = platform;
    }

    public Fund getFund() {
        return fund;
    }

    public LocalDate getSubmittedDate() {
        return this.submittedDate;
    }

    public LocalDate getConfirmedDate() {
        return this.confirmedDate;
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
