package com.lr.platform.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.lr.platform.entity.GoogleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.GGSSchemeBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
@Slf4j
public class GoogleCaptcha {
    /*
    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }*/
    /*
    static CloseableHttpClient httpClient;
    static CloseableHttpResponse httpResponse;

    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();

    }*/

    public static void main(String[] args){


        ///invalid-keys
    }

    public static Boolean isValidGoogleCaptchaV2WithV3(String token,String action,String accessKeyV2,String accessKeyV3){
        if (token==null || token.equals("")) return false;
        String url="https://www.recaptcha.net/recaptcha/api/siteverify?secret="+accessKeyV3//googleCaptchaConfig.getAccessKey()
                +"&response="+token;
        HttpPost httpPost=new HttpPost(url);
        GoogleResponse googleResponse=null;
        try (CloseableHttpClient httpClient= HttpClients.createDefault();
             CloseableHttpResponse httpResponse=httpClient.execute(httpPost)){
            HttpEntity httpEntity=httpResponse.getEntity();
            if (httpEntity!=null){
                String res= EntityUtils.toString(httpEntity);
                googleResponse=Dozer.toEntity(res,GoogleResponse.class);
            }
        }catch (Exception e){
            log.error("ERROR:",e);
        }
        boolean res =googleResponse != null &&
                googleResponse.getSuccess() &&
                googleResponse.getScore() >= 0.6 &&
                Objects.equals(googleResponse.getAction(), action);
        if (!res){
            System.out.println(googleResponse);
        }else {
            return true;
        }
        if (googleResponse != null && googleResponse.getErrorCodes()!=null &&
                !googleResponse.getErrorCodes().isEmpty() &&
                Objects.equals(googleResponse.getErrorCodes().get(0), "invalid-keys")){
            return isValidGoogleCaptchaV2(token,accessKeyV2);
        }
        return false;
    }

    public static Boolean isValidGoogleCaptchaV2(String token,String accessKey){
        if (token==null || token.equals("")) return false;
        String url="https://www.recaptcha.net/recaptcha/api/siteverify?secret="+accessKey//googleCaptchaConfig.getAccessKey()
                +"&response="+token;
        HttpPost httpPost=new HttpPost(url);
        GoogleResponse googleResponse=null;
        try (CloseableHttpClient httpClient= HttpClients.createDefault();
             CloseableHttpResponse httpResponse=httpClient.execute(httpPost)){
            HttpEntity httpEntity=httpResponse.getEntity();
            if (httpEntity!=null){
                String res= EntityUtils.toString(httpEntity);
                googleResponse=Dozer.toEntity(res,GoogleResponse.class);
            }
        }catch (Exception e){
            log.error("ERROR:",e);
        }
        Boolean res =googleResponse != null && googleResponse.getSuccess();
        if (!res){
            System.out.println(googleResponse);
        }
        return res;
    }


    public static Boolean isValidGoogleCaptcha(String token,String action,String accessKey){
        if (token==null || token.equals("")) return false;
        String url="https://www.recaptcha.net/recaptcha/api/siteverify?secret="+accessKey//googleCaptchaConfig.getAccessKey()
                +"&response="+token;
        HttpPost httpPost=new HttpPost(url);
        GoogleResponse googleResponse=null;
        try (CloseableHttpClient httpClient= HttpClients.createDefault();
             CloseableHttpResponse httpResponse=httpClient.execute(httpPost)){
            HttpEntity httpEntity=httpResponse.getEntity();
            if (httpEntity!=null){
                String res= EntityUtils.toString(httpEntity);
                googleResponse=Dozer.toEntity(res,GoogleResponse.class);
                if (googleResponse==null ||!googleResponse.getSuccess()){
                    System.out.println(googleResponse);
                }
            }
        }catch (Exception e){
            log.error("ERROR:",e);
        }
        Boolean res =googleResponse != null && googleResponse.getSuccess() && googleResponse.getScore() >= 0.6 && Objects.equals(googleResponse.getAction(), action);
        if (!res){
            System.out.println(googleResponse);
        }
        return res;
    }


    public static Boolean isValidGoogleCaptcha(String token,String action,String accessKey,RestTemplate restTemplate){
        if (token==null || token.equals("")) return false;
        String url="https://www.recaptcha.net/recaptcha/api/siteverify?secret="+accessKey//googleCaptchaConfig.getAccessKey()
                +"&response="+token;
        ResponseEntity<String> responseEntity=restTemplate.postForEntity(url,null,String.class);
        GoogleResponse googleResponse=Dozer.toEntity(responseEntity.getBody(),GoogleResponse.class);
        return googleResponse != null && googleResponse.getSuccess() && googleResponse.getScore() >= 0.6 && Objects.equals(googleResponse.getAction(), action);
        /*
        RestTemplate restTemplate=new RestTemplate();
        String url="https://www.recaptcha.net/recaptcha/api/siteverify?secret="+googleCaptchaConfig.getAccessKey()
                +"&response="+token;
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity=new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);
        GoogleResponse googleResponse= Dozer.toEntity(response.getBody(),GoogleResponse.class);
        return googleResponse != null && googleResponse.getSuccess() && googleResponse.getScore() >= 0.6 && Objects.equals(googleResponse.getAction(), action);
        */
        /*

        HttpGet httpGet=new HttpGet(url);
        GoogleResponse googleResponse=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                ObjectMapper mapper=new ObjectMapper();
                googleResponse=mapper.readValue(EntityUtils.toString(entity),GoogleResponse.class);
            }
        } catch (Exception e) {
            return null;
        }*/
    }


}
