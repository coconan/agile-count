package me.coconan.agileaccount;

import me.coconan.agileaccount.command.AssetCommand;
import me.coconan.agileaccount.command.ChartCommand;
import me.coconan.agileaccount.command.OperationCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Application {
    
    public static void main(String[] args) {
        Path operationsDirPath = Paths.get(args[1]);
        Path fundsPath = Paths.get(args[2]);
        try (FileReader fundsFileReader = new FileReader(fundsPath.toFile())) {
            FundStore fundStore = new FundStore();
            try (BufferedReader reader = new BufferedReader(fundsFileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length != 3) {
                        continue;
                    }
                    fundStore.put(fields[0].trim(), new Fund(fields[0].trim(), fields[2]));
                }
            }

            Account account = new Account();
            for (File operationsFile : Objects.requireNonNull(operationsDirPath.toFile().listFiles())) {
                try (BufferedReader reader = new BufferedReader(new FileReader(operationsFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split("\\s+");
                        if (fields.length != 8) {
                            continue;
                        }
                        if (fundStore.get(fields[0].trim()) == null) {
                            continue;
                        }
                        account.addOperation(new Operation(fundStore.get(fields[0].trim()), fields[1].trim(), fields[2].trim(),
                            fields[3].trim(), fields[5].trim(), fields[4].trim(), fields[6].trim(), fields[7].trim()));
                    }
                }
            }

            if ("asset".equals(args[3])) {
                new AssetCommand(args, account).execute();
            } else if ("chart".equals(args[3])) {
                new ChartCommand(args, account).execute();
            } else if ("operation".equals(args[3])) {
                new OperationCommand(args, account).execute();
            }

            Executor.networkIO().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
