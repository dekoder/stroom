package stroom.pipeline.refdata.store;

import stroom.pipeline.refdata.RefDataValueByteBufferConsumer;

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;

public class ByteBufferConsumerBinder {

    private final MapBinder<ByteBufferConsumerId, RefDataValueByteBufferConsumer.Factory> mapBinder;

    private ByteBufferConsumerBinder(final Binder binder) {
        mapBinder = MapBinder.newMapBinder(binder,
                ByteBufferConsumerId.class,
                RefDataValueByteBufferConsumer.Factory.class);
    }

    public static ByteBufferConsumerBinder create(final Binder binder) {
        return new ByteBufferConsumerBinder(binder);
    }

    public <F extends RefDataValueByteBufferConsumer.Factory> ByteBufferConsumerBinder bind(final byte id,
                                                                                            final Class<F> handler) {
        mapBinder.addBinding(new ByteBufferConsumerId(id)).to(handler);
        return this;
    }
}
