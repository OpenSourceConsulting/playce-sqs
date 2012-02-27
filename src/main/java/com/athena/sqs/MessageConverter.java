package com.athena.sqs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;


/**
 * This class is to convert Video Hub message before sending and receiving with Amazon SQS.
 *  
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public class MessageConverter {
	
	public static byte [] stringToByteArray(String message) throws IOException {
		return stringToByteArray(message, null);
	}
	
	/**
	 * Convert string to byte array object
	 * @param message
	 * @param encoding
	 * @return converted byte array
	 * @throws IOException
	 */
	public static byte [] stringToByteArray(String message, String encoding) throws IOException {
		if( encoding == null ) {
			return message.getBytes();
		}
		return message.getBytes(encoding);
	} 
	
	/**
	 * Converts object to byte array
	 * @param object
	 * @return byte array object
	 * @throws MessageException
	 */
	public static byte [] objectToByteArray(Object object) throws MessageException {
		if( object instanceof Serializable ) {
			throw new MessageException(object.getClass().getName() + " is not serializable object. Plase check your source code");
		}
		
		ByteArrayOutputStream bos = null;
		ObjectOutput out = null;
		try {
			bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);   
			out.writeObject(object);
			byte[] bytesObject = bos.toByteArray(); 
	
			
			return bytesObject;
		} catch(IOException ioe) {
			throw new MessageException(MessageFormat.format(MessageErrors.INTERNAL_ERROR.getDescription(), ioe.getMessage()));
		} finally {
			try {
				out.close();
				bos.close();
			}catch(Exception e) {}
		}
	}
	
	/**
	 * Converts byte array to object
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static Object byteArrayToObject(byte [] input) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(input);
		ObjectInput in = new ObjectInputStream(bis);
		Object object = null;
		try {
			object = in.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("Input byte array is not a java object type");
		} finally {
			bis.close();
			in.close();
		}
		return object;
	}
	
	/**
	 * Converts byte array to string
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String byteArrayToString(byte[] input) throws IOException {
        return new String(input);
    }
	
	/**
	 * Converts byte array to string by charset
	 * @param input
	 * @param encoding encoding name
	 * @return
	 * @throws IOException
	 */
	public static String byteArrayToString(byte [] input, String encoding) throws IOException {
		if (encoding == null) {
            return new String(input);
        } else {
            return new String(input, encoding);
        }
	}
}
