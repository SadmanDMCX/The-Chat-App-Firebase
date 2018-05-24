package meme.app.dmcx.chatapp.LocalDatabase;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.security.Key;
import java.util.Map;

import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;

public class AppLocalDatabase {

    private SharedPreferences sharedPreferences;
    private Editor editor;

    public AppLocalDatabase() {
        sharedPreferences = SuperVariables._MainActivity.getSharedPreferences(SuperVariables.APP_LOCALDATABASE, 0);
        editor = sharedPreferences.edit();
    }

    public void store(String keyName, Object value) {
        if (value.getClass() == Integer.class) {
            editor.putInt(keyName, (Integer) value);
        } else if (value.getClass() == String.class) {
            editor.putString(keyName, (String) value);
        } else if (value.getClass() == Boolean.class) {
            editor.putBoolean(keyName, (Boolean) value);
        } else if (value.getClass() == Float.class) {
            editor.putFloat(keyName, (Float) value);
        } else if (value.getClass() == Long.class) {
            editor.putLong(keyName, (Long) value);
        }

        editor.apply();
    }

    public Map retrive() {
        return sharedPreferences.getAll();
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
