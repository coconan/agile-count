package me.coconan.agileaccount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssetFixedEarningTest {
    @Test
    public void test() {
        // give
        Fund fund = new Fund("002121", "广发沪港深新起点股票A");
        Operation buy = new Operation(fund, "2018-02-01", "2018-02-02", "1000.00", "1000", "1.0000", "0.00", "京东金融");
        Operation sell = new Operation(fund, "2018-02-02", "2018-02-03", "1100.00", "-1000", "1.1000", "0.00", "京东金融");
        Asset asset = new Asset(fund, "京东金融");
        asset.apply(buy);
        asset.apply(sell);

        // when
        BigDecimal fixedEarning = asset.getFixedEarning();

        // then
        assertEquals(new BigDecimal(100).setScale(5, RoundingMode.HALF_DOWN), fixedEarning);
    }
}
