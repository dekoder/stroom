package stroom.planb.impl.serde.session;

import stroom.bytebuffer.impl6.ByteBuffers;
import stroom.planb.impl.db.session.Session;
import stroom.planb.impl.serde.keyprefix.KeyPrefix;
import stroom.planb.impl.serde.time.TimeSerde;
import stroom.query.language.functions.Val;

import org.lmdbjava.Txn;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SimpleSessionSerde implements SessionSerde {

    private final ByteBuffer reusableWriteBuffer;
    private final ByteBuffers byteBuffers;
    private final TimeSerde timeSerde;
    private final int length;

    public SimpleSessionSerde(final ByteBuffers byteBuffers,
                              final TimeSerde timeSerde,
                              final int prefixLength) {
        this.byteBuffers = byteBuffers;
        this.timeSerde = timeSerde;
        length = prefixLength + timeSerde.getSize() + timeSerde.getSize();
        reusableWriteBuffer = ByteBuffer.allocateDirect(length);
    }

    abstract Val readPrefix(final Txn<ByteBuffer> txn, final ByteBuffer byteBuffer);

    abstract void writePrefix(final Val val, final ByteBuffer byteBuffer);

    @Override
    public Session read(final Txn<ByteBuffer> txn, final ByteBuffer byteBuffer) {
        final Val val = readPrefix(txn, byteBuffer);
        final Instant start = timeSerde.read(byteBuffer);
        final Instant end = timeSerde.read(byteBuffer);
        return new Session(KeyPrefix.create(val), start, end);
    }

    @Override
    public void write(final Txn<ByteBuffer> txn, final Session session, final Consumer<ByteBuffer> consumer) {
        // We are in a single write transaction so should be able to reuse the same buffer again and again.
        reusableWriteBuffer.clear();
        writePrefix(session.getPrefix().getVal(), reusableWriteBuffer);
        timeSerde.write(reusableWriteBuffer, session.getStart());
        timeSerde.write(reusableWriteBuffer, session.getEnd());
        reusableWriteBuffer.flip();
        consumer.accept(reusableWriteBuffer);
    }

    @Override
    public <R> R toBufferForGet(final Txn<ByteBuffer> txn,
                                final Session session,
                                final Function<Optional<ByteBuffer>, R> function) {
        return byteBuffers.use(length, byteBuffer -> {
            writePrefix(session.getPrefix().getVal(), byteBuffer);
            timeSerde.write(byteBuffer, session.getStart());
            timeSerde.write(byteBuffer, session.getEnd());
            byteBuffer.flip();
            return function.apply(Optional.of(byteBuffer));
        });
    }
}
