package com.rag.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

public class VectorStoreService {
    // 存储文本片段 + 向量
    private final List<DocChunk> chunkList = new ArrayList<>();

    public static class DocChunk {
        private String text;
        private List<Double> vector;

        public DocChunk(String text, List<Double> vector) {
            this.text = text;
            this.vector = vector;
        }
        // getter setter
        public String getText() {return text;}
        public List<Double> getVector() {return vector;}
    }

    // 添加向量片段
    public void addChunk(String text, List<Double> vector) {
        chunkList.add(new DocChunk(text, vector));
    }

    // 余弦相似度检索，返回topN相关文本
    public List<String> searchTopN(List<Double> queryVector, int topN) {
        return chunkList.stream()
                .map(chunk -> Map.entry(chunk.getText(), cosSimilar(queryVector, chunk.getVector())))
                .sorted((a,b)-> Double.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 余弦相似度计算
    private double cosSimilar(List<Double> v1, List<Double> v2) {
        double dot = 0.0;
        double mod1 = 0.0;
        double mod2 = 0.0;
        for(int i=0;i<v1.size();i++){
            dot += v1.get(i)*v2.get(i);
            mod1 += Math.pow(v1.get(i),2);
            mod2 += Math.pow(v2.get(i),2);
        }
        return dot / (Math.sqrt(mod1)*Math.sqrt(mod2));
    }
}
