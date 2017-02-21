# Push Board Game

###### Add Project into Eclipse Workspace
01.00 Go to File > Import...  
02.00 Go to General > Existing Projects into Workspace  
03.00 SELECT "Select root directory:"  
03.01 SELECT "Browse..."  

###### Java EE with Embedded Apache Tomcat (Eclipse Clean and Rebuild)
01.00 DELETE files under C:\Projects\Eclipse_Workspace\SandboxJavaEE8AndTomcat9\target  
02.00 OPEN Eclipse  
03.00 Go to Project > Clean...  
04.00 SELECT your project in the "Package Explorer" view  
05.00 Go to Run > Run As > Maven build...  
06.01 INPUT "package" for "Goals:"  
06.02 SELECT Run  
07.00 If failed, then repeat steps 03.00 to 06.02  

###### Java EE with Embedded Apache Tomcat (Eclipse Debugging)
01.00 OPEN Eclipse  
02.00 SELECT your project in the "Package Explorer" view  
03.00 Go to Run > Debug As > Java Application  
04.00 FIND your "public static void main" method (eg "Application - web") for the "Select Java Application" dialog  

Contributions are welcome. Feel free to submit issues if you run into problems or you have suggestions on how to improve it!

Created by [William Chang](http://williamchang.org).

## How to contribute?

1. Before starting work on a new feature, enhancement, or fix, please create an issue and optionally assign it to yourself or a developer.
2. Fork the repository and make your changes against the 'development' branch (not master).
3. After making your changes in your fork, run tests and ensure that the page looks good and works with all supported browsers.
4. If you have made a series of commits into the 'development' branch, please try to squash them into a small number of commits.
5. Issue a Pull Request against the 'development' branch (not master).
6. The admins will review your code and may optionally request conformance, functional or other changes. Work with them to resolve any issues.
7. Upon acceptance, your code will be merged into the master branch and will become available for all.

## Legal and Licensing

This project is licensed under the [LGPL license](LICENSE.txt).
