/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jgmongo.interfaces;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jgmongo.util.DateDeserializer;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.util.Date;

/**
 *
 * @author avbravo
 */
public interface ServicesInterfaces {

    /**
     * devuelve el json como un string
     *
     * @param url
     * @param apikey
     * @return
     */
    default String jsonToString(String url, String apikey) {
        String jsonString = "";
        HttpResponse<JsonNode> jsonResponse;
        try {
            jsonResponse = Unirest.get(url)
                    .header("Ocp-Apim-Subscription-Key", apikey)
                    .header("content-type", "application/json")
                    .asJson();
            jsonString = jsonResponse.getBody().toString();

        } catch (Exception e) {
            System.out.println("jsonToString() " + e.getLocalizedMessage());
        }
        return jsonString;
    }

    /**
     * devuelve el gson formateado a fecha
     *
     * @return
     */
//    default Gson getGson() {
//
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                .create();
//
//        return gson;
//    }
    default Gson getGson() {

        Gson gson = new GsonBuilder()
               .setPrettyPrinting()
          .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
               .create();

        return gson;
    }
//    default Gson getGson() {
//
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//     
//                .create();
//
//        return gson;
//    }
    default Gson getGsonDate() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .setPrettyPrinting()
              
                .create();

        return gson;
    }
    



    

}
