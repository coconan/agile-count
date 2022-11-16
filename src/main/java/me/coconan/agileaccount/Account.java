package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private Map<Fund, List<Operation>> assets = new HashMap<>();
    private Map<LocalDate, List<Operation>> operationsByDate = new HashMap<>();
    private LocalDate startedDate;

    public Account() {
        startedDate = LocalDate.now();
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }
    
    public void addOperation(Operation operation) {
        List<Operation> operationList = assets.get(operation.getFund());
        if (operationList == null) {
            operationList = new ArrayList<>();
        }
        operationList.add(operation);
        assets.put(operation.getFund(), operationList);
        
        LocalDate lastDayOfMonth = operation.getConfirmedDate().with(TemporalAdjusters.lastDayOfMonth());
        if (lastDayOfMonth.isBefore(startedDate)) {
            startedDate = lastDayOfMonth;
        }
        if (operationsByDate.get(lastDayOfMonth) == null) {
            operationsByDate.put(lastDayOfMonth, new ArrayList<>());
        }
        operationsByDate.get(lastDayOfMonth).add(operation);
    }

    public Asset getAsset(Fund fund) {
        List<Operation> operationList = assets.get(fund);
        Asset asset = new Asset(fund, "coconan");
        for (Operation operation : operationList) {
            asset.apply(operation);
        }
        return asset;
    }

    public List<Asset> getAssets() {
        List<Asset> assetCollection = new ArrayList<>();
        for (Fund fund : assets.keySet()) {
            assetCollection.add(getAsset(fund));
        }
        Collections.sort(assetCollection, new Comparator<Asset>() {
            @Override
            public int compare(Asset asset1, Asset asset2) {
                return asset1.getFund().getCode().compareTo(asset2.getFund().getCode());
            }
        });
        
        return assetCollection;
    }

    public BigDecimal getTotalAmount() {
        return calculateTotalAmount(getAssets());
    }

    public BigDecimal getTotalCost() {
        return calculateTotalCost(getAssets());
    }

    public BigDecimal getTotalFixedEarning() {
        return calculateTotalFixedEarning(getAssets());
    }

    public Map<LocalDate, BigDecimal> getInvestmentAmountByMonth() {
        Map<LocalDate, BigDecimal> investmentAmountByMonth = new HashMap<>();
        for (LocalDate date = startedDate; date.isBefore(LocalDate.now()); date = date.plusDays(1).with(TemporalAdjusters.lastDayOfMonth())) {
            investmentAmountByMonth.put(date, calculateTotalAmountForOperations(operationsByDate.get(date)));
        }
        
        return investmentAmountByMonth;
    }

    private BigDecimal calculateTotalAmountForOperations(Collection<Operation> operations) {
        if (operations == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalAmount = new BigDecimal(0);
        for (Operation operation : operations) {
            if (operation.getShare().compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = totalAmount.add(operation.getCost());
            } else {
                totalAmount = totalAmount.subtract(operation.getCost());
            }
        }
        
        return totalAmount;       
    }

    private BigDecimal calculateTotalAmount(Collection<Asset> assets) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (Asset asset : assets) {
            totalAmount = totalAmount.add(asset.getAmount());
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
