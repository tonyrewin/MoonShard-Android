package io.moonshard.moonshard.helpers;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import io.moonshard.moonshard.models.dao.ChatDao;
import io.moonshard.moonshard.models.dao.MessageDao;
import io.moonshard.moonshard.models.roomEntities.ChatEntity;
import io.moonshard.moonshard.models.roomEntities.MessageEntity;

@Database(entities = { MessageEntity.class, ChatEntity.class }, version = 1)

@TypeConverters({RoomTypeConverter.class})
public abstract class RoomHelper extends RoomDatabase {
    public abstract ChatDao chatDao();
    public abstract MessageDao messageDao();
}
