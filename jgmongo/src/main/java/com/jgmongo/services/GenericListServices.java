/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jgmongo.services;

import com.jgmongo.interfaces.ServicesInterfaces;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author avbravo
 * @param <T>
 */
public class GenericListServices<T> implements ServicesInterfaces {

    public <T> List<T> fromJsonList(String url, String apikey, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {

            Object[] array = (Object[]) java.lang.reflect.Array.newInstance(clazz, 1);
            array = getGson().fromJson(jsonToString(url, apikey), array.getClass());

            for (Object array1 : array) {
                list.add((T) array1);
            }
        } catch (Exception e) {
            System.out.println("fromJsonList() " + e.getLocalizedMessage());
        }
        return list;
    }
}
