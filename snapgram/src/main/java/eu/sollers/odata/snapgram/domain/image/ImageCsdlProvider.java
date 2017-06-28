package eu.sollers.odata.snapgram.domain.image;

import io.github.mat3e.odata.common.exception.CsdlExtractException;
import io.github.mat3e.odata.common.provider.csdl.JpaEntityCsdlProvider;
import io.github.mat3e.odata.core.spring.annotation.CsdlProvider;

/**
 * Parses definition for Image entity.
 * Consumed by {@link eu.sollers.odata.snapgram.provider.ODataEdmProvider ODataEdmProvider}.
 */
@CsdlProvider
public class ImageCsdlProvider extends JpaEntityCsdlProvider<Image> {
    public ImageCsdlProvider() throws CsdlExtractException {
        super(Image.class);
    }
}
