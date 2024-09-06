package stroom.item.client;

import stroom.svg.client.SvgIconBox;
import stroom.svg.shared.SvgImage;
import stroom.widget.util.client.SelectionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focus;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseSelectionBox<T, I extends SelectionItem>
        extends Composite
        implements SelectionBoxView<T, I>, Focus, HasValueChangeHandlers<T> {

    private final TextBox textBox;
    private final SvgIconBox svgIconBox;
    private SelectionListModel<T, I> model;
    private T value;
    private SelectionPopup<T, I> popup;
    private boolean allowTextEntry;

    private final EventBinder eventBinder = new EventBinder() {
        @Override
        protected void onBind() {
            registerHandler(textBox.addClickHandler(event -> onTextBoxClick()));
            registerHandler(svgIconBox.addClickHandler(event -> showPopup()));
            registerHandler(textBox.addKeyDownHandler(event -> {
                int keyCode = event.getNativeKeyCode();
                if (KeyCodes.KEY_ENTER == keyCode) {
                    showPopup();
                }
            }));
            registerHandler(textBox.addValueChangeHandler(event ->
                    ValueChangeEvent.fire(BaseSelectionBox.this, value)));
        }
    };

    public BaseSelectionBox() {
        textBox = new TextBox();
        textBox.addStyleName("SelectionBox-textBox stroom-control allow-focus");

        svgIconBox = new SvgIconBox();
        svgIconBox.addStyleName("SelectionBox");
        svgIconBox.setWidget(textBox, SvgImage.DROP_DOWN);

        initWidget(svgIconBox);
        setAllowTextEntry(false);
    }

    public void setAllowTextEntry(final boolean allowTextEntry) {
        this.allowTextEntry = allowTextEntry;
        textBox.setReadOnly(!allowTextEntry);
        if (allowTextEntry) {
            textBox.removeStyleName("pointer");
        } else {
            textBox.addStyleName("pointer");
        }
    }

    private void onTextBoxClick() {
        if (!allowTextEntry) {
            showPopup();
        }
    }

    @Override
    protected void onLoad() {
        eventBinder.bind();
    }

    @Override
    protected void onUnload() {
        eventBinder.unbind();
    }

    @Override
    public void setModel(final SelectionListModel<T, I> model) {
        this.model = model;
    }

    private void showPopup() {
        if (popup != null) {
            GWT.log("Hiding popup");
            hidePopup();

        } else {
            popup = new SelectionPopup<>();
            popup.init(model);
            popup.addAutoHidePartner(textBox.getElement());
            popup.addAutoHidePartner(svgIconBox.getElement());

            final I selectionItem = model.wrap(value);
            if (selectionItem != null) {
                popup.getSelectionModel().setSelected(selectionItem, true, new SelectionType(), false);
            }
            final List<HandlerRegistration> handlerRegistrations = new ArrayList<>();
            handlerRegistrations.add(popup.addCloseHandler(event -> {
                focus();
                for (final HandlerRegistration handlerRegistration : handlerRegistrations) {
                    handlerRegistration.removeHandler();
                }
                handlerRegistrations.clear();
                popup = null;
            }));
            handlerRegistrations.add(popup.getSelectionModel().addSelectionHandler(e -> {
                final I selected = popup.getSelectionModel().getSelected();
                if (selected == null) {
                    setValue(null, true);
                } else {
                    setValue(model.unwrap(selected), true);
                }
                hidePopup();
            }));

            popup.show(textBox.getElement());
        }
    }

    private void hidePopup() {
        popup.hide();
        popup = null;
    }

    @Override
    public void focus() {
        textBox.setFocus(true);
    }

    public void setName(final String name) {
        textBox.setName(name);
    }

    public void setEnabled(final boolean enabled) {
        textBox.setEnabled(enabled);
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        setValue(value, false);
    }

    public void setValue(final T value, final boolean fireEvents) {
        this.value = value;

        String currentText = textBox.getText();
        String newText = "";
        if (value != null) {
            newText = model.wrap(value).getLabel();
        }

        textBox.setValue(newText);

        if (fireEvents && !Objects.equals(currentText, newText)) {
            ValueChangeEvent.fire(this, value);
        }
    }

    public String getText() {
        return textBox.getText();
    }

    @Override
    public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(final ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
