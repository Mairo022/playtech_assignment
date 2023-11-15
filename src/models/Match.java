package models;

import enums.Result;

import java.math.BigDecimal;

public class Match {
    public final String matchID;
    public final BigDecimal rateA;
    public final BigDecimal rateB;
    public final Result result;

    public Match(String matchID, String rateA, String rateB, Result result) {
        this.matchID = matchID;
        this.result = result;
        this.rateA = new BigDecimal(rateA);
        this.rateB = new BigDecimal(rateB);
    }
}
