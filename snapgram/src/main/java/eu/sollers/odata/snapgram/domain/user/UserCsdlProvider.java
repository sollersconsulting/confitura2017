package eu.sollers.odata.snapgram.domain.user;

import io.github.mat3e.odata.common.exception.CsdlExtractException;
import io.github.mat3e.odata.common.provider.csdl.JpaEntityCsdlProvider;
import io.github.mat3e.odata.core.spring.annotation.CsdlProvider;

@CsdlProvider
public class UserCsdlProvider extends JpaEntityCsdlProvider<User> {
    public UserCsdlProvider() throws CsdlExtractException {
        super(User.class);
    }
}
