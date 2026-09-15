package com.rag.service;

import com.alibaba.fastjson2.JSON;
import com.rag.config.RagProperties;
import com.rag.entity.DocumentChunk;
import com.rag.util.CosineUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LlmChatService {

    @Resource
    private EmbeddingService embeddingService;
    @Resource
    private RagProperties ragProperties;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * RAG问答：用户问题 -> 向量检索匹配文档片段 -> 组装prompt调用LLM
     */
    public String chat(String userQuestion, List<DocumentChunk> allChunks){
        // 1.问题向量化
        List<Float> questionVector = embeddingService.getEmbedding(userQuestion);

        // 2.向量相似度检索（余弦相似度，取topN相关切片）
        List<DocumentChunk> relatedChunks = searchTopSimilar(questionVector, allChunks, 3);

        // 3.把检索出来的上下文+用户问题组装Prompt
        String prompt = buildPrompt(userQuestion, relatedChunks);

        // 4.调用大模型接口返回答案
        String answer = callLlmApi(prompt);
        return answer;
    }

    // 余弦相似度筛选topN片段
    private List<DocumentChunk> searchTopSimilar(List<Float> vec, List<DocumentChunk> chunks, int topN){
        return chunks.stream()
                .map(chunk -> {
                    double score = CosineUtil.cosineSimilarity(vec, chunk.getEmbedding());
                    return new Object(){
                        DocumentChunk c = chunk;
                        double s = score;
                    };
                })
                .sorted((a,b) -> Double.compare(b.s, a.s))
                .limit(topN)
                .map(item -> item.c)
                .collect(Collectors.toList());
    }

    // 构造提示词
    private String buildPrompt(String question, List<DocumentChunk> chunks){
        StringBuilder context = new StringBuilder();
        for(DocumentChunk c : chunks){
            context.append(c.getContent()).append("\n");
        }
        return "基于下面【参考资料】回答用户问题，不要编造资料外信息。\n【参考资料】\n" + context + "\n用户问题：" + question;
    }

    // 请求大模型接口
    private String callLlmApi(String prompt){
        try {
            // OpenAI兼容消息体
            List<Map<String,String>> messages = List.of(
                    Map.of("role", "system", "content", "你是文档问答助手，严格依据给定上下文回答"),
                    Map.of("role", "user", "content", prompt)
            );
            Map<String, Object> bodyMap = Map.of(
                    "model", "qwen-turbo",
                    "messages", messages,
                    "temperature", 0.1
            );
            String jsonBody = JSON.toJSONString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ragProperties.getLlm().getApiUrl()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ragProperties.getLlm().getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> respMap = JSON.parseObject(response.body());
            List<Map<String,Object>> choices = (List<Map<String,Object>>) respMap.get("choices");
            Map<String,Object> msg = (Map<String,Object>) choices.get(0).get("message");
            return (String) msg.get("content");
        }catch (Exception e){
            e.printStackTrace();
            return "调用大模型接口异常：" + e.getMessage();
        }
    }
}
