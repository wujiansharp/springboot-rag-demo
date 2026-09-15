- 加载文档：`GET http://localhost:8080/rag/loadDoc?docId=doc001&filePath=/xxx/test.pdf`
- RAG 问答：`GET http://localhost:8080/rag/chat?docId=doc001&question=智能水杯续航多久`
# springboot-rag-demo
> SpringBoot 简易RAG知识库问答Demo，大模型应用层项目，不涉及模型训练。
> 上传文档，基于文档内容进行AI问答，缓解大模型幻觉问题。

## 项目介绍
本项目实现轻量RAG（检索增强生成）。
流程：文档上传 → 文本切片 → 文本向量化 → 向量库存储 → 用户提问 → 向量相似度检索 → 拼接上下文Prompt → 调用LLM大模型API → 返回基于文档内容的答案。

适合企业私有文档问答、内部知识库场景。

## 技术栈
- 后端：SpringBoot 2.7
- 数据库：MySQL（存储会话记录）
- 向量存储：内存向量库（无需额外部署Milvus/ES，开箱运行，Demo简化方案）
- 大模型：通过HTTP调用公有LLM API（DeepSeek/通义千问）
- 接口文档：Swagger
- 工具：Maven

## 功能清单
✅ 文档上传：支持txt、md文本文件上传
✅ 文本切片：长文档自动分段处理
✅ 文本向量化：调用Embedding接口生成向量
✅ 向量检索：根据用户问题，检索文档中最相关片段
✅ Prompt组装：把检索到的文档片段和用户问题一起发给大模型
✅ AI对话：返回基于上传文档的回答
✅ 会话记录：保存历史问答记录

## 项目运行
1. 修改application.yml，填入大模型API Key（本地配置，不要提交到代码仓库）
2. 创建MySQL库，执行sql目录初始化脚本
3. Maven打包，启动SpringBoot主类
4. 访问Swagger接口文档：http://localhost:8080/swagger-ui.html

## RAG原理简述
RAG，检索增强生成。
大模型本身知识存在滞后，还容易编造不存在信息（幻觉）。
RAG先检索私有文档里相关内容，把参考材料一起发给大模型，限制大模型只能参考提供的资料回答，降低幻觉。

## 说明
本项目为个人学习Demo，用于技术展示、学习RAG应用开发。
仅做应用层开发，不训练大模型。API密钥请勿上传至代码仓库。

## 可提供服务
本人可承接Java后端bug修复、SQL优化、AI应用接口开发、技术面试咨询。
如有需求可联系。
