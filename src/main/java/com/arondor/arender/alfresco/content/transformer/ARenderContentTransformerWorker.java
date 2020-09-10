package com.arondor.arender.alfresco.content.transformer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.alfresco.repo.content.transform.ContentTransformerWorker;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.InitializingBean;

public class ARenderContentTransformerWorker implements ContentTransformerWorker, InitializingBean
{
    private final ContentTransformARenderRestClient restClient = new ContentTransformARenderRestClient();

    private String arenderRenditionServerAddress = null;

    public ARenderContentTransformerWorker() {
    }

    @Override
    public boolean isAvailable()
    {
        return restClient.getWeatherPerformance() >= 0;
    }

    @Override
    public String getVersionString()
    {
        return "1.0.0";
    }

    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options)
    {
        return "application/pdf".equals(targetMimetype);
    }

    @Override
    public String getComments(boolean available)
    {
        return null;
    }

    @Override
    public void transform(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception
    {
        String uuid = UUID.randomUUID().toString();
        try (InputStream contentInputStream = reader.getContentInputStream())
        {
            restClient.uploadDocument(uuid, contentInputStream, reader.getMimetype());
            try (InputStream inputStream = restClient.getInputStream(uuid, "RENDERED"))
            {
                writer.putContent(inputStream);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        restClient.setAddress(getArenderRenditionServerAddress());
    }

    public String getArenderRenditionServerAddress()
    {
        return arenderRenditionServerAddress;
    }

    public void setArenderRenditionServerAddress(String arenderRenditionServerAddress)
    {
        this.arenderRenditionServerAddress = arenderRenditionServerAddress;
    }
}
