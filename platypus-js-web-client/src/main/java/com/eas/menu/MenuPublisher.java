package com.eas.menu;

import com.eas.core.Utils;
import com.eas.ui.PublishedComponent;
import com.google.gwt.core.client.JavaScriptObject;

public class MenuPublisher {

	private static Utils.JsObject constructors = JavaScriptObject.createObject().cast();
	
	private static JavaScriptObject getPublisher(String aClassName){
		JavaScriptObject constr = constructors.getJs(aClassName);
		if(constr == null)
			throw new IllegalStateException("Constructor for " + aClassName + " was not found.");
		return constr;
	}
	
	public static native JavaScriptObject getConstructors()/*-{
		return @com.eas.menu.MenuPublisher::constructors;
	}-*/;

	public static void putPublisher(String aClassName, JavaScriptObject aPublisher){
		constructors.setJs(aClassName, aPublisher);
	}
	
	public native static PublishedComponent publish(MenuItemSeparator aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('MenuSeparator');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(MenuBar aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('MenuBar');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(Menu aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('Menu');
		return new constr(null, aComponent);
	}-*/;

	public native static PublishedComponent publishPopup(Menu aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('PopupMenu');
		return new constr(aComponent);
	}-*/;

	public native static PublishedComponent publish(MenuItemImageText aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('MenuItem');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static PublishedComponent publish(MenuItemCheckBox aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('CheckMenuItem');
		return new constr(null, null, null, aComponent);
	}-*/;

	public native static JavaScriptObject publish(MenuItemRadioButton aComponent)/*-{
		var constr = @com.eas.menu.MenuPublisher::getPublisher(Ljava/lang/String;)('RadioMenuItem');
		return new constr(null, null, null, aComponent);
	}-*/;
}
