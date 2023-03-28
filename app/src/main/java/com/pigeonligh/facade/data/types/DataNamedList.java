package com.pigeonligh.facade.data.types;

import com.google.gson.internal.LinkedTreeMap;
import com.pigeonligh.facade.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class DataNamedList<E extends DataNamedItem> {
    private static final String TAG = "DataNamedList";

    private final Class<E> typeClass;
    private final List<E> data;

    public DataNamedList(Class<E> typeClass) {
        this.typeClass = typeClass;
        this.data = new ArrayList<>();
    }

    public DataNamedList(String json, Class<E> typeClass) {
        this.typeClass = typeClass;
        this.data = new ArrayList<E>();

        List<LinkedTreeMap> list = Utils.gson().fromJson(json, ArrayList.class);
        for (LinkedTreeMap obj : list) {
            String itemJson = Utils.gson().toJson(obj);
            this.data.add(Utils.gson().fromJson(itemJson, typeClass));
        }
    }

    public int size() {
        return data.size();
    }

    public int getIdByName(String name) {
        for (int i = 0; i < data.size(); i++) {
            E item = data.get(i);
            if (item.getName().contentEquals(name)) {
                return i;
            }
        }
        return -1;
    }

    public E getByName(String name) {
        int id = getIdByName(name);
        if (id == -1) {
            return null;
        }
        return data.get(id);
    }

    public E get(int i) {
        return data.get(i);
    }

    public boolean addItem(E item) {
        if (getByName(item.getName()) != null) {
            return false;
        }
        data.add(0, item);
        return true;
    }

    public boolean editItem(int i, E item) {
        int find = getIdByName(item.getName());
        if (find == -1 || find == i) {
            data.set(i, item);
            return true;
        }
        return false;
    }

    public void remove(int i) {
        data.remove(i);
    }

    public String toJSON() {
        return Utils.gson().toJson(this.data);
    }

    public void pick(int i) {
        if (i != 0) {
            E v = data.get(i);
            data.remove(i);
            data.add(0, v);
        }
    }

}
