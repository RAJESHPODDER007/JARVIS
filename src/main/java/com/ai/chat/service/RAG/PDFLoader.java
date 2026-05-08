package com.ai.chat.service.RAG;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PDFLoader {

    public String loadPdf(String path){
        ClassPathResource
                classPathResource = new ClassPathResource(path);
        try(InputStream inputStream = classPathResource.getInputStream();
                PDDocument document= PDDocument.load(inputStream)){
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
