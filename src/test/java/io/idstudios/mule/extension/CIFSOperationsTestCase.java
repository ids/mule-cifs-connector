package io.idstudios.mule.extension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.io.FileInputStream;
import java.io.InputStream;

import org.mule.runtime.core.internal.streaming.bytes.ManagedCursorStreamProvider;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;

public class CIFSOperationsTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  /*
  @Test
  public void executeSayHiOperation() throws Exception {
    String payloadValue = ((String) flowRunner("sayHiFlow").run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
    assertThat(payloadValue, is("Hello Mariano Gonzalez!!!"));
  }

  @Test
  public void executeRetrieveInfoOperation() throws Exception {
    String payloadValue = ((String) flowRunner("retrieveInfoFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
    assertThat(payloadValue, is("Using Configuration [configId]"));
  }
  */

  @Test
  public void executeReadFileOperation() throws Exception {
    Object payloadValue = ((Object) flowRunner("readFileFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
 
    assert(payloadValue != null);
  }

  @Test
  public void executeReadFilesOperation() throws Exception {
    Object payloadValue = ((Object) flowRunner("readFilesFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
 
    assert(payloadValue != null);
  }

  @Test
  public void executeReadFileNamesOperation() throws Exception {
    Object payloadValue = ((Object) flowRunner("readFileNamesFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
 
    assert(payloadValue != null);
  }

  @Test
  public void executeSaveFileFromStreamOperation() throws Exception {
    Object payloadValue = ((Object) flowRunner("saveFileFromStreamFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
 
    assert(payloadValue != null);
  }

  @Test
  public void executeDeleteFileOperation() throws Exception {
    Object payloadValue = ((Object) flowRunner("deleteFileFlow")
                                      .run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
 
    assert(payloadValue != null);
  }
}
