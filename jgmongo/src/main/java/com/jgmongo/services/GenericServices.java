/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jgmongo.services;

import com.jgmongo.interfaces.ServicesInterfaces;

/**
 *
 * @author avbravo
 * @param <T>
 */
public class GenericServices<T> implements ServicesInterfaces {

/**
 * 
 * @param <T>
 * @param url
 * @param apikey
 * @param clazz
 * @return 
 */ 

    public <T> T fromJsonList(String url, String apikey, Class<T> clazz) {

     
        T jsonToObject = getGson().fromJson(jsonToString(url, apikey), clazz);
        return jsonToObject;
    }
    /**
     * 
     * @param <T>
     * @param json
     * @param clazz
     * @return 
     */
    public <T> T fromJsontoPojo(String json, Class<T> clazz) {
        T jsonToObject = getGson().fromJson(json, clazz);
        return jsonToObject;
    }
    /**
     * para objetos java que contienen un atributo Date
     * @param <T>
     * @param json
     * @param clazz
     * @return 
     */
    public <T> T fromJsontoPojoDate(String json, Class<T> clazz) {
        T jsonToObject = getGsonDate().fromJson(json, clazz);
        return jsonToObject;
    }
    
  

}
