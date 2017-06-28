package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasOpenHandlers<T> {

    /**
     * Adds a {@link OpenEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addOpenHandler(OpenHandler handler);
}