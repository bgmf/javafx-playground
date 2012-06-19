package de.dzim.jfx.pwm.model.internal;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

	@Override
	public Date unmarshal(String v) throws Exception {

		return new Date(DatatypeConverter.parseDate(v).getTimeInMillis());
	}

	@Override
	public String marshal(Date v) throws Exception {

		if (v == null) {
			return null;
		}

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(v.getTime());

		return (javax.xml.bind.DatatypeConverter.printDate(c));
	}
}
