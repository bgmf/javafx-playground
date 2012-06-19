package de.dzim.jfx.pwm.model.container;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "group", propOrder = { "content", "groups" })
public class PWMContainerGroup implements PropertyChangeListener {

	@XmlTransient
	public static final String ID_CHANGED = PWMContainerGroup.class.getName()
			+ ".id";
	@XmlTransient
	public static final String NAME_CHANGED = PWMContainerGroup.class.getName()
			+ ".name";
	@XmlTransient
	public static final String CONTENT_CHANGED = PWMContainerGroup.class
			.getName() + ".content";

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

	@XmlAttribute(name = "id", required = true)
	protected String id;

	@XmlAttribute(name = "name", required = true)
	protected String name;

	@XmlElement(name = "content", required = false)
	protected PWMContainerGroupContent content;
	@XmlElement(name = "group", required = false)
	protected List<PWMContainerGroup> groups;

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

	public PWMContainerGroupContent getContent() {
		return content;
	}

	public void setContent(PWMContainerGroupContent content) {
		final PWMContainerGroupContent old = this.content;
		if (old != null)
			old.removeListener(this);
		this.content = content;
		this.content.addListener(this);
		pcs.firePropertyChange(NAME_CHANGED, old, this.content);
	}

	public List<PWMContainerGroup> getGroups() {
		if (groups == null)
			groups = new ArrayList<PWMContainerGroup>();
		return groups;
	}

	public void addGroup(PWMContainerGroup group) {
		if (groups == null)
			groups = new ArrayList<PWMContainerGroup>();
		groups.add(group);
		pcs.firePropertyChange(PWMContainer.GROUP_CHANGED_ADD, null, group);
	}

	public void removeGroup(PWMContainerGroup group) {
		if (groups == null)
			return;
		groups.remove(group);
		pcs.firePropertyChange(PWMContainer.GROUP_CHANGED_REMOVE, null, group);
	}
}
