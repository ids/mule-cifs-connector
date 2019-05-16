# Mule CIFS Connector

This is a port of the community based [mulesoft 3.9 samba connector](https://github.com/mulesoft-consulting/samba-connector) to support the Mulesoft 4.x runtime.  It allows for authenticated access to CIFS/Samba (Windows Network) file shares, which is tragically lacking in Mulesoft 4.x.  For some reason the Salesforce company is unable to provide this functionality out of the box on their expensive, buggy, closed source product Mulesoft.  But hey, Gartner likes it, so it must be good!

> __Note__ that this port was completed using `mvn` on the CLI and __Visual Studio Code__ because none of Mulesoft's examples work in their Eclipse plugin.  It is by far the sloppiest, buggiest, worst Eclipse plugin experience I have ever had (feedback from a long time Eclipse user that has also developed Eclipse plugins).  The only thing less acceptable then the current state of the Mulesoft IDE offering is their developer documentation that rarely if ever provides working sample code.  Easily the worst Java platform I have ever used, and the fact that this connector had to be built by the community should be all the evidence required to assist people in making a more informed integration platform choice. 


## Installation
After fetching this repo type:

`mvn compile install`

This will install the `mule-cifs-connector` in your local maven repository.

You may also want to run the __Test Suite__ to ensure operation:

`mvn clean test`

## Setup
If you must use Mulesoft, and you need this functionality:

Add this dependency to your application pom.xml

```
<groupId>io.idstudios.mule.extension</groupId>
<artifactId>mule-cifs-connector</artifactId>
<version>1.0.0-SNAPSHOT</version>
<classifier>mule-plugin</classifier>
```

However, if you want to extracate yourself from a sloppy overpriced legacy platform and a flawed integration philosophy, consider reading about __Kafka__ and the __Confluent Platform__.

## Usage

> For a working example see the `test-mule-config.xml` in `src/test/java`.

Ensure you add the necessary schema references to your mulesoft project:

```
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:cifs="http://www.mulesoft.org/schema/mule/cifs"
      xsi:schemaLocation="http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
          http://www.mulesoft.org/schema/mule/cifs http://www.mulesoft.org/schema/mule/cifs/current/mule-cifs.xsd">

```

### CIFSConfiguration

The __CIFSConfiguration__ can be re-used among several of the __CIFSOperations Actions__, and defines common configuration parameters for CIFS/Samba access:

```
    <cifs:config name="config" 
        configId="configId" 
        username="joeuser" 
        password="Password123"
        host="localhost"
        folder="d$/FileDropTest"
        domain="SOMEDOMAIN">
    </cifs:config>
```

### CIFSOperations

The following CIFS operations are currently supported:

#### readFile

Reads a specified file from the configured share.

Eg.
```
    <flow name="readFileFlow">
        <cifs:read-file config-ref="config" fileName="test.xml"/>
    </flow>
```

#### readFiles

Returns a Map of file names and their corresponding InputStream for the specified configuration path and matching `fileNamePattern`:

```
Map<String, InputStream> files = new HashMap<>();
```

Eg.
```
    <flow name="readFilesFlow">
        <cifs:read-files config-ref="config" fileNamePattern="*.xml" />
    </flow>
```

#### readFileNames

Returns a List of file names for the specified configuration path and matching `fileNamePattern`:

```
List<String> files = new ArrayList<>();
```

Eg.
```
    <flow name="readFileNamesFlow">
        <cifs:read-file-names config-ref="config" fileNamePattern="*.xml" />
    </flow>
```

#### saveFileFromStream

Saves the specified `payload` to the output `fileName`:

Eg.
```
    <flow name="saveFileFromStreamFlow">
        <cifs:read-file config-ref="config" fileName="test.xml"/>
        <cifs:save-file-from-stream config-ref="config" fileName="delete-test.xml" payload="#[payload]" />
        <logger level="INFO" message="#[payload]"/>
    </flow>
```

#### deleteFile

Delete the specified `fileName` at the configured path:

Eg.
```
    <flow name="deleteFileFlow">
        <cifs:delete-file config-ref="config" fileName="delete-test.xml"/>
    </flow>
```
