package com.rag.util;

import java.util.List;

public class CosineUtil {

    /**
     * 计算两个向量余弦相似度
     *
     * @param vec1 向量1
     * @param vec2 向量2
     * @return 相似度 [-1,1]，越接近1越相似
     */
    public static double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("向量维度不一致");
        }
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vec1.size(); i++) {
            float v1 = vec1.get(i);
            float v2 = vec2.get(i);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}