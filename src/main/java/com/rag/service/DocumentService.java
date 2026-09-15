package com.rag.service;
import com.rag.entity.DocumentChunk;
import com.rag.config.RagProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    @Resource
    private EmbeddingService embeddingService;
    @Resource
    private RagProperties ragProperties;

    /**
     * 加载文档，切分，生成向量
     */
    public List<DocumentChunk> loadAndSplitDocument(String docId, String filePath) throws Exception {
        // 1.读取文档文本
        String fullText = readFileText(filePath);
        System.out.println("读取到文档原始内容：【" + fullText + "】");
        System.out.println("文本总长度：" + fullText.length());
        // 2.文本切片
        int chunkSize = ragProperties.getChunk().getSize();
        int overlap = ragProperties.getChunk().getOverlap();
        List<String> textChunks = splitText(fullText, chunkSize, overlap);
        System.out.println("切片完成，切片数量：" + textChunks.size());
        // 3.每个切片生成对象 + 向量化
        List<DocumentChunk> chunkList = new ArrayList<>();
        for (int i = 0; i < textChunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocId(docId);
            chunk.setChunkIndex(i);
            chunk.setContent(textChunks.get(i));
            // 调用向量服务
            chunk.setEmbedding(embeddingService.getEmbedding(textChunks.get(i)));
            chunkList.add(chunk);
        }
        return chunkList;
    }

    // 读取PDF / TXT
    private String readFileText(String filePath) throws Exception {
        if(filePath.endsWith(".pdf")){
            try(PDDocument document = PDDocument.load(new File(filePath))){
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        }else {
            return Files.readString(Paths.get(filePath));
        }
    }

    // 核心：文本切片算法（按字符分段，带重叠）
    private List<String> splitText(String text, int chunkSize, int overlap){
        List<String> chunks = new ArrayList<>();
        if(text == null || text.isBlank()){
            return chunks;
        }
        int start = 0;
        int textLen = text.length();
        while(start < textLen){
            int end = Math.min(start + chunkSize, textLen);
            String piece = text.substring(start, end);
            chunks.add(piece);
            // 滑动窗口
            int nextStart = end - overlap;
            // 防止死循环原地踏步
            if(nextStart <= start){
                break;
            }
            start = nextStart;
        }
        return chunks;
    }
}
