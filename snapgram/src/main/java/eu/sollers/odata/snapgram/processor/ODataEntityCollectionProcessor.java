package eu.sollers.odata.snapgram.processor;

import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.transaction.annotation.Transactional;

import io.github.mat3e.odata.core.spring.annotation.OlingoProcessor;

@OlingoProcessor
public class ODataEntityCollectionProcessor extends ODataBaseProcessor implements EntityCollectionProcessor {

    @Override
    @Transactional
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(0, parts);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        // repo call
        EntityCollection entityCollection = new EntityCollection();
        List<Entity> entries = getRepository(edmEntityType.getFullQualifiedName()).findAll();
        entityCollection.getEntities().addAll(entries);

        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions.with().id(id)
                                                                                     .contextURL(contextUrl).build();

        ODataSerializer serializer = odata.createSerializer(responseFormat);
        populateResponseForFormat(response, responseFormat,
                serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, options));
    }
}
