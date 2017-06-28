package eu.sollers.odata.snapgram.domain.category;

import io.github.mat3e.odata.common.exception.CsdlExtractException;
import io.github.mat3e.odata.common.provider.csdl.JpaEntityCsdlProvider;
import io.github.mat3e.odata.core.spring.annotation.CsdlProvider;

@CsdlProvider
public class CategoryCsdlProvider extends JpaEntityCsdlProvider<Category> {
    public CategoryCsdlProvider() throws CsdlExtractException {
        super(Category.class);
    }
}
