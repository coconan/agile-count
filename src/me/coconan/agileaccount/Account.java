package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private Map<Fund, List<Operation>> assets = new HashMap<>();

    public void addOperation(Operation operation) {
        List<Operation> operationList = assets.get(operation.getFund());
        if (operationList == null) {
            operationList = new ArrayList<>();
        }
        operationList.add(operation);
       assets.put(operation.getFund(), operationList);
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
