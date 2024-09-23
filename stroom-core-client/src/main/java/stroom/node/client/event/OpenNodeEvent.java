package stroom.node.client.event;

import stroom.job.shared.JobNode;
import stroom.node.client.event.OpenNodeEvent.OpenNodeHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

import java.util.Objects;

public class OpenNodeEvent extends GwtEvent<OpenNodeHandler> {

    private static Type<OpenNodeHandler> TYPE;
    private final String nodeName;
    private final JobNode jobNode;

    private OpenNodeEvent(final String nodeName) {
        Objects.requireNonNull(nodeName);
        this.nodeName = nodeName;
        this.jobNode = null;
    }

    private OpenNodeEvent(final JobNode jobNode) {
        Objects.requireNonNull(jobNode);
        this.nodeName = jobNode.getNodeName();
        this.jobNode = jobNode;
    }

    /**
     * Open the named node on the nodes screen
     */
    public static void fire(final HasHandlers handlers, final String nodeName) {
        handlers.fireEvent(new OpenNodeEvent(nodeName));
    }

    /**
     * Open the job and node on the nodes screen
     */
    public static void fire(final HasHandlers handlers, final JobNode jobNode) {
        handlers.fireEvent(new OpenNodeEvent(jobNode));
    }

    public static Type<OpenNodeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @Override
    public Type<OpenNodeHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(final OpenNodeHandler handler) {
        handler.onOpen(this);
    }

    public String getNodeName() {
        return nodeName;
    }

    public JobNode getJobNode() {
        return jobNode;
    }

    @Override
    public String toString() {
        return "OpenNodeEvent{" +
                "nodeName='" + nodeName + '\'' +
                ", jobNode=" + jobNode +
                '}';
    }


    // --------------------------------------------------------------------------------


    public interface OpenNodeHandler extends EventHandler {

        void onOpen(OpenNodeEvent event);
    }
}
