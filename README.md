# Remote control application.
This application contains of three modules:
<ul>
<li><b>Server</b>. Handles data exchanging between connected agents and clients. 
This module also holds information about any connected agent.</li>
<li><b>Agent</b>. Passive client application which should be started on "slave" side. This module receives commands from client
and performs control actions on workstation. </li>
<li><b>Client</b>. Active client application with graphical user interface based on JavaFX. The function of this module is to give a "remote control point" for remote workstation, where agent application is running on.</li>
</ul>

# Build and execution
This application is using <a href="https://github.com/ev-koslov/data-exchanging-module">external library</a> as a communication
interface between all of modules.
<br><b>Build sequence</b>
<ol>
<li>Create new project from <a href="https://github.com/ev-koslov/data-exchanging-module">data-exchanging-module</a> sources.</li>
<li>Compile them and install to local maven repository.</li>
<li>Fetch this application sources using this <a href="https://github.com/ev-koslov/remote-control">https://github.com/ev-koslov/remote-control</a>.</li>
<li>After all this project can be sucessfully compiled and executed.</li>
</ol>
