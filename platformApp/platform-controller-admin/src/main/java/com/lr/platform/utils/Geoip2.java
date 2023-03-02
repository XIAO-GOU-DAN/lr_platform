package com.lr.platform.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class Geoip2 {
    private volatile static Geoip2 geoip2;

    private DatabaseReader reader;

    private static final String PATH="/jar/GeoLite2-ASN.mmdb";

    private Geoip2(){
        try {
            //URL url = ResourceUtils.getURL("classpath:GeoLite2-ASN.mmdb");
            //PATH=url.getPath();
            File file=new File(PATH);
            reader = new DatabaseReader.Builder(file).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Geoip2 getInstance(){
        if (geoip2==null){
            synchronized (Geoip2.class){
                if (geoip2==null){
                    geoip2=new Geoip2();
                }
            }
        }
        return geoip2;
    }

    public AsnResponse getAsn(String ip) throws IOException, GeoIp2Exception {
        InetAddress address =InetAddress.getByName(ip);
        return reader.asn(address);
    }
    public static void main(String[] args) throws IOException, GeoIp2Exception {
    }
}
