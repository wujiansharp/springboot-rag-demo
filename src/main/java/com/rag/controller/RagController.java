package com.rag.controller;

import com.rag.entity.DocumentChunk;
import com.rag.service.DocumentService;
import com.rag.service.LlmChatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
//@RequestMapping("/rag")
public class RagController {

    @Resource
    private DocumentService documentService;

    @Resource
    private LlmChatService llmChatService;

    // 内存临时保存文档切片，生产要存入向量库
    private List<DocumentChunk> chunkList;
    @GetMapping("/")
    public String index(){
        return "RAG Demo 服务正常！接口：/loadDoc GET 、/chat POST";
    }
    @GetMapping("/error")
    public String error(){
        return "RAG Demo error！接口：/loadDoc GET 、/chat POST";
    }
    /**
     * 上传文档，解析切片、生成向量
     */
    @GetMapping("/loadDoc")
    public String loadDoc(@RequestParam String docId, @RequestParam String filePath) throws Exception {
        chunkList = documentService.loadAndSplitDocument(docId, filePath);
        return "文档处理完成，切片数量：" + chunkList.size();
    }

    /**
     * RAG问答接口
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String docId,@RequestParam String question) {
        if(chunkList == null || chunkList.isEmpty()){
            return "请先加载文档";
        }
        return llmChatService.chat(question, chunkList);
    }
}
