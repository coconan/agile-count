package me.coconan.agileaccount.command;

import me.coconan.agileaccount.Account;
import me.coconan.agileaccount.Asset;
import me.coconan.agileaccount.Fund;
import me.coconan.agileaccount.Operation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OperationCommand implements Command {
    private final String[] args;
    private final Account account;

    public OperationCommand(String[] args, Account account) {
        this.args = args;
        this.account = account;
    }

    @Override
    public void execute() {
        LocalDate startDate = LocalDate.parse(args[4]);
        LocalDate endDate = LocalDate.parse(args[5]);
        String type = "all";
        if (args.length >= 7 && args[6] != null) {
            type = args[6];
        }
        Map<Fund, List<Operation>> fundOperationsByDateMap = account.getOperationsByDateRange(startDate, endDate);
        for (Fund fund : fundOperationsByDateMap.keySet()) {
            if (fundOperationsByDateMap.get(fund) == null || fundOperationsByDateMap.get(fund).isEmpty()) {
                continue;
            }
            boolean printHeader = false;
            for (Operation operation : fundOperationsByDateMap.get(fund)) {
                if ((type.equals("in") && operation.getShare().compareTo(BigDecimal.ZERO) < 0)
                    || (type.equals("out") && operation.getShare().compareTo(BigDecimal.ZERO) > 0)) {
                    continue;
                }
                if (printHeader == false) {
                    System.out.printf("%s\n", fund.getName());
                    printHeader = true;
                }
                Asset asset = account.getAsset(fund, operation.getConfirmedDate());
                if (asset == null) {
                    asset = new Asset(null, "coconan");
                }
                System.out.printf("%s %s %s %10s %10s %10s %10s %10s\n",
                        operation.getFund().getCode(),
                        operation.getSubmittedDate(),
                        operation.getConfirmedDate(),
                        operation.getCost().setScale(2, RoundingMode.HALF_DOWN),
                        operation.getNetUnitValue().setScale(4, RoundingMode.HALF_DOWN),
                        operation.getShare().setScale(2, RoundingMode.HALF_DOWN),
                        operation.getServiceFee().setScale(2, RoundingMode.HALF_DOWN),
                        asset.getCostPrice().setScale(4, RoundingMode.HALF_DOWN));
            }
            if (printHeader) {
                System.out.println();
            }
        }
    }
}
