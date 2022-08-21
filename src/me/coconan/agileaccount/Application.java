package me.coconan.agileaccount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    
    public static void main(String[] args) {
        Path assetsPath = Paths.get(args[0]);
        Path operationsPath = Paths.get(args[1]);
        Path fundsPath = Paths.get(args[2]);
        try (FileReader assetsFileReader = new FileReader(assetsPath.toFile());
            FileReader operationsFileReader = new FileReader(operationsPath.toFile());
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
            try (BufferedReader reader = new BufferedReader(operationsFileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length != 8) {
                        continue;
                    }
                    account.addOperation(new Operation(fundStore.get(fields[0].trim()), fields[3].trim(), fields[5].trim(), fields[4].trim(), fields[6].trim(), fields[7].trim()));
                }
            }

            System.out.printf("%6s %10s %10s %10s %16s%% %10s %10s %16s %s\n",
                "code", "cost", "amount", "earning", "earning rate", "cost price", "share", "fixed earning", "name");
            for (Asset asset : account.getAssets()) {
                String code = asset.getFund().getCode();
                String name = asset.getFund().getName();
                BigDecimal cost = asset.getCost().setScale(2, RoundingMode.HALF_DOWN);
                BigDecimal amount = asset.getAmount().setScale(2, RoundingMode.HALF_DOWN);
                BigDecimal earning = amount.subtract(cost).setScale(2, RoundingMode.HALF_UP);
                BigDecimal earningRate = cost.compareTo(BigDecimal.valueOf(0)) == 0
                    ? BigDecimal.valueOf(0)
                    : earning.divide(cost, 5, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
                BigDecimal costPrice = asset.getCostPrice().setScale(4, RoundingMode.HALF_DOWN);
                BigDecimal share = asset.getShare().setScale(2, RoundingMode.HALF_DOWN);
                BigDecimal fixedEarning = asset.getFixedEarning().setScale(2, RoundingMode.HALF_DOWN);
                System.out.printf("%6s %10s %10s %10s %16s%% %10s %10s %16s %s\n", code, cost, amount, earning, earningRate, costPrice, share, fixedEarning, name);
            }
            BigDecimal totalCost = account.getTotalCost().setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal totalAmount = account.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal totalEarning = totalAmount.subtract(totalCost).setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal earningRate = totalEarning.divide(totalCost, 5, RoundingMode.HALF_DOWN).setScale(2, RoundingMode.HALF_DOWN);
            System.out.printf("%6s %10s %10s %10s %16s%% %s\n", " ", totalCost, totalAmount, totalEarning, earningRate, " ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
