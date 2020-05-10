# Final Year Project 16387431

This Project is a implementation of a mobile computing orchestrator and node setup which allows for TLS based secure migration docker images. This serves as a proof of concept implementation of a migration and also includes a sample accompanying Android application to connect to any docker image hosted.


## A note on security
For testing purposes these jars are using preprovided keystores, for any actual implementation built on top of this these should be swapped out by your own keystores.

## Setup Instructions

## Orchestrator 
Before trying to run the orchestrator make sure that the port it will run on is free and has firewall clearence,

To run in ordinatry mode the command is
 ```
java -jar {Release.jar} {Port} 
i.e java -jar orchestrator-0.7.0-jar-with-dependencies 443
```
 
To run in secure mode include -s
 ```
java -jar {Release.jar} {Port} -s 
i.e java -jar orchestrator-0.7.0-jar-with-dependencies 443 -s
```

The orchestrator as part of its evaluation uses a rolling average of node CPU and Ram availability, by default this is set 80/20 in favour of new values. To set this manually simple add -RollingAverage to command input in the format 60 for a 60/40 split.
 ```
java -jar {Release.jar} {Port} -RollingAverage 
i.e java -jar orchestrator-0.7.0-jar-with-dependencies 443 -RollingAverage 60
```

## EdgeNode 
To run in ordinatry mode the command is
 ```
java -jar {Release.jar} {Orchestrator address} {file to host} {address of services hosted on this cloudNode} 
i.e java -jar edge-0.6.0-jar-with-dependencies ws://193.145.123.56:443 C://path/to/file.tar ws://192.168.18.250:8080
```
 
To run in secure mode include -s, note all node addresses should be in form wss instead of ws
 ```
java -jar {Release.jar} {Orchestrator address} {file to host} {address of services hosted on this cloudNode} -s
i.e java -jar edge-0.6.0-jar-with-dependencies ws://193.145.123.56:443 C://path/to/file.tar ws://192.168.18.250:8080 -s
```

for testing edgenodes feature an input to declare themselves as badAgents, which means even if they pass the SSL phase, orchestrators won't trust them for transfers.
 ```
java -jar {Release.jar} {Orchestrator address} {file to host} {address of services hosted on this cloudNode} -bA
i.e java -jar edge-0.6.0-jar-with-dependencies ws://193.145.123.56:443 C://path/to/file.tar ws://192.168.18.250:8080 -bA
```

## CloudNode 
To run in ordinatry mode the command is
 ```
java -jar {Release.jar} {Orchestrator address} {file to host} {address of services hosted on this cloudNode} 
i.e java -jar cloud-0.6.0-jar-with-dependencies ws://193.145.123.56:443 C://path/to/file.tar ws://192.168.18.250:8080
```
 
To run in secure mode include -s, note all node addresses should be in form wss instead of ws
 ```
java -jar {Release.jar} {Orchestrator address} {file to host} {address of services hosted on this cloudNode}  -s
i.e java -jar cloud-0.6.0-jar-with-dependencies wss://193.145.123.56:443 C://path/to/file.tar wss://192.168.18.250:8080 -s
```

## Android Application
The Android project that accompanies this implementation serves as simple controller to show how an android application can connect to the orchestrator,request a service, and then communicate with that service.
This needs to be built from source, and has been developed and tested with android studio.
To run:
Select whether or not secure mode should be used, Input the IP of the desired Orchestrator and the app will connect to it. 
After a successful connection you can send strings and file to the docker image, depending on the docker image connected too it may handle these inputs in strange ways, 
so the android app isn't user proofed like the rest of the elements and simply serves as a template for any development of a dedicated app.

## Run instructions
Deploy the orchestrator jar in it's desired mode, then deploy at least two nodes, any number or combinations of cloud and edge are fine based on your needs. 
Make sure that all ports selected aren't blocked by a systems firewall.
Launch the android application and use it to connect to the orchestrator, this should trigger a migration to occur.



