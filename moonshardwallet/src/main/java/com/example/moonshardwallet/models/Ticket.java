package com.example.moonshardwallet.models;

import java.math.BigInteger;

public class Ticket {

    BigInteger payState;
    BigInteger ticketType;
    String jidEvent;


    public Ticket( BigInteger payState, BigInteger ticketType,String jidEvent) {
        this.payState = payState;
        this.ticketType = ticketType;
        this.jidEvent = jidEvent;
    }

    public String getJidEvent() {
        return jidEvent;
    }

    public BigInteger getPayState() {
        return payState;
    }

    public BigInteger getTicketType() {
        return ticketType;
    }
}
