package stroom.query.language.functions;

import java.time.Instant;

public interface StateProvider {

    Val getState(String map, String key, Instant effectiveTimeMs);
}
