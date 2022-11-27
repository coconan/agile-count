package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Fund {
    private final String code;
    private final String name;
    private Map<LocalDate, FundDailyRecord> fundDailyRecordMap;

    public Fund(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLatestNetUnitValueForDate(LocalDate date) {
        if (fundDailyRecordMap == null) {
            loadFundDailyRecords();
        }

        LocalDate latestDate = date.minusYears(1);
        for (LocalDate fundRecordDate : fundDailyRecordMap.keySet()) {
            if ((fundRecordDate.isBefore(date) || fundRecordDate.isEqual(date)) && fundRecordDate.isAfter(latestDate)) {
                latestDate = fundRecordDate;
            }
        }
        FundDailyRecord latestFundDailyRecord = fundDailyRecordMap.get(latestDate);

        return latestFundDailyRecord == null ? BigDecimal.ZERO : latestFundDailyRecord.getY();
    }

    private void loadFundDailyRecords() {
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

                fundDailyRecordMap = new HashMap<>();
                for (FundDailyRecord fundDailyRecord : fundDailyRecords) {
                    fundDailyRecordMap.put(Instant.ofEpochMilli(fundDailyRecord.getX()).atZone(ZoneId.systemDefault()).toLocalDate(), fundDailyRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
