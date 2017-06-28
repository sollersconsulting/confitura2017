package eu.sollers.odata.snapgram.domain.category;

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
@Entity(name = "categories")
@ODataEntity(name = "Category", entitySetName = "Categories")
public class Category extends JpaOlingoEntity {

    @Id
    @Getter
    @Setter
    @ODataKey
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ODataProperty(name = "Id", type = EdmPrimitiveTypeKind.Int64, valueType = ValueType.PRIMITIVE)
    private Long ID;

    @Getter
    @Setter
    @Column(name = "name", unique = true, nullable = false)
    @ODataProperty(name = "Name", type = EdmPrimitiveTypeKind.String, valueType = ValueType.PRIMITIVE)
    private String name;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    @ODataNavigationProperty(name = "Images")
    private List<Image> images;

    @Tolerate
    Category() {
    }
}
