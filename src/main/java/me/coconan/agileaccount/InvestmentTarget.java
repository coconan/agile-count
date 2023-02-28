package me.coconan.agileaccount;

import java.time.LocalDate;

public interface InvestmentTarget {
    String getCode();
    String getName();
    DailyRecord getLatestDailyRecord(LocalDate date);
}
