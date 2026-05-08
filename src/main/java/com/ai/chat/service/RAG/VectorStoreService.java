package com.ai.chat.service.RAG;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VectorStoreService {

    @Value("${app.documents.travel-policy.file.path}")
    private String filePath;
    @Value("${app.documents.events.file.path}")
    private String eventsFilePath;

    private final PDFLoader pdfLoader;
    private final TokenTextSplitter textSplitter;
    @Getter
    private final VectorStore vectorStore;

    public VectorStoreService(PDFLoader pdfLoader, VectorStore vectorStore){
        this.pdfLoader=pdfLoader;
        this.textSplitter= TokenTextSplitter.builder().build();
        this.vectorStore = vectorStore;
    }

    public String initialize(){
        String pdfText = pdfLoader.loadPdf(filePath);
        String eventText = pdfLoader.loadPdf(eventsFilePath);
        vectorStore.add(textSplitter.split(new Document(pdfText)));
        vectorStore.add(textSplitter.split(new Document(eventText)));
        return "Document saved in vector store";
    }
}
