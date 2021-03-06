Project Proposal 
=============

#### Company Name: Pi-Industries LLC.

###### Team Members: Phillip Sime, Aaron Jensen, Michael Hoyt, Andrew Reis

#### Project Description:

Our project will be a LAN casino based game that you will be able to expand with other games by using the included API.  The initial version will include Blackjack and Euchre.  When completed, any card-based game should be able to be played as long as the proper server-side software is written.  It is our goal that this will require no modifications to the client-side software other than a possible patch that would be supplied to the client from the server.

The main game engine with the game logic will be hosted on a Raspian Linux (based on Debian Wheezy) server environment running on a Rasberry Pi.  The Raspberry Pi will be running a Raspberry Pi optimized Linux operating system as well as Java SE installed.  The Raspberry Pi will also be able to connect to a LAN via modern networking technologies.  In addition there will be a GUI based application written in Java that will be able to run on any environment that is running Java 1.7 (or higher).  This GUI will allow users (client) to connect via TCP to the Raspberry Pi (server) and interact by utilizing the Java networking classes.  The GUI will not handle any of the game logic, only respond to display calls received from the game server.  All of the logic will be sent/received to/from the game server on the Raspberry Pi for handling.

All of the members in our group are very knowledgeable in the Java programming language.  In addition, Aaron and Michael have experience building GUIs in Java, Phil has experience in Java networking and experience with Linux based OSes, and Andrew has extensive experience in networking theories and protocols and extensive Linux server management (RPM and DEB based). As a whole, the members of PI-Industries have a combined experience level that should allow a well-designed product to be released within the 60 day timeframe set down by the customer.  

Our goal for this project is to create an expandable application that will allow cross-platform gaming that can be hosted on an economical Raspberry Pi and played by any computer with Java. An extension of this product, if time allows, will include a web server based on Apache Tomcat that will also be able to interact with the Raspberry Pi to allow online players to play the game without needing to have the Java client available and launched. This extension would include the use of JSP, PHP and MySQL for page design, Java-integration, and secure authentication to the gaming environment.


#### Software Process / Methodology:

Our project will be using an Agile process and we will be utilizing the Scrum methodology.  We will also handle version control and formal communcation via GitHub.
