package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.ImageLabel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;

public class PlatypusLabel extends ImageLabel implements HasPublished {
	
	protected JavaScriptObject published;

	public PlatypusLabel(String aTitle, boolean asHtml, ImageResource aImage) {
	    super(aTitle, asHtml, aImage);
    }

	public PlatypusLabel(String aTitle, boolean asHtml) {
	    super(aTitle, asHtml);
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
		published.opaque = false;

		Object.defineProperty(published, "text", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getText()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getImage()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getIconTextGap()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getHorizontalTextPosition()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusLabel::LEFT :	return $wnd.HorizontalPosition.LEFT; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::CENTER :	return $wnd.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.HorizontalPosition.LEFT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusLabel::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusLabel::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusLabel::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getVerticalTextPosition()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusLabel::TOP :	return $wnd.VerticalPosition.TOP; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::CENTER :	return $wnd.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.VerticalPosition.TOP:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusLabel::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusLabel::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusLabel::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getHorizontalAlignment()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusLabel::LEFT :	return $wnd.HorizontalPosition.LEFT; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::CENTER :	return $wnd.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.HorizontalPosition.LEFT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusLabel::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusLabel::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusLabel::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getVerticalAlignment()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusLabel::TOP :	return $wnd.VerticalPosition.TOP; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
					case @com.eas.client.form.published.widgets.PlatypusLabel::CENTER :	return $wnd.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.VerticalPosition.TOP:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusLabel::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusLabel::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusLabel::CENTER);
						break;
				}
			}
		});
	}-*/;
}
