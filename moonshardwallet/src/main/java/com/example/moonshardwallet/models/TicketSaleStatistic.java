package com.example.moonshardwallet.models;

import java.math.BigInteger;

public class TicketSaleStatistic {
    String typeTicket;
    String amount;
    String all;

    public TicketSaleStatistic(String typeTicket, String amount, String all) {
        this.typeTicket = typeTicket;
        this.amount = amount;
        this.all = all;
    }

    public String getTypeTicket() {
        return typeTicket;
    }

    public String getAmount() {
        return amount;
    }

    public String getAll() {
        return all;
    }
}
