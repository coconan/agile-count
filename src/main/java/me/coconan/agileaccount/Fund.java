package me.coconan.agileaccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    public FundDailyRecord getLatestFundDailyRecord(LocalDate date) {
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

        return Objects.requireNonNullElseGet(latestFundDailyRecord, FundDailyRecord::new);
    }

    private void loadFundDailyRecords() {
        try {
            downloadFundDailyRecords();

            try {
                String netListJson = null;
                while (true) {
                    Path path = Paths.get(String.format(".fundStore/%s.json", code));
                    if (path.toFile().exists()) {    
                        netListJson = Files.readString(path);
                        if (netListJson != null && !netListJson.isEmpty()) {
                            break;
                        }
                    }

                    Thread.sleep(1000);
                }
                
                Gson gson = new Gson();
                List<FundDailyRecord> fundDailyRecords = gson.fromJson(netListJson, new TypeToken<List<FundDailyRecord>>() {}.getType());

                fundDailyRecordMap = new HashMap<>();
                for (FundDailyRecord fundDailyRecord : fundDailyRecords) {
                    fundDailyRecordMap.put(fundDailyRecord.getLocalDate(), fundDailyRecord);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFundDailyRecords() {
        Executor.networkIO().submit(() -> {
            try {
                URL fundURL = new URL(String.format("https://fund.eastmoney.com/pingzhongdata/%s.js?v=%d", code, System.currentTimeMillis()));
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(fundURL.openStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    String netListJson = response.toString().split("Data_netWorthTrend = ")[1].split(";/\\*累计净值走势\\*/")[0];
                    Path path = Paths.get(".fundStore/");
                    if (!path.toFile().exists()) {
                        Files.createDirectories(path);
                    }
                    try {
                        Files.writeString(Paths.get(String.format(".fundStore/%s.json", code)), netListJson);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
