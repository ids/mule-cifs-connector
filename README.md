# Mule CIFS Connector

This is a port of the community based [mulesoft 3.9 samba connector](https://github.com/mulesoft-consulting/samba-connector) to support the Mulesoft 4.x runtime.  It allows for authenticated access to CIFS/Samba file shares, which is tragically lacking in Mulesoft 4.x.  For some reason the Salesforce company is unable to provide this functionality out of the box on their expensive, buggy, closed source product.  But hey, Gartner likes it, so it must be good!

> __Note__ that this port was completed using `mvn` on the CLI and __Visual Studio Code__ because none of Mulesoft's examples work in their Eclipse plugin.  It is by far the worst Eclipse plugin experience I have ever had (feedback from a long time Eclipse user that has also developed Eclipse plugins).  The only thing less acceptable then the current state of the Mulesoft IDE offering is their developer documentation; which rarely if ever provides working sample code.  

Mulesoft is easily the worst Java platform I have ever used, and the fact that this connector had to be built by the community should give one pause, and possibly encourage a more informed integration platform choice. 


## Installation

After fetching this repo type:

`mvn install -DskipTests`

This will install the `mule-cifs-connector` in your local maven repository. `install -DskipTests` is required as the _Test Suite_ depends on the manual setup of a CIFS share.

You may also want to perform the required setup and run the _Test Suite_ to ensure operation:

`mvn clean test`

> __Note__ that you will need to update the `test-mule-config.xml` Mulesoft test configuration with a valid CIFS configuration, and ensure the necessary sample file is in place.  The tests assume that the share specified contains a file called `test.xml`.  Before running the test suite make sure to perform the necessary setup, with the correct credentials and CIFS share information.  Review the [test-mule-config.xml](src/test/resources/test-mule-config.xml) for specific details before execution.

## Setup

If you must use Mulesoft, and you need this functionality:

Add this dependency to your application pom.xml

```
<groupId>io.idstudios.mule.extension</groupId>
<artifactId>mule-cifs-connector</artifactId>
<version>1.0.0-SNAPSHOT</version>
<classifier>mule-plugin</classifier>
```

However, if you want to extracate yourself from an overpriced legacy platform and a flawed integration philosophy, consider reading about [Kafka](https://kafka.apache.org/) and the [Confluent Platform](https://www.confluent.io/).

> The API-Everywhere approach touted by Mulesoft strikes me as a bit naive.  While no one would disagree that API abstractions are preferrable to db-to-db synchronization, a collection of APIs cobbled together and interdependent does not represent an integration strategy.  Furthermore, components that implement basic File, FTP, REST and DB functionality, in place of code, in a GUI designer, accomplish nothing toward furthering that end.  The challenge is in mitigating the matrix of complexity that emerges when trying to evolve and change a myriad of interdependent schemas and APIs over time.  Streaming, and the Kafka/PubSub model, seems to have emerged as an alternative approach toward finding a way to tame these complex issues.  API-Everywhere, in and of itself, is a road to dependency hell like any other.

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

#### saveFile

Saves the specified `payload` to the output `fileName`:

Eg.
```
    <flow name="saveFileFlow">
        <cifs:read-file config-ref="config" fileName="test.xml"/>
        <cifs:save-file config-ref="config" fileName="delete-test.xml" payload="#[payload]" />
        <logger level="INFO" message="#[payload]"/>
    </flow>
```

> Note that with the current implementation of __saveFile__ (and deleteFile) the default configuration saves the response as the `payload`, so after saving the file the payload will be a `boolean` value indicating if the file was saved (or not).  If you do not wish your `payload` to be overwritten, make sure to set the target as a variable.  Investigation into having this be more exception based is ongoing.

#### deleteFile

Delete the specified `fileName` at the configured path:

Eg.
```
    <flow name="deleteFileFlow">
        <cifs:delete-file config-ref="config" fileName="delete-test.xml"/>
    </flow>
```

## Developing with JavaKerberos

The jCIFS library used by this Mulesoft connector uses the `JavaKerberos` implementation under the hood.  This is the same `JavaKerberos` used by JDBC when connecting to MSSQL using Windows Authentication from Java.

When debugging, it can be helpful to see the `JavaKerberos` debug messages.  This can be enabled by setting the following system property:

```
-Dsun.security.krb5.debug=true
```

When developing for Mulesoft on Windows, you may experience long delays when connecting to the file share (or database in the case of JDBC).  This can be due to connection failures when the library is auto-discovering domain controllers.  To avoid this, it is wise to create a `krb5.ini` file and place it in the `C:\Windows` directory:

```
[libdefaults]
 dns_lookup_realm = false
 ticket_lifetime = 24h
 renew_lifetime = 7d
 forwardable = true
 rdns = false
 default_ccache_name = KEYRING:persistent:%{uid}
 default_realm = EXAMPLE.COM

[realms]
 EXAMPLE.COM = {
  kdc = kdc.example.com
 }

[domain_realm]
.example.com = EXAMPLE.COM
 example.com = EXAMPLE.COM

```

Replace EXAMPLE.COM with the relevant realm or domain.

Ensure that the server value represented by `kdc.example.com` points to the correct Kerberos server or domain controller.  If you do not know the correct `KDC`, you can examine the debug log output for successful connect messages when running without a `krb5.ini` file, and use the specified server mentioned in the output.  Configure the `krb5.ini` accordingly and the specified `KDC` server will then be the default, and should reduce the timeout/retry delays associated with Kerberos authentication.