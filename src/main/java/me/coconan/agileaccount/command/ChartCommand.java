package me.coconan.agileaccount.command;

import me.coconan.agileaccount.Account;
import me.coconan.agileaccount.ChartRow;
import me.coconan.agileaccount.DateUtil;
import me.coconan.agileaccount.InvestmentStats;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ChartCommand implements Command {
    private final String[] args;
    private final Account account;

    public ChartCommand(String[] args, Account account) {
        this.args = args;
        this.account = account;
    }

    @Override
    public void execute() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<LocalDate, InvestmentStats> investmentStatsByDate = account.getInvestmentStatsByDate();
        String step = "day";
        if (args.length >= 5 && args[4] != null) {
            step = args[4];
        }
        LocalDate start = account.getStartedDate();
        LocalDate end = LocalDate.now();
        if (args.length == 7 && args[5] != null && args[6] != null) {
            start = LocalDate.parse(args[5]);
            end = LocalDate.parse(args[6]);
        }
        System.out.printf("%-10s %16s %16s %16s %16s %16s%% %16s %16s %16s %16s\n",
                "date", "delta cost", "total cost", "net in/out", "total amount", "earning rate", "holding earning", "fixed earning", "accum earning", "daily earning");
        for (LocalDate date = start;
            date.isBefore(DateUtil.changeStep(end, step, 1));
            date = DateUtil.changeStep(date, step, 1)) {
            ChartRow chartRow = ChartRow.build(date, step, investmentStatsByDate);
            System.out.printf("%10s %16s %16s %16s %16s %16s%% %16s %16s %16s %16s\n",
                dtf.format(chartRow.getDate()),
                chartRow.getInvestmentThisDate().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getAccumulatedInvestmentThisDate().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getInvestmentThisDate().subtract(chartRow.getDeltaFixedEarnings()).setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getInvestmentStatsThisDate().getTotalAmount().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getInvestmentStatsThisDate().getEarningRate().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getInvestmentStatsThisDate().getTotalEarning().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getInvestmentStatsThisDate().getTotalFixedEarning().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getInvestmentStatsThisDate().getTotalAccumEarning().setScale(2, RoundingMode.HALF_DOWN),
                chartRow.getDailyEarnings().setScale(2, RoundingMode.HALF_DOWN)
            );
        }
    }
}
