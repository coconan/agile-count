package me.coconan.agileaccount;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FundStore {
    Map<String, Fund> fundMap = new HashMap<>();

    public void put(String code, Fund fund) {
        try {
            URL fundURL = new URL(String.format("https://fund.eastmoney.com/pingzhongdata/%s.js?v=%d", code, System.currentTimeMillis()));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(fundURL.openStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                String netListJson = response.toString().split("Data_netWorthTrend = ")[1].split(";/\\*累计净值走势\\*/")[0];
                String[] netListItemJsonArray = netListJson.split("\\}\\,\\{");
                BigDecimal latestNetUnitValue = new BigDecimal(netListItemJsonArray[netListItemJsonArray.length-1].split("\\,")[1].split("\\:")[1]);
                fund.setNetUnitValue(latestNetUnitValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fundMap.put(code, fund);
    }

    public Fund get(String code) {
        return fundMap.get(code);
    }
}
