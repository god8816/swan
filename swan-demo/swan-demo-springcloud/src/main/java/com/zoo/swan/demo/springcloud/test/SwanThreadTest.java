package com.zoo.swan.demo.springcloud.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Swan多线程测试
 * */
public class SwanThreadTest {

	public static void main(String[] args) {
		
		for(int i=0;i<20;i++) {
			Thread td = new Thread(new Runnable() {
				
				@Override
				public void run() {
					 String url = "http://127.0.0.1:8080/save";
					 String json = "{}";
					 Map<String, String> headsMap = new HashMap<>();
					 headsMap.put("YST_ADMIN_KEY","213");
					 String result = httpPostWithJsonAndHeader(url,json,headsMap);
					 System.out.println("请求结果："+result);
				}
			});
			td.start();
		}
	}
	
	
    public static String httpPostWithJsonAndHeader(String url, String json, Map<String, String> headsMap) {
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json, "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        //头
        if (headsMap != null && !headsMap.isEmpty()) {
            headsMap.forEach((key, value) -> {
                httpPost.addHeader(key, value);
            });
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    result = EntityUtils.toString(responseEntity);
                }
            } else {
                return null;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}




