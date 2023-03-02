package com.lr.platform.utils;

import java.util.regex.Pattern;

public class GeoIP {
    private static String test="China Education and Research Network Center";
    private static final String IP="(\\d{1,3})([.])(\\d{1,3})([.])(\\d{1,3})([.])(\\d{1,3})";

    private static final Pattern ChinaNet=Pattern.compile("(?i)(Chinanet)");
    private static final Pattern ChinaUnicom=Pattern.compile("(?i)(CHINAUNICOM)");
    private static final Pattern ChinaMobile=Pattern.compile("(?i)(ChinaMobile)");
    private static final Pattern ChinaEducation=Pattern.compile("(?i)(ChinaEducation)");
    private static final Pattern Tencent=Pattern.compile("(?i)(Tencent)");
    private static final Pattern Alibaba=Pattern.compile("(?i)(Alibaba)");

    public static Boolean isIp(String ip){
        if (ip==null)return false;
        return Pattern.matches(IP,ip);
    }

    public static String getIp(String str){
        if (str==null)return null;
        return str;
    }

    public static String getCNASNName(String ASN){
        String ASN2=ASN.replaceAll(" ","");
        if (ChinaNet.matcher(ASN2).find()){
            return "电信";
        }
        if (ChinaUnicom.matcher(ASN2).find()){
            return "联通";
        }
        if (ChinaMobile.matcher(ASN2).find()){
            return "移动";
        }
        if (ChinaEducation.matcher(ASN2).find()){
            return "教育网";
        }
        if (Tencent.matcher(ASN2).find()){
            return "腾讯云";
        }
        if (Alibaba.matcher(ASN2).find()){
            return "阿里云";
        }
        return ASN;
    }
    public static void main(String[] args){
        String testip="223.96.43.89";
        System.out.print(isIp(testip));
    }
}
