package de.dzim.jfx.pwm.model.container;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pwm", propOrder = { "groups" })
@XmlRootElement(name = "pwm")
public class PWMContainer implements PropertyChangeListener {

	@XmlTransient
	public static final String COMMON_CHANGED = PWMContainer.class.getName()
			+ ".changed";
	@XmlTransient
	public static final String GROUP_CHANGED_ADD = PWMContainer.class.getName()
			+ ".group.add";
	@XmlTransient
	public static final String GROUP_CHANGED_REMOVE = PWMContainer.class
			.getName() + ".group.remove";

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

	public static void init(PWMContainer container) {
		for (PWMContainerGroup g : container.getGroups()) {
			init(g);
			g.addListener(container);
		}
	}

	private static void init(PWMContainerGroup group) {
		for (PWMContainerGroup g : group.getGroups()) {
			init(g);
			if (g.getContent() != null)
				g.getContent().addListener(g);
		}
	}

	@XmlElement(name = "group", required = false)
	protected List<PWMContainerGroup> groups;

	public List<PWMContainerGroup> getGroups() {
		if (groups == null)
			groups = new ArrayList<PWMContainerGroup>();
		return groups;
	}

	public void addGroup(PWMContainerGroup group) {
		if (groups == null)
			groups = new ArrayList<PWMContainerGroup>();
		groups.add(group);
		pcs.firePropertyChange(GROUP_CHANGED_ADD, null, group);
	}

	public void removeGroup(PWMContainerGroup group) {
		if (groups == null)
			return;
		groups.remove(group);
		pcs.firePropertyChange(GROUP_CHANGED_REMOVE, null, group);
	}

	private final static QName _QNAME = new QName(null, "pwm");

	@XmlElementDecl(name = "pwm")
	public JAXBElement<PWMContainer> createJAXBElement(PWMContainer value) {
		return new JAXBElement<PWMContainer>(_QNAME, PWMContainer.class, null,
				value);
	}
}
