package io.idstudios.mule.extension.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;


/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */

@Operations(CIFSOperations.class)
public class CIFSConfiguration {

  @Parameter
  @Placement(order = 1, tab="System Settings")
  @Summary("The username used to connect to the CIFS share")
  @Optional(defaultValue = "mule")
  private String username;
  
  @Parameter
  @Placement(order = 2, tab="System Settings")
  @Password
  private String password;
  
  @Parameter
  @Placement(order = 3, tab="System Settings")
  @Optional(defaultValue = "localhost")
  private String host;
  
  @Parameter
  @Placement(order = 4, tab="System Settings")
  @Optional(defaultValue = "WORKGROUP")
  private String domain;
  
  @Parameter
  @Placement(order = 5, tab="System Settings")
  @Optional(defaultValue = "")
  private String folder;
  
  @Parameter
  @Placement(tab="Filters")
  @Optional(defaultValue = "false")
  @Summary("Filter to check file age")
  private boolean checkFileAge;
  
  @Parameter
  @Placement(tab="Filters")
  @Optional
  @Summary("Process files older than file age in ms")
  private long fileAge;

  @Parameter
  @Placement(tab="Advanced")
  @Optional
  @Summary("Process files older than file age in ms")
  private String outputFolder;
       
	public String getUsername() {
		return username;
  }
  
	public void setUsername(String username) {
		this.username = username;
  }
  
	public String getPassword() {
		return password;
  }
  
	public void setPassword(String password) {
		this.password = password;
  }
  
	public String getHost() {
		return host;
  }
  
	public void setHost(String host) {
		this.host = host;
  }
  
	public String getDomain() {
		return domain;
  }
  
	public void setDomain(String domain) {
		this.domain = domain;
  }
  
	public String getFolder() {
		return folder;
  }
  
	public void setFolder(String folder) {
		this.folder = folder;
  }
  
	public boolean getCheckFileAge() {
		return checkFileAge;
  }
  
	public void setCheckFileAge(boolean checkFileAge) {
		this.checkFileAge = checkFileAge;
  }
  
	public long getFileAge() {
		return fileAge;
  }
  
	public void setFileAge(long fileAge) {
		this.fileAge = fileAge;
  }
  
	public String getOutputFolder() {
		return outputFolder;
  }
  
	public void setOutputFolder(String folder) {
		this.outputFolder = folder;
	}

}
