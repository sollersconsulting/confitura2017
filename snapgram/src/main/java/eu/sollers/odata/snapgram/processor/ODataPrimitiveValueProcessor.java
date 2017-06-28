package eu.sollers.odata.snapgram.processor;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.PrimitiveValueProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.springframework.transaction.annotation.Transactional;

import io.github.mat3e.odata.core.spring.annotation.OlingoProcessor;

@OlingoProcessor
public class ODataPrimitiveValueProcessor extends ODataPrimitiveProcessor implements PrimitiveValueProcessor {
    @Override
    @Transactional
    public void readPrimitiveValue(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(0, parts);
        String edmPropertyName = ((UriResourceProperty) parts.get(parts.size() - 2)).getProperty().getName();

        Property property = getPropertyFromEntity(edmPropertyName, uriResourceEntitySet);

        Object value = property.getValue();
        if (value == null) {
            populateResponseForNull(response);
        } else {
            response.setContent(new ByteArrayInputStream(value.toString().getBytes(StandardCharsets.UTF_8)));
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.toContentTypeString());
        }
    }

    @Override
    public void updatePrimitiveValue(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Updating property value");
    }

    @Override
    public void deletePrimitiveValue(ODataRequest request, ODataResponse response, UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Deleting property value");
    }
}
