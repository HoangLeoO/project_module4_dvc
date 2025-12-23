package org.example.project_module4_dvc.service.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class SimulatedDigitalSigningService {

    public String signPdf(String inputPdfPath, String signerName) throws IOException {
        File inputFile = new File(inputPdfPath);
        if (!inputFile.exists()) {
            throw new IOException("Input PDF not found: " + inputPdfPath);
        }

        try (PDDocument document = PDDocument.load(inputFile)) {
            // Get the last page to sign
            PDPage page = document.getPage(document.getNumberOfPages() - 1);
            PDRectangle mediaBox = page.getMediaBox();

            // Create content stream for appending
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                
                // Define position (Bottom Right)
                float width = 170; // Slightly wider
                float height = 80;
                float padding = 20;
                // Align right with ~50 units margin
                float startX = mediaBox.getWidth() - width - 50; 
                // Move down to align with "Người ký" section (bottom ~100-120 units)
                float startY = 120;

                // Draw a box (Simulate stamp border)
                contentStream.setStrokingColor(Color.RED);
                contentStream.setLineWidth(2);
                contentStream.addRect(startX, startY, width, height);
                contentStream.stroke();

                // Load Font (Try Arial or Times for Vietnamese support)
                // Fallback to standard font and removed accents if loading fails
                PDType0Font font = null;
                boolean useStandardFont = false;
                try {
                    File fontFile = new File("C:/Windows/Fonts/arial.ttf");
                    if (!fontFile.exists()) fontFile = new File("C:/Windows/Fonts/times.ttf");
                    if (fontFile.exists()) {
                        font = PDType0Font.load(document, fontFile);
                    } else {
                        useStandardFont = true;
                    }
                } catch (IOException e) {
                    useStandardFont = true;
                }

                // Add Text
                contentStream.beginText();
                contentStream.setNonStrokingColor(Color.RED);
                
                // Line 1: Header
                contentStream.setFont(useStandardFont ? PDType1Font.HELVETICA_BOLD : font, 10);
                contentStream.newLineAtOffset(startX + 10, startY + height - 15);
                contentStream.showText("KY SO BOI (SIGNED BY):");
                
                // Line 2: Signer Name
                contentStream.setFont(useStandardFont ? PDType1Font.HELVETICA_BOLD : font, 10);
                contentStream.newLineAtOffset(0, -15);
                
                String displaySigner = signerName.toUpperCase();
                if (useStandardFont) {
                    displaySigner = unAccent(displaySigner); // Helper method to remove accents
                }
                contentStream.showText(displaySigner);

                // Line 3: Date
                contentStream.setFont(useStandardFont ? PDType1Font.HELVETICA : font, 8);
                contentStream.newLineAtOffset(0, -15);
                String params = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                contentStream.showText("Ngay (Date): " + params);
                
                // Line 4: Valid
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Tinh trang: HOP LE (Valid)");
                
                contentStream.endText();
            }

            // Save as new file
            String outputPdfPath = inputPdfPath.replace(".pdf", "_signed.pdf");
            // Prevent double suffix if already signed
            if (inputPdfPath.endsWith("_signed.pdf")) {
                outputPdfPath = inputPdfPath; // Overwrite if already named _signed
            }
            
            document.save(outputPdfPath);
            return outputPdfPath;
        }
    }

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}
