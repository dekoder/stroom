package stroom.lmdb2;

import org.lmdbjava.Env;
import org.lmdbjava.Txn;

import java.nio.ByteBuffer;

public abstract class AbstractTxn implements AutoCloseable {

    final Env<ByteBuffer> env;
    final LmdbErrorHandler lmdbErrorHandler;
    private final Thread thread;

    AbstractTxn(final Env<ByteBuffer> env, final LmdbErrorHandler lmdbErrorHandler) {
        this.env = env;
        this.lmdbErrorHandler = lmdbErrorHandler;
        this.thread = Thread.currentThread();
    }

    void check() {
        if (thread != Thread.currentThread()) {
            throw new RuntimeException("Unexpected thread used. This will break LMDB.");
        }
        if (env.isClosed()) {
            throw new RuntimeException("Environment is closed. This will break LMDB.");
        }
    }

    abstract Txn<ByteBuffer> get();
}
