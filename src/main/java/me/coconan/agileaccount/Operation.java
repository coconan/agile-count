package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Operation {
    private final InvestmentTarget fund;
    private final LocalDate submittedDate;
    private final LocalDate confirmedDate;
    private final BigDecimal cost;
    private final BigDecimal share;
    private final BigDecimal netUnitValue;
    private final BigDecimal serviceFee;
    private final String platform;
    
    public Operation(InvestmentTarget fund, String submittedDate, String confirmedDate, String cost, String share, String netUnitValue, String serviceFee, String platform) {
        this.fund = fund;
        this.submittedDate = LocalDate.parse(submittedDate);
        this.confirmedDate = LocalDate.parse(confirmedDate);
        this.cost = new BigDecimal(cost);
        this.share = new BigDecimal(share);
        this.netUnitValue = new BigDecimal(netUnitValue);
        this.serviceFee = new BigDecimal(serviceFee);
        this.platform = platform;
    }

    public InvestmentTarget getFund() {
        return fund;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
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

    public BigDecimal getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "fund=" + fund +
                ", submittedDate=" + submittedDate +
                ", confirmedDate=" + confirmedDate +
                ", cost=" + cost +
                ", share=" + share +
                ", netUnitValue=" + netUnitValue +
                ", serviceFee=" + serviceFee +
                ", platform='" + platform + '\'' +
                '}';
    }
}
