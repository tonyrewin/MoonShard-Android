package com.example.moonshardwallet.models;

import java.math.BigInteger;

public class Ticket {
    /*
    0 - Non existed
    1 - Paid
    2 - Fulfilled
    3 - Cancelled
     */
    BigInteger payState;
    BigInteger ticketType;
    String jidEvent;
    BigInteger ticketId;
    String ticketSaleAddress;


    public Ticket( BigInteger payState, BigInteger ticketType,String jidEvent,BigInteger ticketId,String ticketSaleAddress) {
        this.payState = payState;
        this.ticketType = ticketType;
        this.jidEvent = jidEvent;
        this.ticketId =ticketId;
        this.ticketSaleAddress = ticketSaleAddress;
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

    public BigInteger getTicketId() {
        return ticketId;
    }

    public String getTicketSaleAddress() {
        return ticketSaleAddress;
    }

}


