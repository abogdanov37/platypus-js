package com.eas.client.form.published.widgets;

import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class PlatypusPasswordField extends PasswordTextBox implements HasPublished {

	protected JavaScriptObject published;
	
	public PlatypusPasswordField(){
		super();
	}
	
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusPasswordField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusPasswordField::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
	}-*/;
}
