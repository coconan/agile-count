package me.coconan.agileaccount.command;

import me.coconan.agileaccount.Account;
import me.coconan.agileaccount.Asset;
import me.coconan.agileaccount.AssetRow;
import me.coconan.agileaccount.InvestmentStats;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AssetCommand implements Command {
    private final String[] args;
    private final Account account;

    public AssetCommand(String[] args, Account account) {
        this.args = args;
        this.account = account;
    }

    @Override
    public void execute() {
        LocalDate date = LocalDate.now();
        String orderBy = "weight";
        String export = "console";
        if (args[3] != null && !args[3].isEmpty()) {
            if (args[3].equals("-s") || args[3].equals("-o")) {
                if (args[3].equals("-s")) {
                    if (args[4].equals("weight")) {
                        orderBy = "weight";
                    } else {
                        orderBy = "code";
                    }
                    if (args[5] != null && !args[5].isEmpty()) {
                        if (args[5].equals("-o")) {
                            export = args[6];
                            if (args.length > 7 && args[7] != null && !args[7].isEmpty()) {
                                date = LocalDate.parse(args[7]);
                            }
                        } else {
                            date = LocalDate.parse(args[5]);
                        }
                    }
                } else {
                    export = args[4];
                }
            } else {
                date = LocalDate.parse(args[3]);
            }
        }
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
        if (orderBy.equals("weight")) {
            assetRows.sort((row1, row2) -> row2.getCost().compareTo(row1.getCost()));
        } else {
            assetRows.sort(Comparator.comparing(AssetRow::getCode));
        }
        if (export.equals("console")) {
            System.out.printf("%6s %10s %10s %16s%% %16s %16s %16s %16s %10s%% %10s%% %18s %10s %10s %s\n",
                    "code", "cost", "amount", "earning rate", "earning holding", "fixed", "accum", "service fee",
                    "weight", "hold", "net price [ date]", "cost price", "share", "name");
            for (AssetRow assetRow : assetRows) {
                System.out.printf("%6s %10s %10s %16s%% %16s %16s %16s %16s %10s%% %10s%% %10s [%s] %10s %10s %s\n",
                        assetRow.getCode(), assetRow.getCost(), assetRow.getAmount(), assetRow.getEarningRate(),
                        assetRow.getEarning(), assetRow.getFixedEarning(), assetRow.getAccumEarning(),
                        assetRow.getServiceFee(), assetRow.getWeight(), assetRow.getHold(), assetRow.getNetPrice(),
                        assetRow.getNetPriceLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                        assetRow.getCostPrice(), assetRow.getShare(), assetRow.getName());
            }
            System.out.printf("%6s %10s %10s %16s%% %16s %16s %16s %16s %10s %10s %10s %10s %10s %s\n",
                    " ",
                    investmentStats.getTotalCost().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalAccumEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalServiceFee().setScale(2, RoundingMode.HALF_DOWN),
                    "", "", "", "", "", " ");
        } else {
            System.out.printf("%s,%s,%s,%s%%,%s,%s,%s,%s,%s%%,%s%%,%s,%s,%s,%s\n",
                    "code name", "cost", "amount", "earning rate", "earning holding", "fixed", "accum", "service fee",
                    "weight", "hold", "net price", "[ date]", "cost price", "share");
            for (AssetRow assetRow : assetRows) {
                System.out.printf("%s,%s,%s,%s%%,%s,%s,%s,%s,%s%%,%s%%,%s,[%s],%s,%s\n",
                        assetRow.getCode() + " " + assetRow.getName(), assetRow.getCost(), assetRow.getAmount(),
                        assetRow.getEarningRate(), assetRow.getEarning(), assetRow.getFixedEarning(),
                        assetRow.getAccumEarning(), assetRow.getServiceFee(), assetRow.getWeight(), assetRow.getHold(),
                        assetRow.getNetPrice(),
                        assetRow.getNetPriceLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                        assetRow.getCostPrice(), assetRow.getShare());
            }
            System.out.printf("%s,%s,%s,%s%%,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    " ",
                    investmentStats.getTotalCost().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalAccumEarning().setScale(2, RoundingMode.HALF_DOWN),
                    investmentStats.getTotalServiceFee().setScale(2, RoundingMode.HALF_DOWN),
                    "", "", "", "", "");
        }
    }
}
