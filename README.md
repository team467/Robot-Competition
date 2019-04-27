# Robot2019-Competition
Welcome to 467's release ofr **Stargazer**'s code

_*NOTE:* this code is still under development so there are many branches to explore_
## Setup

First set up a programming laptop using the instructions [here](https://wpilib.screenstepslive.com/s/4485/m/java)

#### How to view on VScode
* Clone this [repo](https://github.com/team467/Robot2019-Competition) into VScode
* Run `./gradlew` in the terminal to download gradle and FRC libraries
* Run `./gradlew build` or `./gradlew deploy` to build or deploy the code
* _Hopefully_ the robot will run correctly  


#### How to view Pi code
We don't currently run our vision code on the RIO, vision code is done in python and can be obtained [here](https://github.com/team467/Robot2019-RaspberryPI).  

## Highlights
* Non synchronous loggers  
<br/>Nonsynchronous loggers allow for our logging to fast and easier to process. This makes diagnosing any problems easier. Our logging also allows logging to an external USB drive.  
&nbsp;
* Auto
<br/> Our Auto Path planning is done with a stanley controller inspired off of [Python Robotics] (https://github.com/AtsushiSakai/PythonRobotics).  
&nbsp;
* States and state machines
<br/> All gamepeices are controlled through state machines. States are stored within `src/main/java/frc/robot/gamepieces/states` and the state machines within `src/main/java/frc/robot/gamepieces/statesMahines`. The states handle states and the state machines handle state transitions as well as entry and exit actions. All planning and Robot movements are handled currently in `src/main/java/frc/robot/gamepieces/GamePeiceController.java`. 

