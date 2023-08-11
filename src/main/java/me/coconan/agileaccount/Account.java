package me.coconan.agileaccount;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@ToString
public class Account {
    private final Map<InvestmentTarget, Map<LocalDate, List<Operation>>> operationByFundMap = new HashMap<>();
    private Map<LocalDate, Map<InvestmentTarget, Asset>> assetByDateMap;
    private LocalDate startedDate;
    private final String tag;

    public Account(String tag) {
        startedDate = LocalDate.now();
        this.tag = tag;
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }
    
    public void addOperation(Operation operation) {
        if (tag != null && (operation.getTags() == null || !operation.getTags().contains(tag))) {
            return;
        }

        Map<LocalDate, List<Operation>> fundOperationByDateMap =
                operationByFundMap.computeIfAbsent(operation.getFund(), k -> new HashMap<>());
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

        assetByDateMap = null;
    }

    public void addAllOperation(List<Operation> operations) {
        for (Operation operation : operations) {
            addOperation(operation);
        }
    }

    public List<Asset> getAssets(LocalDate date) {
        Map<LocalDate, Map<InvestmentTarget, Asset>> assetByDateMap = buildAssetByDateMap();
        if (assetByDateMap.get(date) == null) {
          return new ArrayList<>();
        }
        List<Asset> assetCollection = new ArrayList<>(assetByDateMap.get(date).values());
        assetCollection.sort(Comparator.comparing(asset -> asset.getInvestmentTarget().getCode()));
        
        return assetCollection;
    }

    public Asset getAsset(InvestmentTarget fund, LocalDate date) {
        Map<LocalDate, Map<InvestmentTarget, Asset>> assetByDateMap = buildAssetByDateMap();
        if (assetByDateMap.get(date) == null) {
          return null;
        }
        
        return assetByDateMap.get(date).get(fund);
    }

    public InvestmentStats getInvestmentStats(Collection<Asset> assets, LocalDate date) {
        InvestmentStats investmentStats = new InvestmentStats();
        investmentStats.setTotalCost(calculateTotalCost(assets));
        investmentStats.setTotalFixedEarning(calculateTotalFixedEarning(assets));
        investmentStats.setTotalAmount(calculateTotalAmount(assets, date));
        investmentStats.setTotalServiceFee(calculateTotalServiceFee(assets));

        return investmentStats;
    }

    public Map<LocalDate, InvestmentStats> getInvestmentStatsByDate() {
        Map<LocalDate, InvestmentStats> investmentStatsByDate = new HashMap<>();
        
        Map<LocalDate, Map<InvestmentTarget, Asset>> assetByDateMap = buildAssetByDateMap();
        for (LocalDate date = getStartedDate();
            date.isBefore(LocalDate.now().plusDays(1));
            date = date.plusDays(1)) {
            Map<InvestmentTarget, Asset> assetMapThisDate = assetByDateMap.get(date);
            if (assetMapThisDate == null) {
                assetMapThisDate = new HashMap<>();
            }
            InvestmentStats investmentStats = getInvestmentStats(assetMapThisDate.values(), date);
            investmentStatsByDate.put(date, investmentStats);
        }

        return investmentStatsByDate;
    }

    public Map<InvestmentTarget, List<Operation>> getOperationsByDateRange(LocalDate startedDate, LocalDate endDate) {
        Map<InvestmentTarget, List<Operation>> fundOperationsMap = new HashMap<>();
        for (InvestmentTarget fund : operationByFundMap.keySet()) {
            List<Operation> operations = new ArrayList<>();
            for (LocalDate date = startedDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                if (operationByFundMap.get(fund) != null && operationByFundMap.get(fund).get(date) != null) {
                    operations.addAll(operationByFundMap.get(fund).get(date));
                }
            }
            fundOperationsMap.put(fund, operations);
        }

        return fundOperationsMap;
    }

    private Map<LocalDate, Map<InvestmentTarget, Asset>> buildAssetByDateMap() {
        if (assetByDateMap != null) {
            return assetByDateMap;
        }
        assetByDateMap = new HashMap<>();
        for (LocalDate date = getStartedDate(); date.isBefore(LocalDate.now().plusDays(1)); date = date.plusDays(1)) {
            Map<InvestmentTarget, Asset> assetMapLastDate = assetByDateMap.get(date.minusDays(1));
            if (assetMapLastDate == null) {
                assetMapLastDate = new HashMap<>();
            }
            Map<InvestmentTarget, Asset> assetMapThisDate = new HashMap<>();
            for (InvestmentTarget fund : operationByFundMap.keySet()) {
                Asset assetLastDate = assetMapLastDate.get(fund);
                if (assetLastDate == null) {
                    assetLastDate = new Asset(fund, "coconan");
                }
                List<Operation> operationsInThisDate = operationByFundMap.get(fund).get(date);
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
        Asset newAsset = oldAsset.copy();
        for (Operation operation : operations) {
            newAsset.apply(operation);
        }
        
        return newAsset;
    }

    private BigDecimal calculateTotalAmount(Collection<Asset> assets, LocalDate date) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (Asset asset : assets) {
            DailyRecord dailyRecord = asset.getInvestmentTarget().getLatestDailyRecord(date);
            BigDecimal amount = asset.getShare().multiply(dailyRecord.getClosingPrice());
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

    private BigDecimal calculateTotalServiceFee(Collection<Asset> assets) {
        BigDecimal totalServiceFee = new BigDecimal(0);
        for (Asset asset : assets) {
            totalServiceFee = totalServiceFee.add(asset.getServiceFee());
        }

        return totalServiceFee;
    }
}
