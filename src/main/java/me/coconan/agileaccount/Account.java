package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Account {
    private final Map<Fund, Map<LocalDate, List<Operation>>> assets = new HashMap<>();
    private LocalDate startedDate;

    public Account() {
        startedDate = LocalDate.now();
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }
    
    public void addOperation(Operation operation) {
        Map<LocalDate, List<Operation>> fundOperationByDateMap = assets.computeIfAbsent(operation.getFund(), k -> new HashMap<>());
        LocalDate lastDayOfMonth = operation.getConfirmedDate().with(TemporalAdjusters.lastDayOfMonth());
        if (lastDayOfMonth.isBefore(startedDate)) {
            startedDate = lastDayOfMonth;
        }
        
        List<Operation> operationList = fundOperationByDateMap.get(lastDayOfMonth);
        if (operationList == null) {
            operationList = new ArrayList<>();
        }
        operationList.add(operation);
        fundOperationByDateMap.put(lastDayOfMonth, operationList);
    }

    public List<Asset> getAssets(LocalDate date) {
        Map<LocalDate, Map<Fund, Asset>> assetByMonthMap = buildAssetByMonthMap();
        date  = date.with(TemporalAdjusters.lastDayOfMonth());
        if (assetByMonthMap.get(date) == null) {
          return new ArrayList<>();
        }
        List<Asset> assetCollection = new ArrayList<>(assetByMonthMap.get(date).values());
        assetCollection.sort(Comparator.comparing(asset -> asset.getFund().getCode()));
        
        return assetCollection;
    }

    public InvestmentStats getInvestmentStats(Collection<Asset> assets, LocalDate date) {
        InvestmentStats investmentStats = new InvestmentStats();
        investmentStats.setTotalCost(calculateTotalCost(assets));
        investmentStats.setTotalFixedEarning(calculateTotalFixedEarning(assets));
        investmentStats.setTotalAmount(calculateTotalAmount(assets, date));

        return investmentStats;
    }

    public Map<LocalDate, InvestmentStats> getInvestmentStatsByMonth() {
        Map<LocalDate, InvestmentStats> investmentStatsByMonth = new HashMap<>();
        
        Map<LocalDate, Map<Fund, Asset>> assetByMonthMap = buildAssetByMonthMap();
        for (LocalDate date = getStartedDate();
            date.isBefore(LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
            date = date.plusDays(1).with(TemporalAdjusters.lastDayOfMonth())) {
            Map<Fund, Asset> assetMapThisMonth = assetByMonthMap.get(date);
            if (assetMapThisMonth == null) {
                assetMapThisMonth = new HashMap<>();
            }
            InvestmentStats investmentStats = getInvestmentStats(assetMapThisMonth.values(), date);
            investmentStatsByMonth.put(date, investmentStats);
        }

        return investmentStatsByMonth;
    }

    private Map<LocalDate, Map<Fund, Asset>> buildAssetByMonthMap() {
        Map<LocalDate, Map<Fund, Asset>> assetByMonthMap = new HashMap<>();
        for (LocalDate date = getStartedDate();
            date.isBefore(LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
            date = date.plusDays(1).with(TemporalAdjusters.lastDayOfMonth())) {
            Map<Fund, Asset> assetMapLastMonth = assetByMonthMap.get(date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
            if (assetMapLastMonth == null) {
                assetMapLastMonth = new HashMap<>();
            }
            Map<Fund, Asset> assetMapThisMonth = new HashMap<>();
            for (Fund fund : assets.keySet()) {
                Asset assetLastMonth = assetMapLastMonth.get(fund);
                if (assetLastMonth == null) {
                    assetLastMonth = new Asset(fund, "coconan");
                }
                List<Operation> operationsInThisMonth = assets.get(fund).get(date);
                if (operationsInThisMonth == null) {
                    operationsInThisMonth = new ArrayList<>();
                }
                Asset assetThisMonth = calculateAsset(assetLastMonth, operationsInThisMonth);
                assetMapThisMonth.put(fund, assetThisMonth);
            }
            assetByMonthMap.put(date, assetMapThisMonth);
        }

        return assetByMonthMap;
    }

    private Asset calculateAsset(Asset oldAsset, List<Operation> operations) {
        Asset newAsset = oldAsset.clone();
        for (Operation operation : operations) {
            newAsset.apply(operation);
        }
        
        return newAsset;
    }

    private BigDecimal calculateTotalAmount(Collection<Asset> assets, LocalDate date) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (Asset asset : assets) {
            BigDecimal amount = asset.getShare().multiply(asset.getFund().getLatestNetUnitValueForDate(date));
            totalAmount = totalAmount.add(amount);
        }
        
        return totalAmount;
    }

    private BigDecimal calculateTotalCost(Collection<Asset> assets) {
        BigDecimal totalCost = new BigDecimal(0);
        for (Asset asset : assets) {
            totalCost = totalCost.add(asset.getCost());
        }
        
        return totalCost;
    }

    private BigDecimal calculateTotalFixedEarning(Collection<Asset> assets) {
        BigDecimal totalFixedEarning = new BigDecimal(0);
        for (Asset asset : assets) {
            totalFixedEarning = totalFixedEarning.add(asset.getFixedEarning());
        }
        
        return totalFixedEarning;
    }
}
