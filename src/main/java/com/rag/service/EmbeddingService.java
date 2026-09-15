package com.rag.service;

import com.alibaba.fastjson2.JSON;
import com.rag.config.RagProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {
    @Resource
    private RagProperties ragProperties;

    // JDK11 HttpClient，单例复用
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    /**
     * 文本转向量
     * @param text 输入文本
     * @return 向量List<Float>
     */
    public List<Float> getEmbedding(String text) {
        try {
            // 构造请求体 openAI embedding格式
            Map<String, Object> bodyMap = Map.of(
                    "input", text,
                    "model", "text-embedding-v1"
            );
            String jsonBody = JSON.toJSONString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ragProperties.getEmbedding().getApiUrl()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ragProperties.getEmbedding().getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // 解析返回JSON
            Map<String, Object> respMap = JSON.parseObject(response.body());
            Map<String, Object> dataItem = (Map<String, Object>) ((List<?>)respMap.get("data")).get(0);
            List<Number> embeddingNumList = (List<Number>) dataItem.get("embedding");

            List<Float> result = new ArrayList<>();
            for(Number num : embeddingNumList){
                result.add(num.floatValue());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("调用Embedding接口失败", e);
        }
    }
}
