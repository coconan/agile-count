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

    public void setX(long x) {
        this.x = x;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }
}
