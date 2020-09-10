package com.arondor.arender.alfresco.content.transformer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContentTransformARenderRestClientTest {

    private ContentTransformARenderRestClient aRenderRestClient;

    @BeforeEach
    public void setupRestClient() {
        aRenderRestClient = new ContentTransformARenderRestClient();
        aRenderRestClient.setAddress(System.getProperty("arender.address"));
    }

    @Test
    public void testWeatherPerformance() {
        assertTrue(aRenderRestClient.getWeatherPerformance() > 0, "Weather performance should be larger than 0");
    }

    private void renderDocument(String docName) throws IOException {

        UUID randomID = UUID.randomUUID();
        try (InputStream testDocument = ContentTransformARenderRestClientTest.class
                .getResourceAsStream(docName)) {
            aRenderRestClient.uploadDocument(randomID.toString(), testDocument, "application/msword");
        }

        try (InputStream result = aRenderRestClient.getInputStream(randomID.toString(), "RENDERED")) {
            byte[] bytes = new byte[5];
            IOUtils.readFully(result, bytes);
            // Check that returned document starts with the PDF header
            assertArrayEquals("%PDF-".getBytes(Charsets.US_ASCII), bytes);
        }
    }

    @Test
    public void testRenderSmallDocument() throws IOException {
        renderDocument("lorem10.doc");
    }

    @Test
    public void testRenderLargeDocument() throws IOException {
        renderDocument("loremLarge.doc");
    }

}
