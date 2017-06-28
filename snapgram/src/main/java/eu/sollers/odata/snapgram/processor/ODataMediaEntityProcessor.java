package eu.sollers.odata.snapgram.processor;

import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.springframework.transaction.annotation.Transactional;

import io.github.mat3e.odata.common.entity.JpaOlingoMediaEntity;
import io.github.mat3e.odata.core.spring.annotation.OlingoProcessor;

@OlingoProcessor
public class ODataMediaEntityProcessor extends ODataEntityProcessor implements MediaEntityProcessor {
    @Override
    @Transactional
    public void readMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

        List<UriResource> parts = uriInfo.getUriResourceParts();
        int segmentCount = parts.size();
        UriResourceEntitySet uriResourceEntitySet = getUriResourceEntitySetFromUriResources(0, parts);
        Entity startEntity = getEntityForKeys(uriResourceEntitySet);

        JpaOlingoMediaEntity mediaEntity = null;

        if (segmentCount == 2) { // Images(1)/$value
            mediaEntity = (JpaOlingoMediaEntity) startEntity;
        } else if (segmentCount == 3) { // Users('2')/Images(3)/$value
            UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) parts.get(1);
            EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();

            String navName = edmNavigationProperty.getName();

            List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();
            if (navKeyPredicates.isEmpty()) {
                mediaEntity = (JpaOlingoMediaEntity) startEntity.getNavigationLink(navName).getInlineEntity();
            } else {
                mediaEntity = (JpaOlingoMediaEntity) startEntity.getNavigationLink(navName).getInlineEntitySet()
                                                                .getEntities().stream()
                                                                .filter(ent -> navKeyPredicates.stream().allMatch(
                                                                        key -> ent.getProperty(key.getName()).getValue()
                                                                                  .toString()
                                                                                  .equals(getUriParameterValue(key)
                                                                                          .toString()))).findFirst()
                                                                .orElse(null);
            }
        } else {
            throwTooManySegments();
        }

        assertNotNull(mediaEntity);

        final byte[] mediaContent = mediaEntity.getContent();
        populateResponseForFormat(response, mediaEntity.getMediaContentType(),
                odata.createFixedFormatSerializer().binary(mediaContent));
    }

    @Override
    public void createMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Creating media entity");
    }

    @Override
    public void updateMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Updating media entity");
    }

    @Override
    public void deleteMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
            throws ODataApplicationException, ODataLibraryException {
        throwNotImplemented("Deleting media entity");
    }
}
