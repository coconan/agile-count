package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class FundDailyRecord {
    private long x;
    private BigDecimal y;
    private BigDecimal equityReturn;
    private String unitMoney;

    public LocalDate getLocalDate() {
        return Instant.ofEpochMilli(x).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public BigDecimal getNetUnitValue() {
        if (y == null) {
            return BigDecimal.ZERO;
        }

        return y;
    }

    @Override
    public String toString() {
        return "FundDailyRecord{" +
                "x=" + x +
                ", y=" + y +
                ", equityReturn=" + equityReturn +
                ", unitMoney='" + unitMoney + '\'' +
                '}';
    }
}
