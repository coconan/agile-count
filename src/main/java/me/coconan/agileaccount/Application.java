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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Application {
    
    public static void main(String[] args) {
        Path eventsDirPath = Paths.get(args[0]);
        Path targetsDirPath = Paths.get(args[1]);

        FundStore fundStore = new FundStore();
        List<Operation> operations = new ArrayList<>();
        String[] accountTags = args[2].split("\\.");
        String tag = accountTags.length == 2 ? accountTags[1] : null;
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

            operations.addAll(readData(fundStore, type, operationsDirPath, fundsPath));
        }

        Account account = new Account(tag);
        account.addAllOperation(operations);
        if (args[2].contains("asset")) {
            new AssetCommand(args, account).execute();
        } else if (args[2].contains("chart")) {
            new ChartCommand(args, account).execute();
        } else if (args[2].contains("operation")) {
            new OperationCommand(args, account).execute();
        }

        Executor.networkIO().shutdown();
    }

    private static List<Operation> readData(FundStore fundStore, String type, Path operationsDirPath, Path fundsPath) {
        List<Operation> operations = new ArrayList<>();
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
                        if (fields.length != 8 && fields.length != 9) {
                            continue;
                        }
                        if (fundStore.get(fields[0].trim()) == null) {
                            continue;
                        }
                        operations.add(new Operation(fundStore.get(fields[0].trim()), fields[1].trim(), fields[2].trim(),
                                fields[3].trim(), fields[5].trim(), fields[4].trim(), fields[6].trim(), fields[7].trim(),
                                fields.length == 9 ? fields[8].trim() : null));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return operations;
    }
}
