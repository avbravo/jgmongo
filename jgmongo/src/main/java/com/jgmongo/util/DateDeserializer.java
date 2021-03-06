/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jgmongo.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author avbravo
 */
public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        String date = je.getAsString();
        SimpleDateFormat formatter;
        // prints "Sep 6, 2009"

        // System.out.println("date "+date);
        if (date.indexOf("T") == -1) {
            formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
           System.out.println(date + "aplicando formater: MMM dd, yyyy HH:mm:ss a");

        } else {
            date = date.replace("T", " ");
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           System.out.println(date + "aplicando formater: yyyy-MM-dd HH:mm:ss");
        }
        try {

            return formatter.parse(date);
        } catch (Exception e) {
            System.err.println("JGMongo.deserialize() Failed to parse Date due to:" + e);
            return null;
        }
    }

}
