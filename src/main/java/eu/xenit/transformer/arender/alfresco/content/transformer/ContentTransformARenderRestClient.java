package eu.xenit.transformer.arender.alfresco.content.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ContentTransformARenderRestClient {

    private static final Logger LOGGER = Logger.getLogger(ContentTransformARenderRestClient.class);

    private String address;

    private final RestTemplate template;

    private final SimpleClientHttpRequestFactory clientHttpRequestFactory;

    public ContentTransformARenderRestClient() {
        template = new RestTemplate();
        clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(120000);
        clientHttpRequestFactory.setReadTimeout(120000);
        template.setRequestFactory(clientHttpRequestFactory);
    }

    public float getWeatherPerformance() {
        try {
            return template.getForObject(UriComponentsBuilder.fromHttpUrl(address + "weather").build().toUri(),
                    Float.class);
        } catch (Exception e) {
            LOGGER.info("Caught exception while trying to fetch weather score, is rendition server down?", e);
            return -1;
        }
    }

    public void setAddress(String address) {
        if (!StringUtils.isEmpty(address)) {
            if (!address.endsWith("/")) {
                address = address + "/";
            }
        } else {
            throw new IllegalArgumentException("Sent null or empty rendition server address");
        }
        this.address = address;
    }


    public void uploadDocument(String uuid, InputStream contentInputStream, String mimeType)
            throws IOException {
        URI uploadUri = UriComponentsBuilder.fromHttpUrl(address
                + "document/{uuid}/upload?mimeType={mimeType}&documentTitle={documentTitle}")
                .buildAndExpand(uuid, mimeType, uuid).toUri();

        ClientHttpRequest uploadRequest = clientHttpRequestFactory.createRequest(uploadUri, HttpMethod.POST);
        uploadRequest.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        IOUtils.copy(contentInputStream, uploadRequest.getBody());

        try (ClientHttpResponse uploadResponse = uploadRequest.execute()) {
            if (uploadResponse.getStatusCode() != HttpStatus.OK) {
                throw new IOException("Failed to upload document: " + uploadResponse.getStatusCode());
            }
        }
    }

    public InputStream getInputStream(String uuid, String selector) throws IOException {
        // wait for layout
        ResponseEntity<Resource> responseEntity = template.exchange(
                UriComponentsBuilder.fromHttpUrl(address + "/document/{uuid}/layout").buildAndExpand(uuid).toUri(),
                HttpMethod.GET, null, Resource.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Could not obtain document layout");
        }

        URI downloadUri = UriComponentsBuilder.fromHttpUrl(address + "accessor/getContent/raw/{uuid}/{selector}")
                .buildAndExpand(uuid, selector).toUri();
        ClientHttpRequest downloadRequest = clientHttpRequestFactory.createRequest(downloadUri, HttpMethod.GET);
        ClientHttpResponse downloadResponse = downloadRequest.execute();
        if (downloadResponse.getStatusCode() != HttpStatus.OK) {
            HttpStatus status = downloadResponse.getStatusCode();
            downloadResponse.close();
            throw new IOException("Failed to download document: " + status);
        }
        return downloadResponse.getBody();
    }
}
