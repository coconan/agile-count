package me.coconan.agileaccount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssetAccountTagTest {
    @Test
    public void test() {

        // give
        Account account = new Account("grid");
        Fund fund = new Fund("002121", "广发沪港深新起点股票A", new Allocation("主动"));
        Operation buyGrid = new Operation(fund, "2018-02-01", "2018-02-02", "1000.00", "1000", "1.0000", "0.00", "京东金融", "grid");
        Operation buy = new Operation(fund, "2018-02-01", "2018-02-02", "1000.00", "1000", "1.0000", "0.00", "京东金融", null);
        account.addOperation(buyGrid);
        account.addOperation(buy);

        // when
        LocalDate date = LocalDate.of(2023, 3, 20);
        List<Asset> assets = account.getAssets(date);
        InvestmentStats investmentStats = account.getInvestmentStats(assets, date);

        // then
        assertEquals(new BigDecimal(1000).setScale(2, RoundingMode.HALF_DOWN), investmentStats.getTotalCost());
    }
}
