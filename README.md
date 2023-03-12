
# ChisaTaki Bot

The ChisaTaki bot is a bot written for the ChisaTaki server, and its exclusive use. This bot should never be allowed into another server as it could cause unforeseen consequences.


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
Class-Path: target/dependency/httpmime-4.0.3.jar target/dependency/okio-2.8.0.jar target/dependency/apache-mime4j-0.6.jar target/dependency/commons-codec-1.3.jar target/dependency/commons-logging.1.1.1.jar target/dependency/httpclient-4.0.3.jar target/dependency/httpcore-4.0.1.jar target/dependency/commons-io-2.11.0.jar target/dependency/httpclient5-5.2.1.jar target/dependency/commons-logging-1.2.jar

```
    
## Deployment

How to build the ChisaTaki Bot
1. Press the ▶️ Run button at the top of the Replit page
2. Confirm that no compilation errors appear
3. Press the ⏹️Stop button once `java -classpath .:target/dependency/* Main` appears. This means that the IDE will run the code. We don't want it to. 
4. In the console, type out the following `jar -cvfm ChisaTaki.jar Manifest.txt dev/*`
5. Press Enter and wait for the jar file to build.
6. Once the jar file has been built download it to your downloads folder.
7. Go to https://control.sparkedhost.us/
8. Navigate to the Chisataki server. 
9. Go to File Manager, and replace the Chisataki.jar file in there with the one you just downloaded.
10. Navigate to Console and press the Restart Button
11. If it is successful, the console should output the following 

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

- [@Hacking Pancakez](https://github.com/Hacking-Pancakez)
- [@shimdevkun](https://github.com/shimdevkun)

