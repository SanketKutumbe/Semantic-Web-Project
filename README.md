# Semantic-Web-Project

Here, we have used Protege 5.2.0 for creating ontologies (= grammar) and merged it and serialised it using file_name.owl

OWL is Ontology Web Language.

In IDE, provide dependency for Apache Jena library, springboot, maven. Data persistence in database is not used here hence database dependency is not needed.

-------------------------------------------------------------------------------------------------------------------------------------------Setting requirements for project (Rebuild project). 

Used softwares and tools:
1) JDK 8
2) IDE
3) MAVEN build tool to fulfil dependency of Spring-Boot, Apache Jena Library, Log4J, Thymeleaf


--------------------------------------------------------------------------------------------------

Controller:

1) HomeController -> Used to display the contents from different ontologies according to URL.

   Methods:- 1] "index" method is used to represent the link for both of ontologies.
   	     2] "onto1home" and "onto2home" methods used to represent the specific set of instances.
	     3] "onto1" and "onto2" methods represent home for ontology.
			 
--------------------------------------------------------------------------------------------------

