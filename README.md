# ARender for Alfresco

ARender for Alfresco integrates Alfresco and the [ARender](https://arender.io/) rendition server as a content transformer.

## Usage

ARender for Alfresco is available in Maven Central as an [Alfresco Module Package](https://docs.alfresco.com/6.1/concepts/dev-extensions-packaging-techniques-amps.html)

### Gradle

The [Alfresco Docker Gradle Plugins](https://github.com/xenit-eu/alfresco-docker-gradle-plugin) can be used to build an Alfresco docker image with ARender.

```groovy
dependencies {
    alfrescoAmp 'eu.xenit.transformers:Arender4Alfresco:1.0.0'
}
```

### Manual installation

You can find the AMP on [Maven Central](https://search.maven.org/artifact/eu.xenit.transformers/Arender4Alfresco/1.0.0/amp), and install it manually, as specified in the [Alfresco documentation](https://docs.alfresco.com/6.1/tasks/amp-install.html).

## Configuration

For this module to work, the transformer must be pointed to an ARender server.
This can be done by adding the `content.transformer.arender2pdf.arenderRenditionServerAddress` global property which points to the base URL of the ARender server.

This module [preconfigures a set of supported content transformations](./src/main/amp/config/alfresco/module/com.arondor.arender.alfresco.content.transformer/alfresco-global.properties).
These defaults can be overridden in the `alfresco-global.properties` file, as documented in [the Alfresco documentation for transformers](https://docs.alfresco.com/6.1/references/dev-extension-points-content-transformer.html).
