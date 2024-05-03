/* CoconanBY (C)2024 */
package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

public class Operation {
    @Getter private final InvestmentTarget fund;
    @Getter private final LocalDate submittedDate;
    @Getter private final LocalDate confirmedDate;
    @Getter private final BigDecimal cost;
    @Getter private final BigDecimal share;
    @Getter private final BigDecimal netUnitValue;
    @Getter private final BigDecimal serviceFee;
    private final String platform;
    @Getter private final String tags;

    public Operation(
            InvestmentTarget fund,
            String submittedDate,
            String confirmedDate,
            String cost,
            String share,
            String netUnitValue,
            String serviceFee,
            String platform,
            String tags) {
        this.fund = fund;
        this.submittedDate = LocalDate.parse(submittedDate);
        this.confirmedDate = LocalDate.parse(confirmedDate);
        this.cost = new BigDecimal(cost);
        this.share = new BigDecimal(share);
        this.netUnitValue = new BigDecimal(netUnitValue);
        this.serviceFee = new BigDecimal(serviceFee);
        this.platform = platform;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Operation{"
                + "fund="
                + fund
                + ", submittedDate="
                + submittedDate
                + ", confirmedDate="
                + confirmedDate
                + ", cost="
                + cost
                + ", share="
                + share
                + ", netUnitValue="
                + netUnitValue
                + ", serviceFee="
                + serviceFee
                + ", platform='"
                + platform
                + '\''
                + '}';
    }
}
