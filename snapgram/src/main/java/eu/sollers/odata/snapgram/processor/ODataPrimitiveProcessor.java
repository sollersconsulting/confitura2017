package eu.sollers.odata.snapgram.processor;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.springframework.transaction.annotation.Transactional;

public class ODataPrimitiveProcessor extends ODataBaseProcessor implements PrimitiveProcessor {
    @Override
    @Transactional
    public void readPrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(parts.size() - 2, parts);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        EdmProperty edmProperty = ((UriResourceProperty) parts.get(parts.size() - 1)).getProperty();
        String edmPropertyName = edmProperty.getName();
        EdmPrimitiveType edmPropertyType = (EdmPrimitiveType) edmProperty.getType();

        Property property = getPropertyFromEntity(edmPropertyName, uriResourceEntitySet);

        Object value = property.getValue();
        if (value == null) {
            populateResponseForNull(response);
        } else {

            ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).navOrPropertyPath(edmPropertyName)
                                              .build();
            PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextUrl).build();

            ODataSerializer serializer = odata.createSerializer(responseFormat);
            populateResponseForFormat(response, responseFormat,
                    serializer.primitive(serviceMetadata, edmPropertyType, property, options));
        }
    }

    @Override
    public void updatePrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Updating property");
    }

    @Override
    public void deletePrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Deleting property");
    }

    Property getPropertyFromEntity(String edmPropertyName, UriResourceEntitySet uriResourceEntitySet)
            throws ODataApplicationException {
        Entity entity = getEntityForKeys(uriResourceEntitySet);
        Property property = entity.getProperty(edmPropertyName);
        if (property == null) {
            throw new ODataApplicationException("Property not found", HttpStatusCode.NOT_FOUND.getStatusCode(),
                    Locale.ENGLISH);
        }
        return property;
    }

    void populateResponseForNull(ODataResponse response) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
}
