package com.example.application.views.inputvalidation;

import com.example.application.views.MainMenuLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.*;

@PageTitle("Binary Data Validation")
@Route(value = "hello", layout = MainMenuLayout.class)
public class BinaryValidationView extends Composite<VerticalLayout> {

    private static final int MAX_FILE_SIZE_BYTES = 1024*1024*10; // 10MB
    private static final String JPEG_MIME = "image/jpeg";

    public BinaryValidationView() {

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        getContent().add(upload);

        /*
         The following configurations are sent to browser as hints of
         what will be accepted by the server. They are *not* secure validation, but only
         a tip to users.
        */

        // Allow only JPEG file
        upload.setAcceptedFileTypes(JPEG_MIME);

        // Allow only MAX_FILE_SIZE_BYTES
        upload.setMaxFileSize(MAX_FILE_SIZE_BYTES);


        /*
            Add progress listeners on the server side to do upload validation.
        */
        upload.addProgressListener((e)-> {
            // We check both announced length and the current length to be ok
            if (e.getContentLength() > MAX_FILE_SIZE_BYTES || e.getReadBytes() > MAX_FILE_SIZE_BYTES) {
              upload.interruptUpload();
            }

            // Make sure the content is actually is JPEG
            if (e.getReadBytes() > 1 && e.getReadBytes() <10) {
                if (!isJpeg(buffer.getInputStream())) {
                    upload.interruptUpload();
                }
            }

        });
    }

    public static Boolean isJpeg(InputStream input)  {;
        try (DataInputStream ins = new DataInputStream(input)){
            if (ins.readInt() == 0xffd8ffe0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            // Tread same as invalid data
        }
        return false;
    }

}
