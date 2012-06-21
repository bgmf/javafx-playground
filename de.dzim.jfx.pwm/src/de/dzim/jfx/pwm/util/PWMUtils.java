package de.dzim.jfx.pwm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.dzim.jfx.pwm.model.container.PWMContainer;
import de.dzim.jfx.pwm.model.container.PWMContainerGroup;
import de.dzim.jfx.pwm.model.container.PWMContainerGroupContent;
import de.dzim.jfx.pwm.model.content.PWMGroup;
import de.dzim.jfx.util.EncryptionDecryptionUtils;

public class PWMUtils {

	/*
	 * path constants
	 */

	// directory for the default file
	public static final String DEFAULT_PATH = System.getProperty("user.home")
			+ "/.pwm";

	// default file extension (it could be .xml.gz, but I chose .pwm)
	public static final String DEFAULT_EXTENSION = ".pwm"; //$NON-NLS-1$
	// backup extension to be appended to the original file
	public static final String DEFAULT_EXTENSION_BACKUP = ".bak"; //$NON-NLS-1$

	// default file name
	public static final String DEFAULT_FILE_NAME = "default-password-groups"
			+ DEFAULT_EXTENSION;

	/*
	 * task id constants
	 */

	public static final String PWM_TASK_ROOT = "de.dzimmermann.rcp.pwm.root";
	public static final String PWM_TASK_OPENDB = "de.dzimmermann.rcp.pwm.btOpenDB";

	/*
	 * convenient methods
	 */

	public static void createBackupFile(File file2backup) throws IOException {
		File backup = new File(file2backup.getAbsolutePath()
				+ DEFAULT_EXTENSION_BACKUP);
		FileReader in = new FileReader(file2backup);
		FileWriter out = new FileWriter(backup);
		int c;

		while ((c = in.read()) != -1)
			out.write(c);

		in.close();
		out.close();
	}

	/*
	 * PWM container methods
	 */

	public static PWMContainer loadPWMContainer(File path) throws Exception {

		PWMContainer container = null;

		InputStream is = new GZIPInputStream(new FileInputStream(path));

		JAXBContext ctxt = JAXBContext.newInstance(PWMContainer.class
				.getPackage().getName());
		Unmarshaller u = ctxt.createUnmarshaller();

		Object o = null;
		try {
			o = u.unmarshal(is);
		} catch (Exception e) {
			System.err.println(e);
		}
		if (o != null) {
			container = (PWMContainer) o;
		}

		is.close();

		return container;
	}

	public static void savePWMContainer(File path, PWMContainer container)
			throws Exception {

		JAXBContext ctxt = JAXBContext.newInstance(PWMContainer.class
				.getPackage().getName());
		Marshaller marshaller = ctxt.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$

		if (path == null)
			throw new Exception("No target to save the database to specified!");

		if (path.exists()) {
			try {
				createBackupFile(path);
			} catch (Exception e) {
				String message = String
						.format("Error on creating a backup for file %s.%nThe message was: %s", //$NON-NLS-1$
								path.getAbsolutePath(), e.getMessage());
				System.err.printf(message);
			}
		}

		OutputStream os = new GZIPOutputStream(
				new FileOutputStream(path, false));

		marshaller.marshal(container.createJAXBElement(container), os);

		os.flush();
		os.close();
	}

	/*
	 * PWM groups methods
	 */

	public static PWMGroup loadPWMGroup(PWMContainerGroup containerGroup,
			String password) throws JAXBException {

		if (password == null)
			password = "";

		PWMGroup pwmGroup = null;
		if (!getPWMContentHash(containerGroup,
				containerGroup.getContent().getValue()).equals(
				containerGroup.getContent().getHash()))
			return null;

		EncryptionDecryptionUtils des = EncryptionDecryptionUtils
				.getInstance(password);
		String decryptedData = des.decrypt(containerGroup.getContent()
				.getValue());
		if (decryptedData == null)
			return null;

		StringReader reader = new StringReader(decryptedData);

		JAXBContext ctxt = JAXBContext.newInstance(PWMGroup.class.getPackage()
				.getName());
		Unmarshaller u = ctxt.createUnmarshaller();

		pwmGroup = (PWMGroup) u.unmarshal(reader);

		reader.close();

		return pwmGroup;
	}

	public static void savePWMGroup(PWMContainerGroup containerGroup,
			PWMGroup pwmGroup, String password) throws JAXBException,
			IOException, Exception {

		if (password == null)
			password = "";

		if (containerGroup.getId() == null
				|| !containerGroup.getId().equals(pwmGroup.getId()))
			containerGroup.setId(pwmGroup.getId());

		if (containerGroup.getName() == null
				|| !containerGroup.getName().equals(pwmGroup.getName()))
			containerGroup.setName(pwmGroup.getName());

		JAXBContext ctxt = JAXBContext.newInstance(PWMGroup.class.getPackage()
				.getName());
		Marshaller marshaller = ctxt.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$

		StringWriter writer = new StringWriter();

		marshaller.marshal(pwmGroup.createJAXBElement(pwmGroup), writer);

		writer.flush();
		writer.close();

		EncryptionDecryptionUtils des = EncryptionDecryptionUtils
				.getInstance(password);
		String encryptedData = des.encrypt(writer.toString());
		if (encryptedData == null)
			throw new Exception("Encryption of the storage data failed!");

		if (containerGroup.getContent() == null)
			containerGroup.setContent(new PWMContainerGroupContent());
		containerGroup.getContent().setValue(encryptedData);

		String hash = getPWMContentHash(containerGroup, encryptedData);
		containerGroup.getContent().setHash(hash);
	}

	/*
	 * PWM helper methods
	 */

	public static String getPWMContentHash(PWMContainerGroup containerGroup,
			String encryptedData) {
		return EncryptionDecryptionUtils.getDigest(containerGroup.getId() + "|"
				+ containerGroup.getName() + "|" + encryptedData);
	}

	public static String getPWMID(String name, Calendar cal) {
		return EncryptionDecryptionUtils
				.getDigest(name + cal.getTimeInMillis());
	}

	public static Object findParentPWMObject(Object parent,
			PWMContainerGroup group) {
		if (parent instanceof PWMContainer) {
			for (PWMContainerGroup cg : ((PWMContainer) parent).getGroups()) {
				if (cg.getId().equals(group.getId()))
					return parent;
			}
			PWMContainerGroup cgParent = null;
			for (PWMContainerGroup cg : ((PWMContainer) parent).getGroups()) {
				Object o = findParentPWMObject(cg, group);
				if (o != null && o instanceof PWMContainerGroup) {
					cgParent = (PWMContainerGroup) o;
					break;
				}
			}
			return cgParent;
		} else {
			PWMContainerGroup cgParent = null;
			for (PWMContainerGroup cg : ((PWMContainerGroup) parent)
					.getGroups()) {
				if (cg.getId().equals(group.getId()))
					return parent;
				Object o = findParentPWMObject(cg, group);
				if (o != null && o instanceof PWMContainerGroup) {
					cgParent = (PWMContainerGroup) o;
					break;
				}
			}
			return cgParent;
		}
	}
}
