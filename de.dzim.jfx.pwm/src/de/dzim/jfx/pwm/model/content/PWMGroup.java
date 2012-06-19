package de.dzim.jfx.pwm.model.content;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupContent", propOrder = { "description", "entries" })
@XmlRootElement(name = "group-content")
public class PWMGroup implements PropertyChangeListener {

	@XmlTransient
	public static final String ID_CHANGED = PWMGroupEntry.class.getName()
			+ ".id";
	@XmlTransient
	public static final String NAME_CHANGED = PWMGroupEntry.class.getName()
			+ ".name";
	@XmlTransient
	public static final String DESC_CHANGED = PWMGroupEntry.class.getName()
			+ ".desc";
	@XmlTransient
	public static final String ENTRY_ADD_CHANGED = PWMGroupEntry.class
			.getName() + ".entry.add";
	@XmlTransient
	public static final String ENTRY_REMOVE_CHANGED = PWMGroupEntry.class
			.getName() + ".entry.remove";

	@XmlTransient
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		pcs.firePropertyChange(evt);
	}

	public static void init(PWMGroup group) {
		for (PWMGroupEntry e : group.getEntries())
			e.addListener(group);
	}

	@XmlAttribute(name = "id", required = true)
	protected String id;

	@XmlAttribute(name = "name", required = true)
	protected String name;

	@XmlElement(name = "description", required = false)
	protected String description;
	@XmlElement(name = "entry", required = false)
	protected List<PWMGroupEntry> entries;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PWMGroupEntry> getEntries() {
		if (entries == null)
			entries = new ArrayList<PWMGroupEntry>();
		return entries;
	}

	public void addEntry(PWMGroupEntry entry) {
		if (entries == null)
			entries = new ArrayList<PWMGroupEntry>();
		entries.add(entry);
		pcs.firePropertyChange(ENTRY_ADD_CHANGED, null, entry);
	}

	public void removeEntry(PWMGroupEntry entry) {
		if (entries == null)
			return;
		entries.remove(entry);
		pcs.firePropertyChange(ENTRY_REMOVE_CHANGED, null, entry);
	}

	private final static QName _QNAME = new QName(null, "group-content");

	@XmlElementDecl(name = "group-content")
	public JAXBElement<PWMGroup> createJAXBElement(PWMGroup value) {
		return new JAXBElement<PWMGroup>(_QNAME, PWMGroup.class, null, value);
	}
}
