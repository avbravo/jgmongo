/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jgmongo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JOptionPane;

/**
 *
 * @author avbravo
 */
public class Fechas {

    /**
     *
     * @param date
     * @param formato yyyy-MM-dd
     * @return
     */
    /**
     * convierte a ISODate
     *
     * @param date
     * @return
     */
    public static Date convertISODate(Date date) {
        String nowAsISO = "";
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
            df.setTimeZone(tz);
            nowAsISO = df.format(date);
//            System.out.println("----->>>>> fechacompra " + df.toString());
//            System.out.println("----->>>>> converter " + nowAsISO);
        } catch (Exception e) {
            System.out.println("convertISODate() " + e.getLocalizedMessage());
        }
        return convertStringToDate(nowAsISO);
    }

    public static Date convertStringToDate(String dateString) {
        Date date = null;
//        Date formatteddate = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        try {
            date = df.parse(dateString);
        //    sout formatteddate = df.format(date);
        } catch (Exception ex) {
            System.out.println(ex);
        }
//        return formatteddate;
        return date;
    }

    public static Date conversor(String date, String formato) {

        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(formato);
//        if (date.indexOf("T") == -1) {
//            formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
//            System.out.println(date + " formater: MMM dd, yyyy HH:mm:ss a");
//
//        } else {
//            date = date.replace("T", " ");
//            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(date + " formater: yyyy-MM-dd HH:mm:ss");
//        }
        try {

            return formatter.parse(date);
        } catch (Exception e) {
            System.err.println("Failed to parse Date due to:" + e);
            JOptionPane.showMessageDialog(null, "conversor() " + e.getLocalizedMessage());
            return null;
        }
    }

    public java.sql.Date converterDate(java.util.Date fecha) {
        try {
            long lfecha = fecha.getTime();
            java.sql.Date dtfecha = new java.sql.Date(lfecha);
            return dtfecha;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "converterDate() " + e.getLocalizedMessage());
        }
        return null;
    }

    public java.util.Date getFechaActual() {
        java.util.Calendar ca = java.util.Calendar.getInstance();
        java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
        return new java.sql.Date(mydate.getTime());

    }

    public Integer getAnioActual() {
        java.util.Calendar ca = java.util.Calendar.getInstance();
        java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
        return ca.get(Calendar.YEAR);
    }

    public Integer getMesActual() {
        java.util.Calendar ca = java.util.Calendar.getInstance();
        java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
        return ca.get(Calendar.MONTH);
    }

    public Integer getMesDeUnaFecha(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        return mes;
    }

    public Integer getAnioDeUnaFecha(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        return anio;
    }

    public Integer getDiaDeUnaFecha(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        return dia;
    }

    public Integer getDiaActual() {
        java.util.Calendar ca = java.util.Calendar.getInstance();
        java.sql.Date mydate = new java.sql.Date(ca.getTimeInMillis());
        return ca.get(Calendar.DATE);
    }

    /**
     * devuelve la primera fecha del año
     *
     * @return
     */
    public Date getPrimeraFechaAnio() {
        LocalDate now = LocalDate.now();//# 2015-11-23
        Integer year = now.getYear();
        Integer month = 1;
        Integer day = 1;
        LocalDate firstDay = LocalDate.of(year, month, day);

        Date date = java.sql.Date.valueOf(firstDay);
        return date;

    }

    /**
     * devuelve la ultima fecha del año
     *
     * @return
     */
    public Date getUltimaFechaAnio() {
        LocalDate now = LocalDate.now();//# 2015-11-23
        Integer year = now.getYear();
        Integer month = 12;
        Integer day = 31;
        LocalDate firstDay = LocalDate.of(year, month, day);

        Date date = java.sql.Date.valueOf(firstDay);
        return date;

    }
}
