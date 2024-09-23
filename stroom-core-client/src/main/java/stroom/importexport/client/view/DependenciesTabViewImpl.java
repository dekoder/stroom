package stroom.importexport.client.view;

import stroom.importexport.client.presenter.DependenciesTabPresenter;
import stroom.importexport.client.presenter.DependenciesTabPresenter.DependenciesTabView;
import stroom.importexport.client.presenter.DependenciesUiHandlers;
import stroom.widget.dropdowntree.client.view.QuickFilter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class DependenciesTabViewImpl
        extends ViewWithUiHandlers<DependenciesUiHandlers>
        implements DependenciesTabView {

    @UiField
    QuickFilter quickFilter;
    @UiField
    SimplePanel dataGrid;

    private final Widget widget;

    @Inject
    public DependenciesTabViewImpl(final EventBus eventBus, final Binder binder) {
        widget = binder.createAndBindUi(this);
    }

//    @Override
//    public void setUiHandlers(final DependenciesUiHandlers uiHandlers) {
//
//    }

    @Override
    public void addToSlot(final Object slot, final Widget content) {

    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void removeFromSlot(final Object slot, final Widget content) {

    }

    @Override
    public void setInSlot(final Object slot, final Widget content) {
        if (DependenciesTabPresenter.LIST.equals(slot)) {
            dataGrid.setWidget(content);
        }
    }

    @Override
    public void setHelpTooltipText(final SafeHtml helpTooltipText) {
        quickFilter.registerPopupTextProvider(() -> helpTooltipText);
    }

    @Override
    public void setQuickFilterText(final String text) {
        quickFilter.setText(text);
        getUiHandlers().changeQuickFilter(text);
    }

    @Override
    public void focusFilter() {
        quickFilter.focus();
    }

    @UiHandler("quickFilter")
    void onFilterChange(final ValueChangeEvent<String> event) {
        getUiHandlers().changeQuickFilter(event.getValue());
    }


    // --------------------------------------------------------------------------------


    public interface Binder extends UiBinder<Widget, DependenciesTabViewImpl> {

    }
}
