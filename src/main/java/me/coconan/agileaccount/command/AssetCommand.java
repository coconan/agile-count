package me.coconan.agileaccount.command;

import me.coconan.agileaccount.Account;
import me.coconan.agileaccount.Asset;
import me.coconan.agileaccount.AssetRow;
import me.coconan.agileaccount.InvestmentStats;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        if (args.length == 4 && args[3] != null) {
            date = LocalDate.parse(args[3]);
        }
        System.out.printf("%6s %10s %10s %16s%% %16s %16s %16s %16s %10s%% %18s %10s %10s %s\n",
                "code", "cost", "amount", "earning rate", "earning holding", "fixed", "accum", "service fee",
                "weight", "net price [ date]", "cost price", "share", "name");
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
        assetRows.sort((row1, row2) -> row2.getCost().compareTo(row1.getCost()));
        for (AssetRow assetRow : assetRows) {
            System.out.printf("%6s %10s %10s %16s%% %16s %16s %16s %16s %10s%% %10s [%s] %10s %10s %s\n",
                    assetRow.getCode(), assetRow.getCost(), assetRow.getAmount(), assetRow.getEarningRate(),
                    assetRow.getEarning(), assetRow.getFixedEarning(), assetRow.getAccumEarning(),
                    assetRow.getServiceFee(), assetRow.getWeight(), assetRow.getNetPrice(),
                    assetRow.getNetPriceLocalDate().format(DateTimeFormatter.ofPattern("MM-dd")),
                    assetRow.getCostPrice(), assetRow.getShare(), assetRow.getName());
        }

        System.out.printf("%6s %10s %10s %16s%% %16s %16s %16s %16s %10s %10s %10s %10s %s\n",
            " ",
            investmentStats.getTotalCost().setScale(2, RoundingMode.HALF_DOWN),
            investmentStats.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
            investmentStats.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
            investmentStats.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
            investmentStats.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
            investmentStats.getTotalAccumEarning().setScale(2, RoundingMode.HALF_DOWN),
            investmentStats.getTotalServiceFee().setScale(2, RoundingMode.HALF_DOWN),
            "", "", "", "", " ");
    }
}
