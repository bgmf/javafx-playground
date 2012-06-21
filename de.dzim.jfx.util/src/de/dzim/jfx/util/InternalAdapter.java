package de.dzim.jfx.util;

/**
 * There seems to be some trouble with Composites with {@link IAdaptable}
 * implementations for RAP, therefore we simply create a replacement, that
 * fulfills the same purpose, but with an individual interface.
 */
public interface InternalAdapter {

	/**
	 * Since this class is used for the same purposes as the {@link IAdaptable},
	 * the description can only be the same:<br>
	 * Returns an object which is an instance of the given class associated with
	 * this object. Returns null if no such object can be found.
	 * 
	 * @param adapter
	 *            the adapter class to look up
	 * @return a object castable to the given class, or null if this object does
	 *         not have an adapter for the given class
	 */
	public Object getInternalAdapter(Class<?> adapter);
}
