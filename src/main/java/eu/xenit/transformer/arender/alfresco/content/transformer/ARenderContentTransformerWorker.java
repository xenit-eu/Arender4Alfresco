package eu.xenit.transformer.arender.alfresco.content.transformer;

import java.io.InputStream;
import java.util.UUID;
import org.alfresco.repo.content.transform.ContentTransformerWorker;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.log4j.Logger;

public class ARenderContentTransformerWorker implements ContentTransformerWorker {

    private static final Logger LOGGER = Logger.getLogger(ARenderContentTransformerWorker.class);

    private ContentTransformARenderRestClient restClient;

    @Override
    public boolean isAvailable() {
        return restClient.getWeatherPerformance() >= 0;
    }

    @Override
    public String getVersionString() {
        return "1.0.0";
    }

    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options) {
        return "application/pdf".equals(targetMimetype);
    }

    @Override
    public String getComments(boolean available) {
        return null;
    }

    @Override
    public void transform(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {
        String uuid = UUID.randomUUID().toString();
        try (InputStream contentInputStream = reader.getContentInputStream()) {
            LOGGER.debug("Starting rendition for reader " + reader + " with reference ID " + uuid);
            restClient.uploadDocument(uuid, contentInputStream, reader.getMimetype());
            LOGGER.debug("Uploaded document with reference ID " + uuid + " to Arender");
            try (InputStream inputStream = restClient.getInputStream(uuid, "RENDERED")) {
                LOGGER.debug("Received rendered document for reference ID " + uuid);
                writer.putContent(inputStream);
                LOGGER.debug("Written rendered document for reference ID " + uuid + " to writer " + writer);
            }
        }
    }

    public void setRestClient(ContentTransformARenderRestClient restClient) {
        this.restClient = restClient;
    }
}
