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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Application {
    
    public static void main(String[] args) {
        Path operationsDirPath = Paths.get(args[1]);
        Path fundsPath = Paths.get(args[2]);
        try (FileReader fundsFileReader = new FileReader(fundsPath.toFile())) {
            FundStore fundStore = new FundStore();
            try (BufferedReader reader = new BufferedReader(fundsFileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length != 3) {
                        continue;
                    }
                    fundStore.put(fields[0].trim(), new Fund(fields[0].trim(), fields[2]));
                }
            }

            Account account = new Account();
            for (File operationsFile : Objects.requireNonNull(operationsDirPath.toFile().listFiles())) {
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
                System.out.printf("%6s %10s %10s %10s %16s%% %16s %16s %10s%% %10s %10s %10s %s\n",
                    "code", "cost", "amount", "earning", "earning rate", "fixed earning", "service fee", "weight", "net price", "cost price", "share", "name");
                List<Asset> assets = account.getAssets(date);
                InvestmentStats investmentStats = account.getInvestmentStats(assets, date);
                List<AssetRow> assetRows = new ArrayList<>();
                for (Asset asset : assets) {
                    if (asset.isNullAsset()) {
                        continue;
                    }
                    AssetRow assetRow = AssetRow.build(asset, investmentStats, date);
                    assetRows.add(assetRow);
                }
                Collections.sort(assetRows, new Comparator<AssetRow>() {
                    @Override
                    public int compare(AssetRow row1, AssetRow row2) {
                        return row2.getCost().compareTo(row1.getCost());
                    }
                });
                for (AssetRow assetRow : assetRows) {
                    System.out.printf("%6s %10s %10s %10s %16s%% %16s %16s %10s%% %10s %10s %10s %s\n",
                        assetRow.getCode(), assetRow.getCost(), assetRow.getAmount(), assetRow.getEarning(),
                        assetRow.getEarningRate(), assetRow.getFixedEarning(), assetRow.getServiceFee(), assetRow.getWeight(),
                        assetRow.getNetPrice(), assetRow.getCostPrice(), assetRow.getShare(), assetRow.getName());
                }


                System.out.printf("%6s %10s %10s %10s %16s%% %16s %16s %10s %10s %10s %10s %s\n",
                    " ",
                    investmentStats.getTotalCost().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalServiceFee().setScale(2, RoundingMode.HALF_DOWN),
                    "", "", "", "", " ");
            } else if ("chart".equals(args[3])) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Map<LocalDate, InvestmentStats> investmentStatsByDate = account.getInvestmentStatsByDate();
                String step = "day";
                if (args.length >= 5 && args[4] != null) {
                    step = args[4];
                }
                LocalDate start = account.getStartedDate();
                LocalDate end = LocalDate.now();
                if (args.length == 7 && args[5] != null && args[6] != null) {
                    start = LocalDate.parse(args[5]);
                    end = LocalDate.parse(args[6]);
                }
                System.out.printf("%-10s %16s %16s %16s %16s %16s %16s%% %24s %16s\n",
                        "date", "delta cost", "total cost", "net in/out", "total amount", "total earning", "earning rate", "total fixed earning", "daily earning");
                for (LocalDate date = start;
                    date.isBefore(changeStep(end, step, 1));
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

                    BigDecimal dailyEarnings = investmentStatsByDate.get(date.minusDays(1)) == null
                            ? BigDecimal.ZERO
                            : investmentStatsThisDate.getTotalEarning()
                            .subtract(investmentStatsByDate.get(date.minusDays(1)).getTotalEarning());
                    BigDecimal deltaFixedEarnings = investmentStatsThisDate.getTotalFixedEarning()
                            .subtract(investmentStatsLastDate.getTotalFixedEarning() == null ? BigDecimal.ZERO : investmentStatsLastDate.getTotalFixedEarning());
                    System.out.printf("%10s %16s %16s %16s %16s %16s %16s%% %24s %16s\n",
                        dtf.format(date),
                        investmentThisDate.setScale(2, RoundingMode.HALF_DOWN),
                        accumulatedInvestmentThisDate.setScale(2, RoundingMode.HALF_DOWN),
                        investmentThisDate.subtract(deltaFixedEarnings).setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                        investmentStatsThisDate.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                        dailyEarnings.setScale(2, RoundingMode.HALF_DOWN)
                    );
                }
            } else if ("operation".equals(args[3])) {
                LocalDate startDate = LocalDate.parse(args[4]);
                LocalDate endDate = LocalDate.parse(args[5]);
                String type = "all";
                if (args.length >= 7 && args[6] != null) {
                    type = args[6];
                }
                Map<Fund, List<Operation>> fundOperationsByDateMap = account.getOperationsByDateRange(startDate, endDate);
                for (Fund fund : fundOperationsByDateMap.keySet()) {
                    if (fundOperationsByDateMap.get(fund) == null || fundOperationsByDateMap.get(fund).isEmpty()) {
                        continue;
                    }
                    boolean printHeader = false;
                    for (Operation operation : fundOperationsByDateMap.get(fund)) {
                        if ((type.equals("in") && operation.getShare().compareTo(BigDecimal.ZERO) < 0)
                            || (type.equals("out") && operation.getShare().compareTo(BigDecimal.ZERO) > 0)) {
                            continue;
                        }
                        if (printHeader == false) {
                            System.out.printf("%s\n", fund.getName());
                            printHeader = true;
                        }
                        Asset asset = account.getAsset(fund, operation.getConfirmedDate()); 
                        if (asset == null) {
                            asset = new Asset(null, "coconan");
                        }
                        System.out.printf("%s %s %s %10s %10s %10s %10s %10s\n",
                                operation.getFund().getCode(),
                                operation.getSubmittedDate(),
                                operation.getConfirmedDate(),
                                operation.getCost().setScale(2, RoundingMode.HALF_DOWN),
                                operation.getNetUnitValue().setScale(4, RoundingMode.HALF_DOWN),
                                operation.getShare().setScale(2, RoundingMode.HALF_DOWN),
                                operation.getServiceFee().setScale(2, RoundingMode.HALF_DOWN),
                                asset.getCostPrice().setScale(4, RoundingMode.HALF_DOWN));
                    }
                    if (printHeader) {
                        System.out.println();
                    }
                }
            }

            Executor.networkIO().shutdown();
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
