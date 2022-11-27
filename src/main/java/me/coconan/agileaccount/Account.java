package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        LocalDate date = operation.getConfirmedDate();
        if (date.isBefore(startedDate)) {
            startedDate = date;
        }
        
        List<Operation> operationList = fundOperationByDateMap.get(date);
        if (operationList == null) {
            operationList = new ArrayList<>();
        }
        operationList.add(operation);
        fundOperationByDateMap.put(date, operationList);
    }

    public List<Asset> getAssets(LocalDate date) {
        Map<LocalDate, Map<Fund, Asset>> assetByMonthMap = buildAssetByDateMap();
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

    public Map<LocalDate, InvestmentStats> getInvestmentStatsByDate() {
        Map<LocalDate, InvestmentStats> investmentStatsByDate = new HashMap<>();
        
        Map<LocalDate, Map<Fund, Asset>> assetByMonthMap = buildAssetByDateMap();
        for (LocalDate date = getStartedDate();
            date.isBefore(LocalDate.now().plusDays(1));
            date = date.plusDays(1)) {
            Map<Fund, Asset> assetMapThisDate = assetByMonthMap.get(date);
            if (assetMapThisDate == null) {
                assetMapThisDate = new HashMap<>();
            }
            InvestmentStats investmentStats = getInvestmentStats(assetMapThisDate.values(), date);
            investmentStatsByDate.put(date, investmentStats);
        }

        return investmentStatsByDate;
    }

    private Map<LocalDate, Map<Fund, Asset>> buildAssetByDateMap() {
        Map<LocalDate, Map<Fund, Asset>> assetByDateMap = new HashMap<>();
        for (LocalDate date = getStartedDate(); date.isBefore(LocalDate.now().plusDays(1)); date = date.plusDays(1)) {
            Map<Fund, Asset> assetMapLastDate = assetByDateMap.get(date.minusDays(1));
            if (assetMapLastDate == null) {
                assetMapLastDate = new HashMap<>();
            }
            Map<Fund, Asset> assetMapThisDate = new HashMap<>();
            for (Fund fund : assets.keySet()) {
                Asset assetLastDate = assetMapLastDate.get(fund);
                if (assetLastDate == null) {
                    assetLastDate = new Asset(fund, "coconan");
                }
                List<Operation> operationsInThisDate = assets.get(fund).get(date);
                if (operationsInThisDate == null) {
                    operationsInThisDate = new ArrayList<>();
                }
                Asset assetThisMonth = calculateAsset(assetLastDate, operationsInThisDate);
                assetMapThisDate.put(fund, assetThisMonth);
            }
            assetByDateMap.put(date, assetMapThisDate);
        }

        return assetByDateMap;
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
