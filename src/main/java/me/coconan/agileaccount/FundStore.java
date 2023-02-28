package me.coconan.agileaccount;

import java.util.HashMap;
import java.util.Map;

public class FundStore {
    Map<String, InvestmentTarget> investmentTargetMap = new HashMap<>();

    public void put(String code, InvestmentTarget target) {
        investmentTargetMap.put(code, target);
    }

    public InvestmentTarget get(String code) {
        return investmentTargetMap.get(code);
    }
}
