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
        Path fundsPath = Paths.get(args[1]);
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
            try (BufferedReader reader = new BufferedReader(assetsFileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length != 4) {
                        continue;
                    }
                    account.addAsset(new Asset(fundStore.get(fields[0].trim()), fields[1].trim(), fields[2].trim(), fields[3].trim()));
                }
            }

            for (Asset asset : account.getAssets()) {
                String code = asset.getFund().getCode();
                String name = asset.getFund().getName();
                BigDecimal cost = asset.getCost().setScale(2, RoundingMode.HALF_DOWN);
                BigDecimal amount = asset.getAmount().setScale(2, RoundingMode.HALF_DOWN);
                BigDecimal earning = amount.subtract(cost).setScale(2, RoundingMode.HALF_UP);
                System.out.printf("%6s %10s %10s %10s %s\n", code, cost, amount, earning, name);
            }
            BigDecimal totalCost = account.getTotalCost().setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal totalAmount = account.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN);
            BigDecimal totalEarning = totalAmount.subtract(totalCost).setScale(2, RoundingMode.HALF_DOWN);
            System.out.printf("%6s %10s %10s %10s %s\n", " ", totalCost, totalAmount, totalEarning, " ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
