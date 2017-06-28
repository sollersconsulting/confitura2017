package eu.sollers.odata.snapgram.domain.user;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

import eu.sollers.odata.snapgram.domain.image.Image;

import io.github.mat3e.odata.common.annotation.ODataEntity;
import io.github.mat3e.odata.common.annotation.ODataKey;
import io.github.mat3e.odata.common.annotation.ODataNavigationProperty;
import io.github.mat3e.odata.common.annotation.ODataProperty;
import io.github.mat3e.odata.common.entity.JpaOlingoEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Entity(name = "users")
@ODataEntity(name = "User", entitySetName = "Users")
public class User extends JpaOlingoEntity {

    @Id
    @Getter
    @Setter
    @ODataKey
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ODataProperty(name = "Id", type = EdmPrimitiveTypeKind.String, valueType = ValueType.PRIMITIVE)
    private String ID;

    @Getter
    @Setter
    @Column(name = "username", unique = true, nullable = false)
    @ODataProperty(name = "Username", type = EdmPrimitiveTypeKind.String, valueType = ValueType.PRIMITIVE)
    private String username;

    @Setter
    @Column(name = "password")
    private String password;

    @Getter
    @Setter
    @Column(name = "mail")
    @ODataProperty(name = "Email", type = EdmPrimitiveTypeKind.String, valueType = ValueType.PRIMITIVE)
    private String mail;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @ODataNavigationProperty(name = "Images")
    private List<Image> images;

    @Tolerate
    User() {
    }
}
