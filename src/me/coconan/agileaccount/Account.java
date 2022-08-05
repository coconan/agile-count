package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private Map<String, List<Asset>> assets = new HashMap<>();

    public void addAsset(Asset asset) {
        List<Asset> assetBucket = assets.get(asset.getFund().getCode());
        if (assetBucket == null) {
            assetBucket = new ArrayList<>();
        }
        assetBucket.add(asset);
       assets.put(asset.getFund().getCode(), assetBucket);
    }

    public Asset getAsset(String code) {
        List<Asset> assetBucket = assets.get(code);
        BigDecimal totalShare = calculateTotalShare(assetBucket);
        BigDecimal totalCost = calculateTotalCost(assetBucket);
        BigDecimal costPrice = totalCost.divide(totalShare, 5, RoundingMode.HALF_UP);
        return new Asset(assetBucket.get(0).getFund(), totalShare.toString(), costPrice.toString(), "coconan");
    }

    public Collection<Asset> getAssets() {
        Collection<Asset> assetCollection = new ArrayList<>();
        for (String code : assets.keySet()) {
            assetCollection.add(getAsset(code));
        }
        return assetCollection;
    }

    public BigDecimal getTotalAmount() {
        return calculateTotalAmount(getAssets());
    }

    public BigDecimal getTotalCost() {
        return calculateTotalCost(getAssets());
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

    private BigDecimal calculateTotalShare(Collection<Asset> assets) {
        BigDecimal totalShare = new BigDecimal(0);
        for (Asset asset : assets) {
            totalShare = totalShare.add(asset.getShare());
        }
        return totalShare;
    }
}