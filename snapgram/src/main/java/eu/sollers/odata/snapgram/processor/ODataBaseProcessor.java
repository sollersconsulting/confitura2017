package eu.sollers.odata.snapgram.processor;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import eu.sollers.odata.snapgram.domain.GenericRepository;

/**
 * Base for all the processors.
 */
public abstract class ODataBaseProcessor implements Processor {

    @Autowired
    protected GenericRepository repos;

    protected OData odata;
    protected ServiceMetadata serviceMetadata;

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    UriResourceEntitySet getUriResourceEntitySetFromUriResources(int index, List<UriResource> resources)
            throws ODataApplicationException {
        UriResource uriResource = resources.get(index);
        if (!(uriResource instanceof UriResourceEntitySet)) {
            throw new ODataApplicationException(
                    uriResource.getSegmentValue() + " is not an EntitySet, which may be used here",
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
        }
        return (UriResourceEntitySet) uriResource;
    }

    Entity getEntityForKeys(UriResourceEntitySet resourceEntitySet) throws ODataApplicationException {
        List<UriParameter> keyPredicates = resourceEntitySet.getKeyPredicates();

        Entity entity = null;
        // no support for multiple keys
        if (keyPredicates.size() == 1) {
            entity = getRepository(resourceEntitySet.getEntityType().getFullQualifiedName())
                    .findOne(getUriParameterValue(keyPredicates.get(0)));
        }
        if (entity == null) { // Bad request
            throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(),
                    Locale.ENGLISH);
        }

        return entity;
    }

    JpaRepository<Entity, Serializable> getRepository(FullQualifiedName fqn) {
        return repos.getRepository(fqn);
    }

    void populateResponseForFormat(ODataResponse response, ContentType responseFormat,
            SerializerResult serializerResult) {
        populateResponseForFormat(response, responseFormat.toContentTypeString(), serializerResult.getContent());
    }

    void populateResponseForFormat(ODataResponse response, String format, InputStream result) {
        response.setContent(result);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, format);
    }

    /**
     * UriParameter is a key, so it is in CrudRepository as Serializable.
     * TODO: for now just string or long
     */
    Serializable getUriParameterValue(UriParameter param) {
        Serializable result = param.getText();

        String temp = result.toString();
        if (temp.startsWith("'")) {
            result = temp.substring(1, temp.length() - 1);
        } else {
            result = Long.valueOf(temp);
        }

        return result;
    }

    void assertNotNull(Entity entity) throws ODataApplicationException {
        if (entity == null) {
            throw new ODataApplicationException("Nothing found", HttpStatusCode.NOT_FOUND.getStatusCode(),
                    Locale.ROOT);
        }
    }

    void throwNotImplemented(String actionName) throws ODataApplicationException {
        throw new ODataApplicationException(actionName + " is not supported",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    void throwTooManySegments() throws ODataApplicationException {
        throw new ODataApplicationException("Too many navigation properties in URL (maximum 2 entities allowed)",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

}
