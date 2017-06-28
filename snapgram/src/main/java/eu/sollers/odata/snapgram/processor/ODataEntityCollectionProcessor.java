package eu.sollers.odata.snapgram.processor;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
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
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.springframework.transaction.annotation.Transactional;

import io.github.mat3e.odata.core.spring.annotation.OlingoProcessor;

@OlingoProcessor
public class ODataEntityCollectionProcessor extends ODataBaseProcessor implements EntityCollectionProcessor {

    @Override
    @Transactional
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        int segmentCount = parts.size();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(0, parts);
        EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntitySet edmEntitySet = null;
        EdmEntityType edmEntityType = null;

        EntityCollection entityCollection = new EntityCollection();
        List<Entity> entitiesGot = null;

        if (segmentCount == 1) {
            FilterOption filter = uriInfo.getFilterOption();

            edmEntitySet = startEdmEntitySet;
            edmEntityType = edmEntitySet.getEntityType();

            if (filter == null) {
                entitiesGot = getRepository(edmEntityType.getFullQualifiedName()).findAll();
            } else {
                try {
                    entitiesGot = repos.executeQueryForFilterAndEdmEntity(filter, edmEntityType);
                } catch (ExpressionVisitException e) {
                    throw new ODataApplicationException(e.getMessage(), HttpStatusCode.BAD_REQUEST.getStatusCode(),
                            Locale.ROOT);
                }
            }

            entityCollection.getEntities().addAll(entitiesGot);
        } else if (segmentCount == 2) {
            if (uriInfo.getFilterOption() != null) {
                throwFilterNotImplemented();
            }

            UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) parts.get(1);
            String propName = uriResourceNavigation.getProperty().getName();

            edmEntitySet = (EdmEntitySet) startEdmEntitySet.getRelatedBindingTarget(propName);
            edmEntityType = edmEntitySet.getEntityType();

            Entity startEntity = getEntityForKeys(uriResourceEntitySet);
            entitiesGot = startEntity.getNavigationLink(propName).getInlineEntitySet().getEntities();
            entityCollection.getEntities().addAll(entitiesGot);
        } else {
            throwTooManySegments();
        }

        // $count
        CountOption countOption = uriInfo.getCountOption();
        if (countOption != null && countOption.getValue()) {
            entityCollection.setCount(entitiesGot.size());
        }

        // $expand
        ExpandOption expandOption = uriInfo.getExpandOption();
        if (expandOption != null && expandOption.getExpandItems().stream()
                                                .anyMatch(expand -> expand.getFilterOption() != null)) {
            throwFilterNotImplemented();
        }

        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet)
                                          .selectList(getSelectList(uriInfo, edmEntityType)).build();
        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions.with().id(id).count(countOption)
                                                                                     .select(uriInfo.getSelectOption())
                                                                                     .expand(expandOption)
                                                                                     .contextURL(contextUrl).build();

        ODataSerializer serializer = odata.createSerializer(responseFormat);
        populateResponseForFormat(response, responseFormat,
                serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, options));
    }

    private void throwFilterNotImplemented() throws ODataApplicationException {
        throwNotImplemented("$filter on nested levels");
    }
}
