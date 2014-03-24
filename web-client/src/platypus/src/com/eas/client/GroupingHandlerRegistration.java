package com.eas.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.HandlerRegistration;

public class GroupingHandlerRegistration {

	protected Set<HandlerRegistration> registrations = new HashSet<>();

	public GroupingHandlerRegistration() {
		super();
	}

	public void add(HandlerRegistration aRegistration) {
		registrations.add(aRegistration);
	}

	public void removeHandler() {
		for (HandlerRegistration r : registrations) {
			r.removeHandler();
		}
		registrations.clear();
	}

}
