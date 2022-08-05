package me.coconan.agileaccount;

import java.math.BigDecimal;

public class Fund {
    private String code;
    private String name;
    private BigDecimal netUnitValue;

    public Fund(String code, String name, String netUnitValue) {
        this.code = code;
        this.name = name;
        this.netUnitValue = new BigDecimal(netUnitValue);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getNetUnitValue() {
        return netUnitValue;
    }
}
