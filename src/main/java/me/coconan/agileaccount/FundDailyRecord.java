package me.coconan.agileaccount;

import java.math.BigDecimal;

public class FundDailyRecord {
    private long x;
    private BigDecimal y;
    private BigDecimal equityReturn;
    private String unitMoney;

    public long getX() {
        return x;
    }

    public BigDecimal getY() {
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
