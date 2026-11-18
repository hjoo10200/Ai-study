package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RagService1 {
  // ##### 필드 #####
  private final ChatClient chatClient;
  private final VectorStore vectorStore;
  // private final JdbcTemplate jdbcTemplate;

  // ##### 생성자 #####
  public RagService1(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {

    this.chatClient = chatClientBuilder.build();
    this.vectorStore = vectorStore;
  }

  // ##### 벡터 저장소의 데이터를 모두 삭제하는 메소드 #####
  public void clearVectorStore() {

  }

  // ##### PDF 파일을 ETL 처리하는 메소드 #####
  public void ragEtl(MultipartFile attach, String source, int chunkSize, int minChunkSizeChars) throws IOException {
    Resource resource = new ByteArrayResource(attach.getBytes());
    DocumentReader reader = new PagePdfDocumentReader(resource);
    List<Document> documents = reader.read();

    // 공통 metadata 추가
    for (Document doc : documents) {
      doc.getMetadata().put("source", source);
    }

    // 직은 청크로 분할
    DocumentTransformer transformer = new TokenTextSplitter(chunkSize, minChunkSizeChars, 5, 10000, true);
    List<Document> tronsformedDocuments = transformer.apply(documents);

    // 테이블에 저장
    vectorStore.add(tronsformedDocuments);
  }

  // ##### LLM과 대화하는 메소드 #####
  public String ragChat(String question, double score, String source) {

    if (source == null || source.equals("")) {
      source = "헌법";
    }

    // 검색 조건
    SearchRequest searchRequest = SearchRequest.builder()
        .filterExpression("source == '%s'".formatted(source))
        .similarityThreshold(score)
        .topK(3)
        .build();

    // rag 용 어드바이저 생성
    QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
        .searchRequest(searchRequest)
        .build();

    // LLM 호출 응답
    String answer = chatClient.prompt()
        .user(question)
        .advisors(questionAnswerAdvisor)
        .call()
        .content();

    return answer;
  }
}
