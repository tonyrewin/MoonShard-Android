package io.moonshard.moonshard.helpers;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.moonshard.moonshard.models.jabber.GenericUser;

public class RoomTypeConverter {

    @TypeConverter
    public static ArrayList<GenericUser> fromString(String value) {
        Type listType = new TypeToken<ArrayList<GenericUser>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<GenericUser> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}
