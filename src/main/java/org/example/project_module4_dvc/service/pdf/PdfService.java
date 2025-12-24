package org.example.project_module4_dvc.service.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimulatedDigitalSigningService signingService;

    private final String UPLOAD_DIR = "uploads/pdf";

    public String generateDossierPdf(OpsDossier dossier) throws Exception {
        // 1. Determine Fragment Path
        String serviceCode = dossier.getService().getServiceCode();
        String fragmentPath = getFragmentPath(serviceCode);

        // 2. Prepare Data
        Map<String, Object> formData = null;
        if (dossier.getFormData() != null) {
            Object rawForm = dossier.getFormData();
            if (rawForm instanceof String) {
                formData = objectMapper.readValue((String) rawForm, Map.class);
            } else if (rawForm instanceof Map) {
                formData = (Map<String, Object>) rawForm;
            }
        }

        // 3. Prepare Context
        Context context = new Context();
        context.setVariable("formData", formData);
        context.setVariable("fragmentName", fragmentPath);

        // 4. Render HTML
        String html = templateEngine.process("components/form/pdf-layout", context);

        // 5. Ensure Upload Directory
        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 6. Generate PDF File Path
        String fileName = dossier.getService().getServiceCode() + "_" + dossier.getId() + "_signed.pdf";
        File pdfFile = uploadPath.resolve(fileName).toFile();

        // 7. Render PDF
        try (FileOutputStream os = new FileOutputStream(pdfFile)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            // Point Base URI to static resources for simplified CSS loading in local env
            // This assumes running from project root.
            String baseUri = new File("src/main/resources/static/").toURI().toString();
            builder.withHtmlContent(html, baseUri);
            
            // Register Windows Fonts for Vietnamese support
            File fontDir = new File("C:/Windows/Fonts");
            if (fontDir.exists()) {
                 File regular = new File(fontDir, "times.ttf");
                 File bold = new File(fontDir, "timesbd.ttf");
                 File italic = new File(fontDir, "timesi.ttf");
                 
                 if (regular.exists()) builder.useFont(regular, "Times New Roman");
                 if (bold.exists()) builder.useFont(bold, "Times New Roman");
                 if (italic.exists()) builder.useFont(italic, "Times New Roman");
            }

            builder.toStream(os);
            builder.run();
        }

        return pdfFile.getAbsolutePath();
    }

    private String getFragmentPath(String serviceCode) {
        switch (serviceCode) {
            case "HK01_TRE": return "components/form/preview/birth-registration";
            case "HK02_KAITU": return "components/form/preview/death-registration";
            case "HT01_KETHON": return "components/form/preview/marriage-registration";
            case "HT02_XACNHANHN": return "components/form/preview/marital-status-certificate";
            case "DD01_BIENDONG": return "components/form/preview/land-change-registration";
            case "DD02_CHUYENMDSD": return "components/form/preview/land-purpose-change";
            case "DD03_TACHHOP": return "components/form/preview/land-split-merge";
            case "KD01_HKD": return "components/form/preview/household-business-registration";
            default: return "components/common/no-data";
        }
    }
    public String generateSignedDossierPdf(OpsDossier dossier, String signerName) throws Exception {
        String pdfPath = generateDossierPdf(dossier);
        return signingService.signPdf(pdfPath, signerName);
    }

}
