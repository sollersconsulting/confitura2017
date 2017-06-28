package eu.sollers.odata.snapgram.processor;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.springframework.transaction.annotation.Transactional;

import io.github.mat3e.odata.core.spring.annotation.OlingoProcessor;

@OlingoProcessor
public class ODataEntityProcessor extends ODataBaseProcessor implements EntityProcessor {

    @Override
    @Transactional
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        int segmentCount = parts.size();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(0, parts);
        Entity startEntity = getEntityForKeys(uriResourceEntitySet);
        EdmEntitySet edmEntitySet = null;
        EdmEntityType edmEntityType = null;

        Entity responseEntity = null;

        if (segmentCount == 1) {
            edmEntitySet = uriResourceEntitySet.getEntitySet();
            edmEntityType = edmEntitySet.getEntityType();
            responseEntity = startEntity;
        } else if (segmentCount == 2) {
            UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) parts.get(1);
            EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
            edmEntityType = edmNavigationProperty.getType();

            String navName = edmNavigationProperty.getName();
            EdmBindingTarget target = uriResourceEntitySet.getEntitySet().getRelatedBindingTarget(navName);
            if (target instanceof EdmEntitySet) {
                edmEntitySet = (EdmEntitySet) target;
            } else {
                throw new ODataApplicationException("Singletons not supported",
                        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
            }

            List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();
            if (navKeyPredicates.isEmpty()) { // e.g. Images(1)/User
                responseEntity = startEntity.getNavigationLink(navName).getInlineEntity();
            } else { // e.g. Users('2')/Image(1)
                responseEntity = startEntity.getNavigationLink(navName).getInlineEntitySet().getEntities().stream()
                                            .filter(ent -> navKeyPredicates.stream().allMatch(
                                                    key -> ent.getProperty(key.getName()).getValue().toString()
                                                              .equals(getUriParameterValue(key).toString())))
                                            .findFirst().orElse(null);
            }
        } else {
            throwTooManySegments();
        }

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
