package me.coconan.agileaccount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Application {
    
    public static void main(String[] args) {
        Path assetsPath = Paths.get(args[0]);
        Path operationsDirPath = Paths.get(args[1]);
        Path fundsPath = Paths.get(args[2]);
        try (FileReader assetsFileReader = new FileReader(assetsPath.toFile());
            FileReader fundsFileReader = new FileReader(fundsPath.toFile())) {
            FundStore fundStore = new FundStore();
            try (BufferedReader reader = new BufferedReader(fundsFileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length != 3) {
                        continue;
                    }
                    fundStore.put(fields[0].trim(), new Fund(fields[0].trim(), fields[2], fields[1].trim()));
                }
            }

            Account account = new Account();
            for (File operationsFile : operationsDirPath.toFile().listFiles()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(operationsFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split("\\s+");
                        if (fields.length != 8) {
                            continue;
                        }
                        if (fundStore.get(fields[0].trim()) == null) {
                            continue;
                        }
                        account.addOperation(new Operation(fundStore.get(fields[0].trim()), fields[1].trim(), fields[2].trim(),
                            fields[3].trim(), fields[5].trim(), fields[4].trim(), fields[6].trim(), fields[7].trim()));
                    }
                }
            }

            if ("asset".equals(args[3])) {
                LocalDate date = LocalDate.now();
                if (args.length == 5 && args[4] != null) {
                    date = LocalDate.parse(args[4]);
                }
                System.out.printf("%6s %10s %10s %10s %16s%% %16s %10s %10s %10s %s\n",
                    "code", "cost", "amount", "earning", "earning rate", "fixed earning", "net price", "cost price", "share", "name");
                List<Asset> assets = account.getAssets(date);
                for (Asset asset : assets) {
                    if (asset.isNullAsset()) {
                        continue;
                    }
                    String code = asset.getFund().getCode();
                    String name = asset.getFund().getName();
                    BigDecimal netPrice = asset.getFund().getNetUnitValue().setScale(4, RoundingMode.HALF_DOWN);
                    BigDecimal cost = asset.getCost().setScale(2, RoundingMode.HALF_DOWN);
                    BigDecimal amount = asset.getShare().multiply(asset.getFund().getLatestNetUnitValueForDate(date))
                            .setScale(2, RoundingMode.HALF_DOWN);
                    BigDecimal earning = amount.subtract(cost).setScale(2, RoundingMode.HALF_DOWN);
                    BigDecimal earningRate = cost.compareTo(BigDecimal.valueOf(0)) == 0
                        ? BigDecimal.valueOf(0)
                        : earning.divide(cost, 5, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
                    BigDecimal costPrice = asset.getCostPrice().setScale(4, RoundingMode.HALF_UP);
                    BigDecimal share = asset.getShare().setScale(2, RoundingMode.HALF_DOWN);
                    BigDecimal fixedEarning = asset.getFixedEarning().setScale(2, RoundingMode.HALF_DOWN);
                    System.out.printf("%6s %10s %10s %10s %16s%% %16s %10s %10s %10s %s\n",
                        code, cost, amount, earning, earningRate, fixedEarning, netPrice, costPrice, share, name);
                }

                InvestmentStats investmentStats = account.getInvestmentStats(assets, date);
                System.out.printf("%6s %10s %10s %10s %16s%% %16s %s\n",
                    " ",
                    investmentStats.getTotalCost().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                    " ");
            } else if ("chart".equals(args[3])) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Map<LocalDate, InvestmentStats> investmentStatsByDate = account.getInvestmentStatsByDate();
                String step = "day";
                if (args.length == 5 && args[4] != null) {
                    step = args[4];
                }
                for (LocalDate date = account.getStartedDate();
                    date.isBefore(changeStep(LocalDate.now(), step, 1));
                    date = changeStep(date, step, 1)) {
                    InvestmentStats investmentStatsLastDate = investmentStatsByDate.get(changeStep(date, step , -1));
                    if (investmentStatsLastDate == null) {
                        investmentStatsLastDate = new InvestmentStats();
                    }
                    InvestmentStats investmentStatsThisDate = investmentStatsByDate.get(date);
                    int count = 1;
                    while (investmentStatsThisDate == null) {
                        investmentStatsThisDate = investmentStatsByDate.get(date.minusDays(count));
                        count += 1;
                    }
                    BigDecimal accumulatedInvestmentThisDate = investmentStatsThisDate.getTotalCost() == null
                            ? BigDecimal.ZERO : investmentStatsThisDate.getTotalCost();
                    BigDecimal accumulatedInvestmentLastDate = investmentStatsLastDate.getTotalCost() == null
                            ? BigDecimal.ZERO : investmentStatsLastDate.getTotalCost();
                    BigDecimal investmentThisDate = accumulatedInvestmentThisDate.subtract(accumulatedInvestmentLastDate);
                    System.out.printf("%s %16s %16s %16s %16s %16s%% %16s\n",
                        dtf.format(date),
                        investmentThisDate.setScale(2, RoundingMode.HALF_DOWN),
                        accumulatedInvestmentThisDate.setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN)
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LocalDate changeStep(LocalDate date, String step, int count) {
        switch (step) {
            case "day":
                return date.plusDays(count);
            case "week":
                return date.plusWeeks(count);
            case "month":
                return date.plusMonths(count);
            case "year":
                return date.plusYears(count);
            default:
                throw new RuntimeException("unsupported step");
        }
    }
}
