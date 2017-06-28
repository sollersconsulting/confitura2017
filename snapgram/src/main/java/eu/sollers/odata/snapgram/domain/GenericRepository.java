package eu.sollers.odata.snapgram.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;

import eu.sollers.odata.snapgram.processor.FilterExpressionVisitor;

import io.github.mat3e.odata.common.provider.csdl.CsdlProvider;
import io.github.mat3e.odata.common.provider.csdl.JpaEntityCsdlProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper for working with the database in a general way, for all the entities.
 */
@Slf4j
@Repository
public class GenericRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private List<CsdlProvider> providers;

    private Repositories repositories = null;

    @Autowired
    public GenericRepository(WebApplicationContext appContext) {
        repositories = new Repositories(appContext);
    }

    /**
     * Gets a repository dynamically, based on its OData type.
     *
     * @param fqn
     *         OData identifier of the entity
     * @return repository for the entity
     */
    public JpaRepository<Entity, Serializable> getRepository(FullQualifiedName fqn) {
        JpaEntityCsdlProvider jpaProvider = (JpaEntityCsdlProvider) providers.stream().filter(provider ->
                provider instanceof JpaEntityCsdlProvider && provider.getFQN().equals(fqn)).findFirst().get();

        return getRepository(jpaProvider.getBackingClass());
    }

    /**
     * Gets a repository dynamically, based on Java class.
     *
     * @param javaClass
     *         class of the entity
     * @return repository for the entity
     */
    public JpaRepository<Entity, Serializable> getRepository(Class<?> javaClass) {
        return (JpaRepository<Entity, Serializable>) repositories.getRepositoryFor(javaClass);
    }

    /**
     * Skips the repository and executes Query directly.
     *
     * @param filter
     *         filter query param from uri info
     * @param edmEntityType
     *         type to be taken into account
     * @return all the entities matching the given criteria
     */
    public List<Entity> executeQueryForFilterAndEdmEntity(FilterOption filter, EdmEntityType edmEntityType)
            throws ODataApplicationException, ExpressionVisitException {
        JpaEntityCsdlProvider entityProvider = (JpaEntityCsdlProvider) getProviderForName(edmEntityType.getName());
        String entityName = entityProvider.getBackingClass().getSimpleName();
        String entityAlias = entityName.toLowerCase();

        String sqlCriteria = filter.getExpression().accept(new FilterExpressionVisitor(entityProvider));
        String fullSQL = "SELECT " + entityAlias + " FROM " + entityName + " " + entityAlias + " WHERE " + sqlCriteria;

        log.debug("Executed HQL: " + fullSQL);
        return (List<Entity>) em.createQuery(fullSQL).getResultList();
    }

    private CsdlProvider getProviderForName(String name) {
        return providers.stream().filter(p -> p.getCsdlEntityType().getName().equals(name)).findFirst().get();
    }
}
