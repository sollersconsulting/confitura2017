package eu.sollers.odata.snapgram.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;

import io.github.mat3e.odata.common.provider.csdl.CsdlProvider;
import io.github.mat3e.odata.common.provider.csdl.JpaEntityCsdlProvider;

/**
 * Helper for working with the database in a general way, for all the entities.
 */
@Repository
public class GenericRepository {
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
}
