package com.rag.entity;
import lombok.Data;
import java.util.List;

@Data
public class DocumentChunk {
    // 文档ID
    private String docId;
    // 切片序号
    private Integer chunkIndex;
    // 切片文本
    private String content;
    // 文本向量
    private List<Float> embedding;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding;
    }
}
