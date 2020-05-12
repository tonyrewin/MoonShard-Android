package io.moonshard.moonshard.models.wallet;

public class GeneralItem extends ListItem {
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }


}