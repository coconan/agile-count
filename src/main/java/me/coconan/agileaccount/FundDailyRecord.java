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

    public long getX() {
        return x;
    }

    public LocalDate getLocalDate() {
        return Instant.ofEpochMilli(getX()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public BigDecimal getY() {
        return y;
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
