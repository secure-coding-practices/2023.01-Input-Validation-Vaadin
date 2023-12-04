package com.example.application.views.inputvalidation;

import com.example.application.validation.BinaryValidation;
import com.example.application.validation.ValidationResult;
import com.example.application.views.MainMenuLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.tika.Tika;

import java.io.*;

@PageTitle("Upload Validation")
@Route(value = "upload", layout = MainMenuLayout.class)
public class UploadValidationView extends Composite<VerticalLayout> {

    private static final int MAX_FILE_SIZE_BYTES = 1024*1024*10; // 10MB
    private static final String JPEG_MIME = "image/jpeg";
    private static final String PDF_MIME = "application/pdf";

    public UploadValidationView() {

        MemoryBuffer imageBuffer = new MemoryBuffer();
        Upload imageUpload = new Upload(imageBuffer);

        Image image = new Image();
        image.setMaxWidth("300px");
        image.setMaxHeight("300px");

        getContent().add(new H4("JPEG-only Upload"), imageUpload, image);
        /*
         The following configurations are sent to browser as hints of
         what will be accepted by the server. They are *not* secure validation, but only
         a tip to users.
        */

        // Allow only JPEG file extensions
        imageUpload.setAcceptedFileTypes(JPEG_MIME);

        // Allow only MAX_FILE_SIZE_BYTES
        imageUpload.setMaxFileSize(MAX_FILE_SIZE_BYTES);


        /*
            Add progress listeners on the server side to do upload validation.
        */
        imageUpload.addProgressListener(e -> {
            // We check both announced length and the current length to be ok
            if (e.getContentLength() > MAX_FILE_SIZE_BYTES || e.getReadBytes() > MAX_FILE_SIZE_BYTES) {
                imageUpload.interruptUpload();
                Notification.show("Content too long");
            }
        });

        // Checksum and image validation example
        imageUpload.addFinishedListener(e -> {

            byte[] bytes;
            try {
                bytes = imageBuffer.getInputStream().readAllBytes();
            } catch (IOException ex) {
                Notification.show("Upload failed: %s".formatted(ex.getMessage()));
                return;
            }

            // Make sure the content is actually is JPEG
            if (BinaryValidation.isValidJPEG(bytes) != ValidationResult.OK) {
                Notification.show("Not valid JPEG data");
                return;
            }

            // All validation good, safe to show the image
            image.setSrc(new StreamResource("image.jpg", () -> new ByteArrayInputStream(bytes)));

        });

        /*
            SHA256 validateChecksum sample.
         */

        TextField checksum = new TextField("SHA256 Checksum");
        checksum.setTooltipText("Use 'shasum -a 256 <file>'");
        checksum.setWidth("40rem");

        MemoryBuffer checksumBuffer = new MemoryBuffer();
        Upload checksumUpload = new Upload(checksumBuffer);
        getContent().add(new H4("SHA256 Checksum"),
                new Paragraph("Upload and validate SHA256 checksum"),
                checksum,
                checksumUpload);
        checksumUpload.setMaxFiles(1);
        checksumUpload.setMaxFileSize(MAX_FILE_SIZE_BYTES);
        checksumUpload.addFinishedListener(e -> {
            try {
                // Do SHA256 validateChecksum validation
                byte[] data = checksumBuffer.getInputStream().readAllBytes();
                if (BinaryValidation.validateChecksum(data, checksum.getValue()) != ValidationResult.OK) {
                    Notification.show("Checksum mismatch");
                    checksum.setInvalid(true);
                    return;
                }
                checksum.setInvalid(false);
                Notification.show("Checksum matches");
            } catch (IOException ex) {
                Notification.show("Upload failed: %s".formatted(ex.getMessage()));
            }
        });



        /*
           PDF upload and validation sample.
         */
        MemoryBuffer pdfBuffer = new MemoryBuffer();
        Upload pdfUpload = new Upload(pdfBuffer);
        getContent().add(new H4("PDF Upload"),
                new Paragraph("Upload and validate PDF"),
                pdfUpload);

        // Allow only JPEG file
        pdfUpload.setAcceptedFileTypes(PDF_MIME);

        // Allow only MAX_FILE_SIZE_BYTES
        pdfUpload.setMaxFileSize(MAX_FILE_SIZE_BYTES);

        // Allow only one file
        pdfUpload.setMaxFiles(1);

        pdfUpload.addFinishedListener(e -> {
            try {
                byte[] pdfData = pdfBuffer.getInputStream().readAllBytes();
                if (BinaryValidation.isValidPDF(pdfData) != ValidationResult.OK) {
                    Notification.show("Not a valid PDF");
                    return;
                }
            } catch (IOException ex) {
                Notification.show("Upload failed: %s".formatted(ex.getMessage()));
                return;
            }
            Notification.show("The file is valid PDF");
        });

        /* Upload example using Apache Tika for content detection */
        MemoryBuffer tikaBuffer = new MemoryBuffer();
        Upload tikaUpload = new Upload(tikaBuffer);
        tikaUpload.setMaxFiles(1);
        tikaUpload.setMaxFileSize(MAX_FILE_SIZE_BYTES);
        getContent().add(new H2("Content detection"),
                new Paragraph("Make content detection using Apache Tika.")
                ,tikaUpload);
        tikaUpload.addFinishedListener(e -> {
            try {
                byte[] data = imageBuffer.getInputStream().readAllBytes();
                Tika tika = new Tika();
                String detection = tika.detect(data, e.getFileName());
                Notification.show("Uploaded file '%s', %s bytes, %s".formatted(e.getFileName(),e.getContentLength(),detection));
            } catch (IOException ex) {
                Notification.show("Upload failed: %s".formatted(ex.getMessage()));
            }
        });


    }




}
