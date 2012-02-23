package com.athena.sqs;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 *
 * This class is to process compressing/decompressing the message that send to SQS or receive from SQS.
 * The class might not be able to use currently, but if message size become larger, you might have to use this class in the future.
 *
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public class MessageCompressor {
	private static final Logger logger = Logger.getLogger(MessageCompressor.class);
    private static String CHARSET = "utf-8";
    private static final int BUFFER_SIZE = 1024 * 4;

    public static byte[] compress(String source) throws IOException {
        return compress(source, CHARSET);
    }

    public static byte[] compress(String source, String charset) throws IOException {
        return compress(source.getBytes(charset));
    }

    /**
     * Returns byte array that input byte array is compressed.
     *
     * @param bytes
     * @return
     * @throws IOException if input bytes are wrong
     */
    public static byte[] compress(byte[] bytes) throws IOException {
        return compress(new ByteArrayInputStream(bytes));
    }

    /**
     * Returns byte array that InputStream is compressed.
     *
     * @param input
     * @return
     * @throws IOException if input is wrong
     */
    public static byte[] compress(InputStream input) throws IOException {
        ByteArrayOutputStream bos = null;
        GZIPOutputStream zipOutput = null;

        try {
        	bos = new ByteArrayOutputStream();
            zipOutput = new GZIPOutputStream(bos);
            copy(input, zipOutput);
        } catch(IOException e) {
        	throw e;
        } finally {
        	input.close();
        	// Close before copy byte array. If not, it will cause ZIP EOFException.
        	zipOutput.flush();
        	zipOutput.close();
        }
        byte[] compressedBytes = bos.toByteArray();
        return compressedBytes;
    }


    public static byte[] uncompress(String input) throws IOException {
        return uncompress(input, CHARSET);
    }

    public static byte[] uncompress(String input, String charset) throws IOException {
        return uncompress(input.getBytes(charset));
    }

    public static byte[] uncompress(byte[] bytes) throws IOException {
        return uncompress(new ByteArrayInputStream(bytes));
    }


    public static byte[] uncompress(InputStream input) throws IOException {
        ByteArrayOutputStream bos = null;
        InputStream zipInput = null;
        byte[] bytes = null;
		try {
			bos = new ByteArrayOutputStream();
			zipInput = new GZIPInputStream(input);
			copy(zipInput, bos);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			zipInput.close();
			bos.close();
		}
        return bytes;
    }

    /**
     * The input and output stream won't be closed after the method invocation
     *
     * @param is
     * @param os
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int len = -1;

        while ((len = input.read(buf, 0, buf.length)) != -1) {
        	output.write(buf, 0, len);
        }
    }

    public static void main(String [] args) throws Exception {
    	BufferedReader br = new BufferedReader(new FileReader("c:\\temp\\a.txt"));
    	String line = "";
    	StringBuilder sb = new StringBuilder();
    	while((line = br.readLine()) != null) {
    		sb.append(line).append("\r\n");
    	}

    	logger.debug("Before Compress Size : " + sb.toString().getBytes().length);
    	byte [] compressed = MessageCompressor.compress(sb.toString().getBytes());
    	logger.debug("After compress size : " + compressed.length);

    	byte [] uncompressed = MessageCompressor.uncompress(compressed);
    	logger.debug("After uncompress size : " + uncompressed.length);
    	FileOutputStream fw = new FileOutputStream("C:\\Temp\\b.txt");
    	fw.write(uncompressed);
    }
}