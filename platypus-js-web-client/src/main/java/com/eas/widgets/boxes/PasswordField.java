package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class PasswordField extends TextField {

    public PasswordField() {
        super(Document.get().createPasswordInputElement());
    }

}