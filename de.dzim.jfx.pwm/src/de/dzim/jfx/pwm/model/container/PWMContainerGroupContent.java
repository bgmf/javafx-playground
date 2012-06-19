package de.dzim.jfx.pwm.model.container;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "content")
public class PWMContainerGroupContent {

	@XmlTransient
	public static final String HASH_CHANGED = PWMContainerGroupContent.class.getName()
			+ ".hash";
	@XmlTransient
	public static final String VALUE_CHANGED = PWMContainerGroupContent.class.getName()
			+ ".value";

	@XmlTransient
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@XmlAttribute(name = "hash", required = true)
	protected String hash;

	@XmlValue
	protected String value;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		final String old = this.hash;
		this.hash = hash;
		pcs.firePropertyChange(HASH_CHANGED, old, this.hash);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		final String old = this.value;
		this.value = value;
		pcs.firePropertyChange(VALUE_CHANGED, old, this.value);
	}

}
