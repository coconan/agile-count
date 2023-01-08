package me.coconan.agileaccount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ChartRow {
    private LocalDate date;
    private InvestmentStats investmentStatsThisDate;
    private BigDecimal accumulatedInvestmentThisDate;
    private BigDecimal accumulatedInvestmentLastDate;
    private BigDecimal investmentThisDate;
    private BigDecimal dailyEarnings;
    private BigDecimal deltaFixedEarnings;

    public LocalDate getDate() {
        return date;
    }

    public InvestmentStats getInvestmentStatsThisDate() {
        return investmentStatsThisDate;
    }

    public BigDecimal getInvestmentThisDate() {
        return investmentThisDate;
    }

    public BigDecimal getDeltaFixedEarnings() {
        return deltaFixedEarnings;
    }

    public BigDecimal getAccumulatedInvestmentThisDate() {
        return accumulatedInvestmentThisDate;
    }

    public BigDecimal getDailyEarnings() {
        return dailyEarnings;
    }

    public static ChartRow build(LocalDate date, String step, Map<LocalDate, InvestmentStats> investmentStatsByDate) {
        InvestmentStats investmentStatsLastDate = investmentStatsByDate.get(DateUtil.changeStep(date, step , -1));
        if (investmentStatsLastDate == null) {
            investmentStatsLastDate = new InvestmentStats();
        }
        InvestmentStats investmentStatsThisDate = investmentStatsByDate.get(date);
        int count = 1;
        while (investmentStatsThisDate == null) {
            investmentStatsThisDate = investmentStatsByDate.get(date.minusDays(count));
            count += 1;
        }
        ChartRow chartRow = new ChartRow();
        chartRow.date = date;
        chartRow.investmentStatsThisDate = investmentStatsThisDate;
        chartRow.accumulatedInvestmentThisDate = investmentStatsThisDate.getTotalCost() == null
                ? BigDecimal.ZERO : investmentStatsThisDate.getTotalCost();
        chartRow.accumulatedInvestmentLastDate = investmentStatsLastDate.getTotalCost() == null
                ? BigDecimal.ZERO : investmentStatsLastDate.getTotalCost();
        chartRow.investmentThisDate = chartRow.accumulatedInvestmentThisDate.subtract(chartRow.accumulatedInvestmentLastDate);
        chartRow.dailyEarnings = investmentStatsByDate.get(date.minusDays(1)) == null
                ? BigDecimal.ZERO
                : investmentStatsThisDate.getTotalEarning()
                .subtract(investmentStatsByDate.get(date.minusDays(1)).getTotalEarning());
        chartRow.deltaFixedEarnings = investmentStatsThisDate.getTotalFixedEarning()
                .subtract(investmentStatsLastDate.getTotalFixedEarning() == null ? BigDecimal.ZERO : investmentStatsLastDate.getTotalFixedEarning());

        return chartRow;
    }
}
