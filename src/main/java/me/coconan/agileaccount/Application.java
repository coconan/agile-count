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
import java.util.Arrays;
import java.util.Objects;

public class Application {
    
    public static void main(String[] args) {
        Path eventsDirPath = Paths.get(args[0]);
        Path targetsDirPath = Paths.get(args[1]);

        FundStore fundStore = new FundStore();
        Account account = new Account();
        for (String type : Arrays.asList("bonds", "funds")) {
            Path operationsDirPath = Paths.get(args[0]);
            Path fundsPath = Paths.get(args[1]);
            for (File targetsFile : Objects.requireNonNull(targetsDirPath.toFile().listFiles())) {
                if (targetsFile.getName().contains(type)) {
                    fundsPath = targetsFile.toPath();
                }
            }
            for (File targetEventsDir : Objects.requireNonNull(eventsDirPath.toFile().listFiles())) {
                if (targetEventsDir.getName().contains(type)) {
                    operationsDirPath = targetEventsDir.toPath();
                }
            }

            readData(fundStore, account, type, operationsDirPath, fundsPath);
        }

        if ("asset".equals(args[2])) {
            new AssetCommand(args, account).execute();
        } else if ("chart".equals(args[2])) {
            new ChartCommand(args, account).execute();
        } else if ("operation".equals(args[2])) {
            new OperationCommand(args, account).execute();
        }

        Executor.networkIO().shutdown();
    }

    private static void readData(FundStore fundStore, Account account, String type, Path operationsDirPath, Path fundsPath) {
        try (FileReader fundsFileReader = new FileReader(fundsPath.toFile())) {
            try (BufferedReader reader = new BufferedReader(fundsFileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\s+");
                    if (fields.length != 3) {
                        continue;
                    }
                    if (type.equals("funds")) {
                        fundStore.put(fields[0].trim(), new Fund(fields[0].trim(), fields[2]));
                    } else {
                        fundStore.put(fields[0].trim(), new ConvertibleBond(fields[0].trim(), fields[2]));
                    }
                }
            }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
