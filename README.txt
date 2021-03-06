OpenDS (Open-source Driving Simulator)
======================================

Version 1.0 (Source code version)


OpenDS is an open source driving simulator for research. The software is 
programmed entirely in Java and is based on the jMonkeyEngine framework, 
a scene graph based game engine which is mainly used for rendering and 
physics computation. OpenDS is distributed under the terms of GNU General 
Public License (GPL).



1. What is contained in this archive
------------------------------------
The archive you downloaded contains the source code version of OpenDS including
the Analyzer to replay a drive and all library files needed to run both. As it 
does NOT contain any scenes, models or tasks (assets), you will have to obtain 
them from http://www.opends.eu separately. OpenDS will not work without a valid
assets-folder. More details about the already available tasks can be found in 
paragraph 7.



2. Getting started
------------------
If you are not comfortable building Java applications from source, you should
download the already built version ('binaries') from http://www.opend.eu.

Otherwise, continue with the next paragraph.



3. Building OpenDS
------------------
Binaries for Windows, Mac OS and Linux are contained in this archive. The following 
steps show how to setup the source code for Eclipse. Of course, you can use your 
favored IDE.

1. Make sure you have installed the Java Development Kit (JDK) version 1.6 
   or higher. If not, download it from http://www.oracle.com/technetwork/java/javase/downloads/index.html
2. Start Eclipse and import an existing Project (File -> Import...). 
   Select 'General' -> 'Existing Projects into Workspace' and click 'Next'.
3. Select this folder as root directory and click 'Finish'.
4. Add a new folder 'assets' to your project and copy the contents of 
   assets.zip from http://www.opend.eu to that folder (required).

You can skip the next two instructions if there are no Build Path errors.
5. Make sure that all jar files (counting 90) that can be found in 'lib' or 
   any of its sub-folders have been added to the Build Path.
6. Right-click the project and select 'Build Path' -> 'Configure Build 
   Path...' to open the 'Properties'-dialog. Go to tab 'Libraries' and 
   click 'Add Class Folder'. Select the check box of folder 'Logo' which 
   can be found at 'assets/Textures' as well as the check box of folder 
   'log4j' which can be found at 'assets/JasperReports'. Click 'OK' to close 
   both dialog windows.

7. Run OpenDS by right-clicking eu.opends.main.Simulator in Eclipse's Package 
   Explorer and selecting 'Run As' -> 'Java Application'.
8. Select resolution and proceed with clicking 'OK'.
9. Specify driver’s name (optional).
10.Select which task to load and click 'Start'.

To stop the application, hit the ESC key or close the window.
Press F1 during simulation for default key assignment.



4. Where to find documentation
------------------------------.
As with any starting project, documentation writing is only beginning! The  
'doc' folder of this distribution contains the JavaDoc files. More Information
which is still growing can be found on the project website.



5. Contributors
---------------
a) Concept: Christian Müller
b) Architecture and development: Rafael Math
c) Other contributions:
	Saied Tehrani
	Michael Feld
	Otávio Biasutti
	Daniel Braun
	Gleb Banas
	Till Maurer
d) How to contribute? 
   Please write us using the contact form on the project website.



6. Credits
----------
Digital media assets have been taken from jMonkeyEngine (http://www.jmonkeyengine.org)
if no other reference can be found in the corresponding folder.



7. Available tasks
------------------

Idealtest2/idealtest2.xml

In this model the traffic light control will be demonstrated by the help of a few
simple intersections. At the beginning, trigger mode is active, i.e. approaching 
vehicles will be detected and the related traffic light will turn green. By 
pressing the "A"-key, you can toggle all traffic light modes: 
TRIGGER -> PROGRAM -> EXTERNAL INPUT -> BLINKING -> OFF. In program mode, all 
traffic lights will obey to a pre-defined list of traffic light rules and in 
external mode traffic lights can be interactively controlled by the experiment 
leader. When you reached modes blinking and off you can go back to trigger mode 
by pressing the "A"-key once more. The trip will be recorded for later analysis 
and in the "data analyzer" folder. Furthermore, a traffic vehicle obeying to traffic
lights will be available.

Stadtmitte22/stadtmitte22.xml

In order to demonstrate the triggering of events, which are able to perform state 
changes, this model contains three colored boxes (triggers). Usually triggers are 
invisible and vehicles cannot collide with them. Hitting the boxes causes actions 
like moving objects, making them (dis)appear, play sound files, set up reaction 
time measurements, set the driving car to a certain position, etc.

     
ReactionTest/ReactionTest.xml

This task contains a reaction experiment with instruction screen. The driver has to 
react to suddenly appearing signs. After the drive, a PDF file with a bar chart will 
show up.


Video5/video5.xml

This model has been created with CityEngine and shows the ability to model roads in 
hilly terrain. Furthermore, two vehicles (bus and car) will be driving.


Video16/video16.xml

Another CityEngine model showing snow and fog effects.

     
Paris/paris.xml

This CityEngine model includes photo-realistic textures of Paris and was equipped 
with further details (e.g. bus stop, roundabout).