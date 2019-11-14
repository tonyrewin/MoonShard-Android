package io.moonshard.moonshard.models.roomEntities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class MessageEntity {
    @PrimaryKey(autoGenerate = true) public long messageID; // Global message ID
    @ColumnInfo public String chatID; // Chat ID
    @ColumnInfo public String messageUid;
    @ColumnInfo public String senderJid;
    @ColumnInfo public long timestamp; // Timestamp
    @ColumnInfo public String text; // Message text
    @ColumnInfo public boolean isSent; // Send status indicator
    @ColumnInfo public boolean isRead; // Message Read Indicator

    public MessageEntity(String chatID, String messageUid, String senderJid, long timestamp, String text, boolean isSent, boolean isRead) {
        this.chatID = chatID;
        this.messageUid = messageUid;
        this.senderJid = senderJid;
        this.timestamp = timestamp;
        this.text = text;
        this.isSent = isSent;
        this.isRead = isRead;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}