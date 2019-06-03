package io.idstudios.mule.extension.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.slf4j.LoggerFactory;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;

/**
 * This class is a container for operations, every public method in this class
 * will be taken as an extension operation.
 */
public class CIFSOperations {

	private static Logger _logger = LoggerFactory.getLogger(CIFSOperations.class);

	@Config
	CIFSConfiguration config;

	/**
	 * This method will save a file in a file location.
	 * 
	 * @param fileName The name of the file
	 * @param data     the content of the file as InputStream. If you have byte[]
	 *                 array, please following to convert the content as InputStream
	 *                 'new ByteArrayInputStream(sBytes)'
	 * @return true or false
	 */
	@MediaType(value = ANY, strict = false)
	public boolean saveFile(@Config CIFSConfiguration configuration, InputStream payload, String fileName) {
		_logger.debug("Start->saveFile");
		this.config = configuration;

		NtlmPasswordAuthentication auth = this.getNtlmAuth();
		String path = getSambaConnectionString() + fileName;

		SmbFileOutputStream out = null;
		try {
			SmbFile resource = new SmbFile(path, auth);
			out = new SmbFileOutputStream(resource);
			out.write(IOUtils.toByteArray(payload));
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			_logger.error("Something went wrong while writing the file", e);
			try {
				if (out != null)
					out.close();
			} catch (IOException ignored) {
			}
		}

		_logger.debug("End->saveFile");
		return false;
	}

	/**
	 * This processor will return a map of fully qualified file name and its content
	 * present at the - configured folder based on a file name pattern
	 *
	 * @param payload
	 * @param fileNamePattern The file name pattern to read
	 * @return Map FilePath -> FileContentAsStream
	 */
	@MediaType(value = ANY, strict = false)
	public Map<String, InputStream> readFiles(@Config CIFSConfiguration configuration,
			@Content(primary = true) String payload, String fileNamePattern) {
		_logger.debug("Start->readFiles");

		this.config = configuration;

		Map<String, InputStream> files = new HashMap<>();

		NtlmPasswordAuthentication auth = this.getNtlmAuth();

		String path = getSambaConnectionString();
		_logger.debug("Target location => " + path);
		try {
			SmbFile resource = new SmbFile(path, auth);
			if (resource.isFile()) {
				files.put(resource.getPath(), readFileContents(resource));
				_logger.debug("Resource Found=> " + resource.getPath());
			} else if (resource.list().length > 0) {
				SmbFile[] fileList = resource.listFiles(fileNamePattern);
				_logger.debug("Resource is a Folder. Below are the files in this folder => " + resource.getPath());
				for (SmbFile file : fileList) {
					files.put(file.getPath(), readFileContents(file));
					_logger.debug(file.getPath());
				}
			}
		} catch (Exception e) {
			_logger.error("Something went wrong while accessing the resource", e);
			files = null;
		}

		_logger.debug("End->readFiles");
		return files;
	}

	/**
	 * This processor will return a list of fully qualified file name present at the
	 * - configured folder based on a file name pattern
	 *
	 * @param payload
	 * @param fileNamePattern The file name pattern to read
	 * @return List of files
	 */
	@MediaType(value = ANY, strict = false)
	public List<String> readFileNames(@Config CIFSConfiguration configuration, @Content(primary = true) String payload,
			String fileNamePattern) {
		_logger.debug("Start->readFileNames");
		List<String> files = new ArrayList<>();

		this.config = configuration;

		NtlmPasswordAuthentication auth = this.getNtlmAuth();

		String path = getSambaConnectionString();
		_logger.debug("Target location => " + path);
		try {
			SmbFile resource = new SmbFile(path, auth);
			if (resource.isFile()) {
				files.add(resource.getPath());
				_logger.debug("Resource Found=> " + resource.getPath());
			} else if (resource.list().length > 0) {
				SmbFile[] fileList = resource.listFiles(fileNamePattern);
				_logger.debug("Resource is a Folder. Below are the files in this folder => " + resource.getPath());
				for (SmbFile file : fileList) {
					files.add(file.getPath());
					_logger.debug(file.getPath());
				}
			}
		} catch (Exception e) {
			_logger.error("Something went wrong while accessing the resource", e);
			files = null;
		}

		_logger.debug("End->readFileNames");
		return files;
	}

	/**
	 * Message processor that can be directly called to read a specified File
	 *
	 * @param fileName The name of the file to read (excluding path)
	 * @return returns file contents as byte array
	 */
	@MediaType(value = ANY, strict = false)
	public InputStream readFile(@Config CIFSConfiguration configuration, String fileName) {
		_logger.debug("Start->readFile");

		this.config = configuration;

		NtlmPasswordAuthentication auth = this.getNtlmAuth();

		String path = getSambaConnectionString() + fileName;
		try {
			SmbFile resource = new SmbFile(path, auth);
			return readFileContents(resource);

		} catch (Exception e) {
			_logger.error("Something went wrong while reading the file", e);
		}

		_logger.debug("End->readFile");
		return null;
	}

	/**
	 * This processor will delete any file specified by the file name
	 *
	 * @param fileName The name of the file you want to delete
	 * @return returns boolean to indicate successful deletion of a file
	 */
	@MediaType(value = ANY, strict = false)
	public boolean deleteFile(@Config CIFSConfiguration configuration, String fileName) {

		_logger.debug("Start->deleteFile");

		this.config = configuration;

		NtlmPasswordAuthentication auth = this.getNtlmAuth();

		String path = getSambaConnectionString() + fileName;

		boolean status = true;
		try {
			SmbFile resource = new SmbFile(path, auth);
			resource.delete();
		} catch (Exception e) {
			_logger.error("Something went wrong while deleting the resource", e);
			status = false;
		}

		_logger.debug("End->deleteFile");
		return status;
	}

	/**
	 * Private and Protected Methods are not exposed as operations
	 */

	protected InputStream readFileContents(SmbFile sFile) {

		SmbFileInputStream inFile = null;

		try {
			inFile = new SmbFileInputStream(sFile);
			byte[] sBytes = IOUtils.toByteArray(inFile);
			inFile.close();
			return new ByteArrayInputStream(sBytes);

		} catch (Exception e) {

			try {
				if (inFile != null)
					inFile.close();
			} catch (IOException ignored) {
			}
			_logger.error("Could not read the file=> " + sFile.getPath(), e);
		}

		return null;

	}

	protected NtlmPasswordAuthentication getNtlmAuth() {
		String domain = "";
		if (config.getDomain() != null) {
			domain = config.getDomain();
		}
		return new NtlmPasswordAuthentication(domain, config.getUsername(), config.getPassword());
	}

	protected String getSambaConnectionString() {
		String FRONT_SLASH = "/";
		String BACK_SLASH = "\\";
		String folder = config.getFolder().trim();
		String connStringPostFix = FRONT_SLASH;
		if (folder.endsWith(FRONT_SLASH) || folder.endsWith(BACK_SLASH))
			connStringPostFix = "";

		StringBuilder connStr = new StringBuilder();
		connStr.append("smb://").append(config.getHost()).append(FRONT_SLASH).append(config.getFolder())
				.append(connStringPostFix);

		return connStr.toString();
	}

}
