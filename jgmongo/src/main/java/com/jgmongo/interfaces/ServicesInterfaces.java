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
import org.bson.Document;

/**
 *
 * @author avbravo
 * @param <T>
 */
public interface ServicesInterfaces<T> {

    default public Document toDoc(T t) {
        Document doc = new Document();
        try {
            doc = Document.parse(getGson().toJson(t));
        } catch (Exception e) {
            System.out.println("toDocument() " + e.getLocalizedMessage());
        }
        return doc;
    }

    
//    default T toJava(Document doc, T t1) {
//
//        T o = fromJsontoJava(doc.toJson(), (Class<T>) t1);
//
//        return o;
//    }
    default T toJava(Document doc, Class<T> clazz) {

        T o = fromJsontoJava(doc.toJson(), clazz);

        return o;
    }

    default <T> T fromJsontoJava(String json, Class<T> clazz) {
        T jsonToObject = getGson().fromJson(json, clazz);
        return jsonToObject;
    }

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
//               .setPrettyPrinting()
//          .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//               .create();
//
//        return gson;
//    }
    /**
     * formato de fecha
     *
     * @param dateformat
     * @return
     */
    default Gson getGson(String... dateformat) {
        String format = "dd/MM/yyyy HH:mm:ss a";
        if (dateformat.length != 0) {
            format = dateformat[0];

        }
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy HH:mm:ss a")
                .setPrettyPrinting()
          
                .create();

        return gson;
    }

//    default Gson getGson() {
//      
//        Gson gson = new GsonBuilder()
//                .setDateFormat("dd/MM/yyyy HH:mm:ss a")
//                .setPrettyPrinting()
//              
//                .create();
//
//        return gson;
//    }
//    default Gson getGsonFecha() {
//
//        Gson gson = new GsonBuilder()
//                .setDateFormat("dd/MM/yyyy")
//                .setPrettyPrinting()
//                .create();
//
//        return gson;
//    }

//    default Gson getGson() {
//
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Date.class, new DateDeserializer())
//                .setPrettyPrinting()
//              
//                .create();
//
//        return gson;
//    }
//    default Gson getGsonDate() {
//
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Date.class, new DateDeserializer())
//                .setPrettyPrinting()
//                .create();
//
//        return gson;
//    }

}
