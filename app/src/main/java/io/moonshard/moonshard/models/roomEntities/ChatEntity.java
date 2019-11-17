package io.moonshard.moonshard.models.roomEntities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.ArrayList;
import io.moonshard.moonshard.models.jabber.GenericUser;

@Entity(tableName = "chats")
public class ChatEntity {
    @PrimaryKey
    @NonNull
    public String jid;
    @ColumnInfo
    public String chatName;
    @ColumnInfo
    public ArrayList<GenericUser> users;
    @ColumnInfo
    public int unreadMessagesCount;
    @ColumnInfo
    public String firstMessageUid;
    @ColumnInfo
    public Boolean isGroupChat;

    public ChatEntity(@NonNull String jid, String chatName, ArrayList<GenericUser> users,
                      int unreadMessagesCount, String firstMessageUid,Boolean isGroupChat) {
        this.jid = jid;
        this.chatName = chatName;
        this.users = users;
        this.unreadMessagesCount = unreadMessagesCount;
        this.firstMessageUid = firstMessageUid;
        this.isGroupChat = isGroupChat;
    }

    public boolean isPrivateChat() {
        return users.size() == 2;
    }
}
