package com.oliveoyl.utils;

//import com.everis.jpmorgancc.data.JPMCertificateInfo;
import com.oliveoyl.CryptoFishy;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PDFUtils {

    private static final Integer MARGIN_TOP_FIRST_FIELD = 240;
    private static final Integer MARGIN_TOP_SECOND_FIELD = 330;
    private static final Integer MARGIN_TOP_THIRD_FIELD = 450;
    private static final Integer FONT_SIZE = 16;
    private static final Integer FONT_SIZE_SMALL = 8;

    public static void generatePDFCertificate(CryptoFishy cryptoFishy, String fileName, String regulatoryBodyName) {

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Create a new font object selecting one of the PDF base fonts
            PDFont font = PDType1Font.HELVETICA_BOLD;
            //PDFont font = PDTrueTypeFont.loadTTF(document, new File("certificates/template/calibri.ttf"));

            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"

            String description = "This certificate allow to fish " + cryptoFishy.getType() + " in " + cryptoFishy.getLocation()  + " .";

            String regulatorBody = regulatoryBodyName;

            writeCenteredLine(contentStream, font, page, description, MARGIN_TOP_FIRST_FIELD, FONT_SIZE);
            writeCenteredLine(contentStream, font, page, regulatorBody, MARGIN_TOP_SECOND_FIELD, FONT_SIZE);

            document.setDocumentInformation(addDocumentInfo(cryptoFishy, regulatorBody));

            // Make sure that the content stream is closed:
            contentStream.close();

            String templatepath = "certificates/template/template2018.pdf";

            HashMap<Integer, String> overlayGuide = new HashMap<>();
            for(int i=0; i<document.getNumberOfPages(); i++){
                overlayGuide.put(i+1, templatepath);
            }
            Overlay overlay = new Overlay();
            overlay.setInputPDF(document);
            overlay.setOverlayPosition(Overlay.Position.BACKGROUND);

            overlay.overlay(overlayGuide);

            // Save the results and ensure that the document is properly closed:
            document.save("certificates/generated/" + fileName);
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCenteredLine(PDPageContentStream contentStream, PDFont font, PDPage page, String text, Integer marginTop, Integer fontSize) {
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(getPositionX(font, page, text, fontSize), getPositionY(font, page, marginTop));
            contentStream.showText(text);
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Float getPositionX(PDFont font, PDPage page, String text, Integer fontSize) {
        return (page.getMediaBox().getWidth() - getTextWidth(font, text, fontSize)) / 2;
    }

    private static Float getPositionY(PDFont font, PDPage page, Integer marginTop) {
        return page.getMediaBox().getHeight() - marginTop - getFontHeight(font);
    }

    private static float getTextWidth(PDFont font, String text, Integer fontSize) {
        try {
            return font.getStringWidth(text) / 1000 * fontSize;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static float getFontHeight(PDFont font) {
        return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * FONT_SIZE;
    }

    private static PDDocumentInformation addDocumentInfo(CryptoFishy cryptoFishy, String regulatorBodyName) {

        PDDocumentInformation pdDocumentInformation = new PDDocumentInformation();
        pdDocumentInformation.setAuthor("CorDapp");
        pdDocumentInformation.setCustomMetadataValue("RegulatorBody", regulatorBodyName);
        pdDocumentInformation.setCustomMetadataValue("Year", String.valueOf(cryptoFishy.getYear()));
        pdDocumentInformation.setCustomMetadataValue("Type", cryptoFishy.getType());
        pdDocumentInformation.setCustomMetadataValue("Location", cryptoFishy.getLocation());

        return pdDocumentInformation;
    }


    public static CryptoFishyCertificateInfo getDocumentInfo(File file) {

        CryptoFishyCertificateInfo certificateInfo = null;
        PDDocument document = null;
        try {
            certificateInfo = new CryptoFishyCertificateInfo();
            document = PDDocument.load(file);
            PDDocumentInformation insertedInfo = document.getDocumentInformation();
            certificateInfo.setRegulatorBody(insertedInfo.getCustomMetadataValue("RegulatorBody"));
            certificateInfo.setYear(Integer.parseInt(insertedInfo.getCustomMetadataValue("Year")));
            certificateInfo.setType(insertedInfo.getCustomMetadataValue("Type"));
            certificateInfo.setLocation(insertedInfo.getCustomMetadataValue("Location"));
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("ERROR reading file meta inf");
            file.delete();
            return null;
        }

        return certificateInfo;
    }

}
