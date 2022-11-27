package me.coconan.agileaccount;

import java.util.HashMap;
import java.util.Map;

public class FundStore {
    Map<String, Fund> fundMap = new HashMap<>();

    public void put(String code, Fund fund) {
        fundMap.put(code, fund);
    }

    public Fund get(String code) {
        return fundMap.get(code);
    }
}
