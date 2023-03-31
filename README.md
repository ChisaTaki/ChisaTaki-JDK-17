# ChisaTaki Bot

The ChisaTaki bot is a bot written for the ChisaTaki server, and its exclusive use. This bot should never be allowed into another server as it could cause unforeseen consequences. However, you are allowed to fork this repo and use the code in your own bot.

## Installation

The ChisaTaki bot requires two seperate .jar files to function correctly: `Packages.jar` and `Packages2.jar`

Their manifests are defined by the following:

**Manifest2.txt**

```bash
Manifest-Version: 0.12
Class-Path: target/dependency/nv-websocket-client-2.14.jar target/dependency/jackson-databind-2.13.2.2.jar target/dependency/jackson-core-2.13.3.jar target/dependency/jackson-annotations-2.13.2.jar target/dependency/jackson-datatype-jdk8-2.12.4.jar target/dependency/javax.json-1.1.4.jar target/dependency/javax.json-api-1.0.jar target/dependency/lavaplayer-1.3.77.jar target/dependency/lava.jar target/dependency/lavaplayer-natives-1.3.14.jar

```

**Manifest3.txt**

```bash
Manifest-Version: 0.12
Class-Path: target/dependency/httpmime-4.0.3.jar target/dependency/okio-2.8.0.jar target/dependency/apache-mime4j-0.6.jar target/dependency/commons-codec-1.3.jar target/dependency/commons-logging.1.1.1.jar target/dependency/httpclient-4.0.3.jar target/dependency/httpcore-4.0.1.jar target/dependency/commons-io-2.11.0.jar target/dependency/httpclient5-5.2.1.jar target/dependency/commons-logging-1.2.jar logback.xml target/dependency/dotenv-java-2.3.2.jar

```

### Maven Dependencies

To avoid formatting conflicts between developer environments, each developer will have their own `pom.xml` by following the steps below:

**Setting up the pom.xml**

1. Create a `pom.xml` file at project root level
2. Copy the content of `pom.sample.xml` into the new file created
3. Save and do a maven update (replit does it automatically)

**Updating the pom.xml**

1. Add the necessary dependencies as usual in your _own_ `pom.xml`
2. Ensure your changes produce the desired output
3. Copy and paste the lines you added in step 1 into the `pom.sample.xml`
4. Resume the usual development worflow until merge (commit changes, create PR, merge branch)
5. Once the branch is merged into the main branch, you may want to notify the other devs so they can update their respective `pom.xml` after pulling your changes

## Deployment

How to build the ChisaTaki Bot

### Building the ChisaTaki.jar

**Replit**

1. Press the ▶️ Run button at the top of the Replit page
2. Confirm that no compilation errors appear
3. Press the ⏹️Stop button once `java -classpath .:target/dependency/* Main` appears. This means that the IDE will run the code. We don't want it to.
4. In the console, type out the following `jar -cvfm ChisaTaki.jar Manifest.txt dev/*`
5. Press Enter and wait for the jar file to build.
6. Once the jar file has been built download it to your downloads folder.

**Local IDE**

1. Open a File Explorer window and navigate to your target classes folder (i.e.: `target/classes`)
2. Perform a maven clean
3. Confirm that no compilation errors appear
4. Ensure the `.class` files were regenerated
5. In your target classes folder, delete any existing files/folders that do not match the repository's package name (i.e.: "META-INF") if any
6. Copy-paste the `Manifest.txt` file found at the root of the project
7. Open a terminal window and type out the following `jar -cvfm ChisaTaki.jar Manifest.txt dev/*`
8. Press Enter and wait for the jar file to build

### Deployment on Sparkedhost

1. Build the `Chisataki.jar` (see section above)
2. Go to https://control.sparkedhost.us/
3. Navigate to the Chisataki server.
4. Go to File Manager, and replace the Chisataki.jar file in there with the one you just downloaded.
5. Navigate to Console and press the Restart Button
6. If it is successful, the console should output the following

```
[main] INFO net.dv8tion.jda.api.JDA - Login Successful!
[JDA MainWS-ReadThread] INFO net.dv8tion.jda.internal.requests.WebSocketClient - Connected to WebSocket
[JDA MainWS-ReadThread] INFO net.dv8tion.jda.api.JDA - Finished Loading!
[main] INFO dev.kurumiDisciples.chisataki.Main - Chisataki Bot successfully built and connected to JDA!
[main] INFO dev.kurumiDisciples.chisataki.Main - Commands added!
[main] INFO dev.kurumiDisciples.chisataki.Main - Message Cache Size: 2000
```

### What to do in case of a Deployment Failure

1. Navigate to `File Manager`
2. Click on `Recycle Bin`
3. Recover the old `ChisaTaki.jar`
4. Press `Restart`

## Authors

-   [@Hacking Pancakez](https://github.com/Hacking-Pancakez)
-   [@shimdevkun](https://github.com/shimdevkun)
