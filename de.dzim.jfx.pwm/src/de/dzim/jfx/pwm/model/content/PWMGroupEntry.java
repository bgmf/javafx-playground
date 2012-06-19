package de.dzim.jfx.pwm.model.content;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entry", propOrder = { "username", "password", "url",
		"description" })
public class PWMGroupEntry {

	@XmlTransient
	public static final String ID_CHANGED = PWMGroupEntry.class.getName()
			+ ".id";
	@XmlTransient
	public static final String NAME_CHANGED = PWMGroupEntry.class.getName()
			+ ".name";
	@XmlTransient
	public static final String USER_CHANGED = PWMGroupEntry.class.getName()
			+ ".user";
	@XmlTransient
	public static final String PASS_CHANGED = PWMGroupEntry.class.getName()
			+ ".pass";
	@XmlTransient
	public static final String URL_CHANGED = PWMGroupEntry.class.getName()
			+ ".url";
	@XmlTransient
	public static final String DESC_CHANGED = PWMGroupEntry.class.getName()
			+ ".desc";
	@XmlTransient
	public static final String DATE_ADD_CHANGED = PWMGroupEntry.class.getName()
			+ ".date.add";
	@XmlTransient
	public static final String DATE_MOD_CHANGED = PWMGroupEntry.class.getName()
			+ ".date.mod";
	@XmlTransient
	public static final String DATE_EXP_CHANGED = PWMGroupEntry.class.getName()
			+ ".date.exp";

	@XmlTransient
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "date-added", required = true)
	protected Calendar dateAdded;
	@XmlAttribute(name = "date-modified", required = true)
	protected Calendar dateModified;
	@XmlAttribute(name = "date-expiration", required = false)
	protected Calendar dateExpiration;

	@XmlElement(name = "username", required = true)
	protected String username;
	@XmlElement(name = "password", required = false)
	protected String password;
	@XmlElement(name = "url", required = false)
	protected String url;
	@XmlElement(name = "description", required = false)
	protected String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		final String old = this.id;
		this.id = id;
		pcs.firePropertyChange(ID_CHANGED, old, this.id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		final String old = this.name;
		this.name = name;
		pcs.firePropertyChange(NAME_CHANGED, old, this.name);
	}

	public Calendar getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Calendar dateAdded) {
		final Calendar old = this.dateAdded;
		this.dateAdded = dateAdded;
		pcs.firePropertyChange(DATE_ADD_CHANGED, old, this.dateAdded);
	}

	public Calendar getDateModified() {
		return dateModified;
	}

	public void setDateModified(Calendar dateModified) {
		final Calendar old = this.dateModified;
		this.dateModified = dateModified;
		pcs.firePropertyChange(DATE_MOD_CHANGED, old, this.dateModified);
	}

	public Calendar getDateExpiration() {
		return dateExpiration;
	}

	public void setDateExpiration(Calendar dateExpiration) {
		final Calendar old = this.dateExpiration;
		this.dateExpiration = dateExpiration;
		pcs.firePropertyChange(DATE_EXP_CHANGED, old, this.dateExpiration);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		final String old = this.username;
		this.username = username;
		pcs.firePropertyChange(USER_CHANGED, old, this.username);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		final String old = this.password;
		this.password = password;
		pcs.firePropertyChange(PASS_CHANGED, old, this.password);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		final String old = this.url;
		this.url = url;
		pcs.firePropertyChange(URL_CHANGED, old, this.url);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		final String old = this.description;
		this.description = description;
		pcs.firePropertyChange(DESC_CHANGED, old, this.description);
	}
}
