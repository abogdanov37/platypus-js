package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a closed event.
 *
 * @author mg
 */
public class ClosedEvent extends Event<WindowPanel> {

    /**
     * Creates a new closed event.
     *
     * @param target the target
     */
    public ClosedEvent(WindowPanel target) {
        super(target, target);
    }
}
