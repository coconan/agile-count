package me.coconan.agileaccount.command;

import me.coconan.agileaccount.*;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AllocationCommand implements Command {
    private final String[] args;
    private final Account account;

    public AllocationCommand(String[] args, Account account) {
        this.args = args;
        this.account = account;
    }

    @Override
    public void execute() {
        LocalDate date = LocalDate.now();
        String orderBy = "weight";
        if (args.length > 3 && args[3] != null && !args[3].isEmpty()) {
            if (args[3].equals("-s")) {
                if (args[4].equals("weight")) {
                    orderBy = "weight";
                } else {
                    orderBy = "hold";
                }
                if (args.length > 5 && args[5] != null && !args[5].isEmpty()) {
                    date = LocalDate.parse(args[5]);
                }
            } else {
                date = LocalDate.parse(args[3]);
            }
        }
        System.out.printf("%10s %10s %16s%% %16s %16s %16s %16s %10s%% %10s%% %s\n",
                "cost", "amount", "earning rate", "earning holding", "fixed", "accum", "service fee",
                "weight", "hold", "allocation");
        List<Asset> assets = account.getAssets(date);
        Map<Allocation, List<Asset>> allocationMap = new HashMap<>();
        for (Asset asset : assets) {
            if (allocationMap.get(asset.getInvestmentTarget().getAllocation()) == null) {
                List<Asset> assetList = new ArrayList<>();
                allocationMap.put(asset.getInvestmentTarget().getAllocation(), assetList);
            }
            allocationMap.get(asset.getInvestmentTarget().getAllocation()).add(asset);
        }
        InvestmentStats investmentStats = account.getInvestmentStats(assets, date);
        List<AllocationRow> allocationRows = new ArrayList<>();
        for (Allocation allocation : allocationMap.keySet()) {
            AllocationRow allocationRow = AllocationRow.build(allocation, allocationMap.get(allocation), investmentStats, date);
            allocationRows.add(allocationRow);
        }
        if (orderBy.equals("weight")) {
            allocationRows.sort((row1, row2) -> row2.getCost().compareTo(row1.getCost()));
        } else {
            allocationRows.sort(Comparator.comparing(AllocationRow::getHold));
        }
        for (AllocationRow allocationRow : allocationRows) {
            System.out.printf("%10s %10s %16s%% %16s %16s %16s %16s %10s%% %10s%% %s\n",
                    allocationRow.getCost(), allocationRow.getAmount(), allocationRow.getEarningRate(),
                    allocationRow.getEarning(), allocationRow.getFixedEarning(), allocationRow.getAccumulatedEarning(),
                    allocationRow.getServiceFee(), allocationRow.getWeight(), allocationRow.getHold(),
                    allocationRow.getAllocation().getName());
        }

        System.out.printf("%10s %10s %16s%% %16s %16s %16s %16s %10s %10s %s\n",
                investmentStats.getTotalCost().setScale(2, RoundingMode.HALF_DOWN),
                investmentStats.getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                investmentStats.getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                investmentStats.getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                investmentStats.getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                investmentStats.getTotalAccumEarning().setScale(2, RoundingMode.HALF_DOWN),
                investmentStats.getTotalServiceFee().setScale(2, RoundingMode.HALF_DOWN),
                "", "", "");
    }
}
