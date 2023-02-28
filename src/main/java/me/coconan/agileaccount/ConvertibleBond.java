package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConvertibleBond implements InvestmentTarget {
    private final String code;
    private final String name;

    public ConvertibleBond(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DailyRecord getLatestDailyRecord(LocalDate date) {
        // https://datacenter-web.eastmoney.com/api/data/get?sty=ALL&token=894050c76af8597a853f5b408b759f5d&st=date&sr=1&source=WEB&type=RPTA_WEB_KZZ_LS&filter=%28zcode%3D%22127080%22%29&p=1&ps=8000&_=1648629088839
        return DailyRecord.builder()
                .investmentTarget(this)
                .date(date)
                .closingPrice(new BigDecimal(100))
                .build();
    }
}
