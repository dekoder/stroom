package stroom.pipeline.refdata.store.offheapstore;

import java.util.Objects;


/**
 * < typeId >< referenceCount >
 * < 1 byte >< 3 bytes >
 * <p>
 * referenceCount stored as a 3 byte unsigned integer so a max
 * of ~16 million.
 */
public class ValueStoreMeta {

    private final byte typeId;
    private final int referenceCount;

    public ValueStoreMeta(byte typeId) {
        this.typeId = typeId;
        this.referenceCount = 1;
    }

    public ValueStoreMeta(byte typeId, int referenceCount) {
        this.typeId = typeId;
        this.referenceCount = referenceCount;
    }

    public byte getTypeId() {
        return typeId;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValueStoreMeta that = (ValueStoreMeta) o;
        return typeId == that.typeId &&
                referenceCount == that.referenceCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId, referenceCount);
    }

    @Override
    public String toString() {
        return "ValueStoreMeta{" +
                "typeId=" + typeId +
                ", referenceCount=" + referenceCount +
                '}';
    }
}
