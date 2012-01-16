
package com.ponysdk.ui.server.list.renderer.header;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ponysdk.ui.server.addon.PAttachedPopupPanel;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PHorizontalPanel;
import com.ponysdk.ui.server.basic.PImage;
import com.ponysdk.ui.server.basic.PLabel;
import com.ponysdk.ui.server.basic.PVerticalPanel;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;
import com.ponysdk.ui.server.basic.event.PCloseHandler;
import com.ponysdk.ui.server.basic.event.PKeyUpEvent;
import com.ponysdk.ui.server.basic.event.PKeyUpFilterHandler;
import com.ponysdk.ui.server.basic.event.PValueChangeHandler;
import com.ponysdk.ui.server.form.FormField;
import com.ponysdk.ui.server.form.renderer.DateBoxFormFieldRenderer;
import com.ponysdk.ui.server.form.renderer.TextBoxFormFieldRenderer;
import com.ponysdk.ui.server.list.event.RefreshListEvent;
import com.ponysdk.ui.terminal.basic.PVerticalAlignment;

public class DateRangeHeaderCellRenderer extends ComplexHeaderCellRenderer implements PClickHandler {

    final PHorizontalPanel fieldContainer = new PHorizontalPanel();

    private PVerticalPanel popupContent;

    private FormField from;

    private FormField to;

    private TextBoxFormFieldRenderer mainformFieldRenderer;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public DateRangeHeaderCellRenderer(String caption, final FormField from, final FormField to, String pojoProperty) {
        super(caption, pojoProperty);

        final PImage plusAnchor = new PImage("images/plus_20.png"); // must be a parameter ?
        plusAnchor.setHeight("16px");
        plusAnchor.setWidth("16px");

        mainformFieldRenderer = (TextBoxFormFieldRenderer) formField.getFormFieldRenderer();
        mainformFieldRenderer.addClickHandler(this);
        mainformFieldRenderer.setEnabled(false);

        this.from = from;
        this.to = to;

        popupContent = new PVerticalPanel();
        popupContent.add(new PLabel("from"));
        popupContent.add(from.render().asWidget());
        popupContent.add(new PLabel("to"));
        popupContent.add(to.render().asWidget());
        PKeyUpFilterHandler handler = new PKeyUpFilterHandler(KEY_ENTER) {

            @Override
            public void onKeyUp(int keyCode) {
                if (keyCode != KEY_ENTER) return;
                final RefreshListEvent refreshListEvent = new RefreshListEvent(this, from);
                eventBus.fireEvent(refreshListEvent);
            }
        };
        from.addDomHandler(handler, PKeyUpEvent.TYPE);
        to.addDomHandler(handler, PKeyUpEvent.TYPE);

        DateBoxFormFieldRenderer fromRenderer = (DateBoxFormFieldRenderer) from.getFormFieldRenderer();
        fromRenderer.addValueChangeHandler(new PValueChangeHandler<Date>() {

            @Override
            public void onValueChange(Date value) {
                updateMainFormField();
            }

        });

        DateBoxFormFieldRenderer toRenderer = (DateBoxFormFieldRenderer) to.getFormFieldRenderer();
        toRenderer.addValueChangeHandler(new PValueChangeHandler<Date>() {

            @Override
            public void onValueChange(Date value) {
                updateMainFormField();
            }
        });

        plusAnchor.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(PClickEvent arg0) {
                showPopup();
            }

        });

        fieldContainer.add(container);
        fieldContainer.add(plusAnchor);
        fieldContainer.setCellVerticalAlignment(plusAnchor, PVerticalAlignment.ALIGN_BOTTOM);
    }

    @Override
    public IsPWidget render() {
        return fieldContainer;
    }

    protected void updateMainFormField() {
        String fromFormat = from.getValue() == null ? null : dateFormat.format(from.getValue());
        String toFormat = to.getValue() == null ? null : dateFormat.format(to.getValue());

        if (fromFormat == null && toFormat == null) mainformFieldRenderer.reset();
        else if (fromFormat == null) mainformFieldRenderer.setText("<= " + toFormat);
        else if (toFormat == null) mainformFieldRenderer.setText(">= " + fromFormat);
        else mainformFieldRenderer.setText(fromFormat + " - " + toFormat);
    }

    protected void showPopup() {
        final PAttachedPopupPanel levelPopupPanel = new PAttachedPopupPanel(true, fieldContainer);
        levelPopupPanel.setWidget(popupContent);
        levelPopupPanel.show();

        levelPopupPanel.addCloseHandler(new PCloseHandler() {

            @Override
            public void onClose() {
                updateMainFormField();
            }
        });

    }

    @Override
    public void onClick(PClickEvent event) {
        showPopup();
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}