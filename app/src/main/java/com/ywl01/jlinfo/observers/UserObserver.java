package com.ywl01.jlinfo.observers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.User;

import java.util.List;

public class UserObserver extends BaseObserver {
    @Override
    protected User convert(String data) {
        List<User> users = new Gson().fromJson(data, new TypeToken<List<User>>() {}.getType());
        if(users.size() > 0)
            return users.get(0);
        return null;
    }
}
