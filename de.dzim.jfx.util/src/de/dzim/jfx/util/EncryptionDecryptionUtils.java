package de.dzim.jfx.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptionDecryptionUtils {

	public static EncryptionDecryptionUtils getInstance(String passPhrase) {
		return new EncryptionDecryptionUtils(passPhrase);
	}

	public static String getDigest(String content) {
		if (content == null)
			return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1", "SUN");
			byte[] result = null;
			// System.getProperties().list(System.err);
			String osname = System.getProperty("os.name", "");
			if (osname.toLowerCase().contains("win")) {
				result = digest.digest(content.getBytes("windows-1252"));
				return new String(new Base64().encode(result), "windows-1252");
			} else {
				result = digest.digest(content.getBytes("UTF-8"));
				return new String(new Base64().encode(result), "UTF-8");
			}
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchProviderException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	private Cipher ecipher;
	private Cipher dcipher;

	// 8-byte Salt
	private byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
			(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

	// Iteration count
	private int iterationCount = 19;

	private EncryptionDecryptionUtils(String passPhrase) {
		if (passPhrase == null)
			return;
		try {
			// Create the key
			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt,
					iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
					.generateSecret(keySpec);
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());

			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iterationCount);

			// Create the ciphers
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (java.security.InvalidAlgorithmParameterException e) {
		} catch (java.security.spec.InvalidKeySpecException e) {
		} catch (javax.crypto.NoSuchPaddingException e) {
		} catch (java.security.NoSuchAlgorithmException e) {
		} catch (java.security.InvalidKeyException e) {
		}
	}

	public String encrypt(String str) {
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");

			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			return new String(new Base64().encode(enc));
		} catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (Exception e) {
		}
		return null;
	}

	public String decrypt(String str) {
		try {
			// Decode base64 to get bytes
			byte[] dec = new Base64().decode(str.getBytes());

			// Decrypt
			byte[] utf8 = dcipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, "UTF8");
		} catch (javax.crypto.BadPaddingException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (Exception e) {
		}
		return null;
	}
}
