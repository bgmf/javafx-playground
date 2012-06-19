package de.dzim.jfx.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandler {

	private static EventHandler instance;

	public static EventHandler getInstance() {
		if (instance == null)
			instance = new EventHandler();
		return instance;
	}

	private List<EventHandlerListener> fireAlways = new ArrayList<EventHandlerListener>();
	private Map<String, List<EventHandlerListener>> fireOnName = new HashMap<String, List<EventHandlerListener>>();

	private EventHandler() {
	}

	public void addListener(EventHandlerListener listener) {
		fireAlways.add(listener);
	}

	public void addListener(EventHandlerListener listener, String... name) {
		if (name == null || name.length == 0) {
			addListener(listener);
			return;
		}
		for (String n : name) {
			List<EventHandlerListener> l = fireOnName.get(n);
			if (l == null)
				l = new ArrayList<EventHandlerListener>();
			if (l.contains(listener))
				continue;
			l.add(listener);
			fireOnName.put(n, l);
		}
	}

	public void removeListener(EventHandlerListener listener) {
		fireAlways.remove(listener);
	}

	public void removeListener(EventHandlerListener listener, String... name) {
		if (name == null || name.length == 0) {
			removeListener(listener);
			return;
		}
		for (String n : name) {
			List<EventHandlerListener> l = fireOnName.get(n);
			if (l == null)
				continue;
			l.remove(listener);
			fireOnName.put(n, l);
		}
	}

	public void fireEvent(Object source) {
		for (EventHandlerListener l : fireAlways) {
			l.handleEvent(new Event(null, source));
		}
	}

	public void fireEvent(Object source, String name) {
		for (EventHandlerListener l : fireAlways) {
			l.handleEvent(new Event(name, source));
		}
		List<EventHandlerListener> l = fireOnName.get(name);
		if (l == null)
			return;
		for (EventHandlerListener ehl : l) {
			ehl.handleEvent(new Event(name, source));
		}
	}

	public static class Event {

		protected String name;
		protected Object source;

		protected Event(String name, Object source) {
			this.name = name;
			this.source = source;
		}

		public String getName() {
			return name;
		}

		public Object getSource() {
			return source;
		}
	}
}
