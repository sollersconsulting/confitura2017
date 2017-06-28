package eu.sollers.odata.snapgram.processor;

import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.transaction.annotation.Transactional;

import io.github.mat3e.odata.core.spring.annotation.OlingoProcessor;

@OlingoProcessor
public class ODataEntityProcessor extends ODataBaseProcessor implements EntityProcessor {

    @Override
    @Transactional
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(0, parts);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        Entity responseEntity = getEntityForKeys(uriResourceEntitySet);
        assertNotNull(responseEntity);

        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).suffix(ContextURL.Suffix.ENTITY).build();
        EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

        ODataSerializer serializer = odata.createSerializer(responseFormat);
        populateResponseForFormat(response, responseFormat,
                serializer.entity(serviceMetadata, edmEntityType, responseEntity, options));
    }

    @Override
    public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Creating an entity");
    }

    @Override
    public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Updating an entity");
    }

    @Override
    public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Deleting an entity");
    }
}
