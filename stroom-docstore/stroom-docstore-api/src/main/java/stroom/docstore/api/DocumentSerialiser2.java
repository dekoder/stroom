package stroom.docstore.api;

import java.io.IOException;
import java.util.Map;

public interface DocumentSerialiser2<D> {

    D read(Map<String, byte[]> data) throws IOException;

    Map<String, byte[]> write(D document) throws IOException;
}

