package me.coconan.agileaccount;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
                Gson gson = new Gson();
                List<FundDailyRecord> fundDailyRecords = gson.fromJson(netListJson, new TypeToken<List<FundDailyRecord>>() {}.getType());
                BigDecimal latestNetUnitValue = fundDailyRecords.get(fundDailyRecords.size()-1).getY();
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
