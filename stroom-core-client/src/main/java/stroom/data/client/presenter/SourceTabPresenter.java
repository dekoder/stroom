package stroom.data.client.presenter;

import stroom.content.client.presenter.ContentTabPresenter;
import stroom.data.client.SourceKey;
import stroom.data.client.presenter.SourceTabPresenter.SourceTabView;
import stroom.pipeline.shared.SourceLocation;
import stroom.svg.shared.SvgImage;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.View;

public class SourceTabPresenter extends ContentTabPresenter<SourceTabView> {

    public static final String TAB_TYPE = "DataSource";
    private final SourcePresenter sourcePresenter;
    private SourceKey sourceKey;

    @Inject
    public SourceTabPresenter(final EventBus eventBus,
                              final SourcePresenter sourcePresenter,
                              final SourceTabView view) {
        super(eventBus, view);
        this.sourcePresenter = sourcePresenter;

        getView().setSourceView(sourcePresenter.getView());
    }

    @Override
    protected void onBind() {
    }

    @Override
    public SvgImage getIcon() {
        return SvgImage.FILE_RAW;
    }

    @Override
    public String getLabel() {
        final String type = sourceKey.getOptChildStreamType()
                .map(val -> " (" + val + ")")
                .orElse("");
        return sourceKey != null
                ? ("Stream " + sourceKey.getMetaId()
                + " : " + (sourceKey.getPartIndex() + 1) // to one based
                + " : " + (sourceKey.getRecordIndex().orElse(0L) + 1) // to one based
                + type)
                : "Source Data";
    }

    public void setSourceLocationUsingHighlight(final SourceLocation sourceLocation) {
        sourceKey = new SourceKey(sourceLocation);
        sourcePresenter.setSourceLocationUsingHighlight(sourceLocation);
    }

    public void setSourceLocation(final SourceLocation sourceLocation) {
        sourceKey = new SourceKey(sourceLocation);
        sourcePresenter.setSourceLocation(sourceLocation);
    }

//    public void setSourceKey(final SourceKey sourceKey) {
//        this.sourceKey = sourceKey;
//
//        sourcePresenter.setSourceLocation(SourceLocation.builder(sourceKey.getMetaId())
//                .withChildStreamType(sourceKey.getOptChildStreamType().orElse(null))
//                .build());
//    }

    @Override
    public String getType() {
        return TAB_TYPE;
    }


    // --------------------------------------------------------------------------------


    public interface SourceTabView extends View {

        void setSourceView(final View sourceView);
    }

}
