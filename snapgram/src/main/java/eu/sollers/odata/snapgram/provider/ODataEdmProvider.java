package eu.sollers.odata.snapgram.provider;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.mat3e.odata.common.provider.AbstractEdmProvider;
import io.github.mat3e.odata.common.provider.csdl.CsdlProvider;

/**
 * Magic which builds OData main endpoints, models, etc.
 *
 * @see <a href="http://docs.oasis-open.org/odata/odata/v4.0/errata03/os/complete/part1-protocol/odata-v4.0-errata03-os-part1-protocol-complete.html#_Toc453752278">Data
 * Service Requests</a>
 */
@Service
public class ODataEdmProvider extends AbstractEdmProvider {
    @Autowired
    public ODataEdmProvider(List<CsdlProvider> providers) {
        super(providers);
    }

    @Override
    protected List<CsdlActionImport> getActionImports() {
        return Collections.emptyList();
    }

    @Override
    protected List<CsdlFunctionImport> getFunctionImports() {
        return Collections.emptyList();
    }
}
