package stroom.pipeline.refdata;

import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;

import java.nio.ByteBuffer;

public interface RefDataValueByteBufferConsumer {

    void consumeBytes(final ByteBuffer byteBuffer);


    // --------------------------------------------------------------------------------


    interface Factory {

        RefDataValueByteBufferConsumer create(
                final Receiver receiver,
                final PipelineConfiguration pipelineConfiguration);
    }

}
