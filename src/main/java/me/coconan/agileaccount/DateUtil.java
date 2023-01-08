package me.coconan.agileaccount;

import java.time.LocalDate;

public class DateUtil {
    public static LocalDate changeStep(LocalDate date, String step, int count) {
        switch (step) {
            case "day":
                return date.plusDays(count);
            case "week":
                return date.plusWeeks(count);
            case "month":
                return date.plusMonths(count);
            case "year":
                return date.plusYears(count);
            default:
                throw new RuntimeException("unsupported step");
        }
    }
}
