package stroom.pipeline.refdata.store;

import java.io.Serializable;
import java.util.Objects;

public class ValueConsumerId implements Serializable {

    private final byte id;

    public ValueConsumerId(final byte id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ValueConsumerId valueConsumerId = (ValueConsumerId) o;
        return Objects.equals(id, valueConsumerId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return Byte.toString(id);
    }
}
