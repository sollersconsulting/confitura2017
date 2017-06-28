package eu.sollers.odata.snapgram.domain.image;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ContentType;

import eu.sollers.odata.snapgram.domain.category.Category;
import eu.sollers.odata.snapgram.domain.user.User;

import io.github.mat3e.odata.common.annotation.ODataEntity;
import io.github.mat3e.odata.common.annotation.ODataKey;
import io.github.mat3e.odata.common.annotation.ODataNavigationProperty;
import io.github.mat3e.odata.common.annotation.ODataProperty;
import io.github.mat3e.odata.common.entity.JpaOlingoMediaEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

/**
 * Media entity for storing images.
 */
@Builder
@Entity(name = "images")
@ODataEntity(name = "Image", entitySetName = "Images")
public class Image extends JpaOlingoMediaEntity {
    public static final ContentType PNG = ContentType.parse("image/png");
    public static final ContentType GIF = ContentType.parse("image/gif");
    public static final ContentType JPEG = ContentType.parse("image/jpeg");

    private static final ContentType[] ALLOWED_TYPES = { PNG, GIF, JPEG };

    @Id
    @Getter
    @Setter
    @ODataKey
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ODataProperty(name = "Id", type = EdmPrimitiveTypeKind.Int64)
    private Long ID;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @ODataNavigationProperty(name = "User")
    private User user;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @ODataNavigationProperty(name = "Category")
    private Category category;

    @Getter
    @Setter
    @Column(name = "name")
    @ODataProperty(name = "Name", type = EdmPrimitiveTypeKind.String)
    private String name;

    @Getter
    @Setter
    @Column(name = "desc")
    @ODataProperty(name = "Description", type = EdmPrimitiveTypeKind.String)
    private String description;

    @Getter
    @Setter
    @Column(name = "is_private")
    @ODataProperty(name = "IsPrivate", type = EdmPrimitiveTypeKind.Boolean)
    private Boolean isPrivate;

    /**
     * Bytes which represent the image.
     */
    @Lob
    @Column(name = "bytes", columnDefinition = "mediumblob")
    private byte[] $value;

    @Getter
    @Setter
    @Column(name = "width")
    @ODataProperty(name = "Width", type = EdmPrimitiveTypeKind.Int32)
    private Integer width;

    @Getter
    @Setter
    @Column(name = "height")
    @ODataProperty(name = "Height", type = EdmPrimitiveTypeKind.Int32)
    private Integer height;

    @Tolerate
    Image() {
    }

    @Override
    public byte[] getContent() {
        // return (byte[]) getProperty(MEDIA_PROPERTY_NAME).asPrimitive();
        return $value;
    }

    @Override
    public void setContent(byte[] data) {
        $value = data;
        this.getProperties().remove(this.getProperty(MEDIA_PROPERTY_NAME));
        this.addProperty(new Property(null, MEDIA_PROPERTY_NAME, ValueType.PRIMITIVE, data));
    }

    @Override
    public void setMediaContentType(String contentType) {
        for (ContentType ct : ALLOWED_TYPES) {
            if (contentType.equals(ct.toString())) {
                super.setMediaContentType(contentType);
                return;
            }
        }

        throw new IllegalArgumentException("Wrong image content type: " + contentType);
    }
}
