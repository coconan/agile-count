package me.coconan.agileaccount;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyRecord {
    private InvestmentTarget investmentTarget;
    private LocalDate date;
    private BigDecimal closingPrice;
}
