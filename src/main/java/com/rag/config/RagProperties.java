package com.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {
    private EmbeddingProp embedding;
    private LlmProp llm;
    private ChunkProp chunk;

    @Data
    public static class EmbeddingProp{
        private String apiUrl;
        private String apiKey;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
    @Data
    public static class LlmProp{
        private String apiUrl;
        private String apiKey;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
    @Data
    public static class ChunkProp{
        private Integer size;
        private Integer overlap;

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public Integer getOverlap() {
            return overlap;
        }

        public void setOverlap(Integer overlap) {
            this.overlap = overlap;
        }
    }

    public EmbeddingProp getEmbedding() {
        return embedding;
    }

    public void setEmbedding(EmbeddingProp embedding) {
        this.embedding = embedding;
    }

    public LlmProp getLlm() {
        return llm;
    }

    public void setLlm(LlmProp llm) {
        this.llm = llm;
    }

    public ChunkProp getChunk() {
        return chunk;
    }

    public void setChunk(ChunkProp chunk) {
        this.chunk = chunk;
    }
}

