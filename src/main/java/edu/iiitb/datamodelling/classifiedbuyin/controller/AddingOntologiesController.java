package edu.iiitb.datamodelling.classifiedbuyin.controller;

import edu.iiitb.datamodelling.classifiedbuyin.model.Classes;
import edu.iiitb.datamodelling.classifiedbuyin.model.IRI;
import edu.iiitb.datamodelling.classifiedbuyin.model.Instances;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class AddingOntologiesController {

    private List<Classes> classes;
    private List<Classes> superClasses;
    private List<Instances> instances;
    private List<Property> properties;
    private List<IRI> iris;

    public List<Classes> getSuperClasses() {
        return superClasses;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Classes> getClasses() {
        return classes;
    }

    public List<Instances> getInstances() {
        return instances;
    }

    public List<IRI> getIris() {
        return iris;
    }

    public void update(String owlFile) throws IOException {
        BasicConfigurator.configure(new NullAppender());

        System.out.println("SPSClassifiedBuyIn - A Data Modelling Project ");
        Model model = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file
        model.read(in, null);

        //To show the classes

        Map<Integer, String> cls = new HashMap();

        classes = new ArrayList<>();
        iris = new ArrayList<>();
        instances = new ArrayList<>();
        superClasses = new ArrayList<>();
        properties = new ArrayList<>();


        //Initialising the IRIs and properties
        Map<String, String> map = model.getNsPrefixMap();
        IRI iri;
        for(Map.Entry<String, String> entries: map.entrySet())
        {
            iri = new IRI();
            iri.setString(entries.getValue());
            iris.add(iri);
        }

        //Initialising properties
        Set<Property> set = new HashSet<>();

        StmtIterator stmtIterator = model.listStatements();
        while( stmtIterator.hasNext() )
        {
            Statement statement = stmtIterator.nextStatement();
            set.add( statement.getPredicate() );
        }

        Iterator<Property> iterator = set.iterator();
        while( iterator.hasNext() )
        {
            properties.add(iterator.next());
        }

        NodeIterator nodeIterator = model.listObjects();
        while( nodeIterator.hasNext() )
        {
            RDFNode rdfNode = nodeIterator.next();

            if( rdfNode.toString().contains("#Class") )
            {
                ResIterator resIterator = model.listSubjectsWithProperty( model.getProperty(model.getNsPrefixURI("rdf") + "type"), rdfNode);

                int n = 0;
                while( resIterator.hasNext() )
                {
                    Resource res = resIterator.next();


                    n++;
                    Classes cs = new Classes();
                    cs.setIri(res.getNameSpace());
                    cs.setName(res.getLocalName());
                    classes.add(cs);

                    cls.put(n, res.toString());
//                    System.out.println(resIterator.next().getLocalName());
                }
            }

        }
        Map<Integer, String> instancs = new HashMap<>();
        nodeIterator = model.listObjects();

        while( nodeIterator.hasNext() )
        {

            RDFNode rdfNode = nodeIterator.next();

            if( cls.containsValue(rdfNode.toString()) )
            {
                ResIterator resIterator = model.listSubjectsWithProperty( model.getProperty(model.getNsPrefixURI("rdf") + "type"), rdfNode);

                while( resIterator.hasNext() )
                {
                    Resource resource = resIterator.next();

                    Instances i = new Instances();
                    i.setIri(resource.getNameSpace());
                    i.setName(resource.getLocalName());
                    instances.add(i);

                }
            }

        }


        superClasses.addAll(classes);
        Classes c = new Classes();
        c.setName("Thing");
        c.setIri("http://www.w3.org/2002/07/owl#");
        superClasses.add(c);

        if( owlFile.contains("Ontology1") ) {
            try {
                FileWriter out = new FileWriter("src/main/resources/static/owl/new.owl");
                model.write(out, "RDF/XML-ABBREV");
                //            new FileWriter("/src/main/resources/new.owl");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        in.close();
//        model.close();

    }


    @RequestMapping( value = { "/admin/iris" }, method = RequestMethod.POST )
    public ModelAndView addIRI(HttpServletRequest request) throws IOException {

        String owlFile = "src/main/resources/static/owl/new.owl";
        ModelAndView modelAndView = new ModelAndView();
        String str = request.getParameter("iri");
        modelAndView.setViewName("add");

        Model model = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file

        model.read(in, null);

        model.setNsPrefix("abc", str);
        try
        {
            FileWriter out = new FileWriter(owlFile);
            model.write(out, "RDF/XML-ABBREV");
//            new FileWriter("/src/main/resources/new.owl");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        in.close();
        model.close();
        update(owlFile);

        modelAndView.addObject("iris", iris);
        modelAndView.addObject("classes",classes);
        modelAndView.addObject("properties",properties);
        modelAndView.addObject("superClasses",superClasses);

        return modelAndView;
    }

    @RequestMapping( value = {"/admin/classes"}, method = RequestMethod.POST )
    public ModelAndView addClasses(HttpServletRequest request) throws IOException {
//        String owlFile = "src/main/resources/static/owl/new.owl";
        String owlFile = "target/classes/static/owl/new.owl";
        ModelAndView modelAndView = new ModelAndView();

        String subjectNS = request.getParameter("SubjectNS");
        String subject = request.getParameter("Subject");
        modelAndView.setViewName("add");

        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file

        model.read(in, null);

        System.out.println(model.getNsURIPrefix(subjectNS));
//        Resource res = model.createResource( model.getNsPrefixURI(model.getNsURIPrefix(subjectNS)) + subject );
//        Property prop = model.createProperty( model.getNsPrefixURI("rdf") + "type"  );
//        Resource obj = model.createResource( model.getNsPrefixURI("owl") + "Class");
//
//        model.add(res,prop,obj);

//        Resource resource;
//        Property property;
//        Resource object;
//
//        model.setNsPrefix("foaf","http://www.newontology/Ontology3.owl#");
//        resource = model.createResource((model.getNsPrefixURI("foaf")) + "Person");
//        property = model.createProperty(model.getNsPrefixURI("rdf") + "type");
//        object = model.createResource( model.getNsPrefixURI("owl") + "Class");
//        model.add(resource,property,object);

        try
        {
            FileWriter out = new FileWriter(owlFile);
            model.write(out, "RDF/XML-ABBREV");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        update(owlFile);

        modelAndView.addObject("iris", iris);
        modelAndView.addObject("classes",classes);
        modelAndView.addObject("properties",properties);
        modelAndView.addObject("superClasses",superClasses);

        in.close();
        model.close();

        return modelAndView;
    }

    @RequestMapping( value = {"/admin/subClasses"}, method = RequestMethod.POST )
    public ModelAndView addSubClasses(HttpServletRequest request) throws IOException {
        String owlFile = "src/main/resources/static/owl/new.owl";
        ModelAndView modelAndView = new ModelAndView();

        String subject = request.getParameter("Subject");
//        String subjectNS = request.getParameter("SubjectNS");
        String property = request.getParameter("Property");
        String object = request.getParameter("Object");
        modelAndView.setViewName("add");

        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file

        model.read(in, null);

        String[] prope = property.split("#");
        String[] objc = object.split("#");

        Resource res = model.createResource( model.getResource(subject));
        Property prop = model.createProperty( model.getNsPrefixURI(model.getNsURIPrefix(prope[0])) + prope[1] );
        Resource obj = model.createResource( model.getNsPrefixURI(model.getNsURIPrefix(objc[0])) + objc[1]);

        model.add(res,prop,obj);
        try
        {
            FileWriter out = new FileWriter(owlFile);
            model.write(out, "RDF/XML-ABBREV");
//            new FileWriter("/src/main/resources/new.owl");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        in.close();
        model.close();

        update(owlFile);

        modelAndView.addObject("iris", iris);
        modelAndView.addObject("classes",classes);
        modelAndView.addObject("properties",properties);
        modelAndView.addObject("superClasses",superClasses);

        return modelAndView;
    }

    @RequestMapping( value = {"/admin/instances"}, method = RequestMethod.POST )
    public ModelAndView addInstances(HttpServletRequest request) throws IOException {
        String owlFile = "src/main/resources/static/owl/new.owl";
        ModelAndView modelAndView = new ModelAndView();

        String subject = request.getParameter("Subject");
        String subjectNS = request.getParameter("SubjectNS");
        String property = request.getParameter("Property");
        String object = request.getParameter("Object");
        modelAndView.setViewName("add");

        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file

        model.read(in, null);

        String[] objc = object.split("#");

        Resource res = model.createResource( model.getNsPrefixURI(model.getNsURIPrefix(subjectNS)) + subject );
        Property prop = model.createProperty( model.getNsPrefixURI("rdf") + "type" );
        Resource obj = model.createResource( model.getNsPrefixURI(model.getNsURIPrefix(objc[0])) + objc[1]);

        model.add(res,prop,obj);
        try
        {
            FileWriter out = new FileWriter(owlFile);
            model.write(out, "RDF/XML-ABBREV");
//            new FileWriter("/src/main/resources/new.owl");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        in.close();
        model.close();

        update(owlFile);

        modelAndView.addObject("iris", iris);
        modelAndView.addObject("classes",classes);
        modelAndView.addObject("properties",properties);
        modelAndView.addObject("superClasses",superClasses);

        return modelAndView;
    }

    @RequestMapping( value = {"/admin/equivalency"}, method = RequestMethod.POST )
    public ModelAndView addEquivalency(HttpServletRequest request) throws IOException {
        String owlFile = "src/main/resources/static/owl/new.owl";
        ModelAndView modelAndView = new ModelAndView();

        String subject = request.getParameter("Subject");
        String object = request.getParameter("Object");
        modelAndView.setViewName("add");

        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file

        model.read(in, null);

        String[] subj = object.split("#");
        String[] objc = object.split("#");

        Resource res = model.createResource( model.getNsPrefixURI(model.getNsURIPrefix(subj[0])) + subj[1] );
        Property prop = model.createProperty( model.getNsPrefixURI("owl") + "equivalentClass" );
        Resource obj = model.createResource( model.getNsPrefixURI(model.getNsURIPrefix(objc[0])) + objc[1]);

        model.add(res,prop,obj);
        try
        {
            FileWriter out = new FileWriter(owlFile);
            model.write(out, "RDF/XML-ABBREV");
//            new FileWriter("/src/main/resources/new.owl");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        in.close();
        model.close();

        update(owlFile);

        modelAndView.addObject("iris", iris);
        modelAndView.addObject("classes",classes);
        modelAndView.addObject("properties",properties);
        modelAndView.addObject("superClasses",superClasses);

        return modelAndView;
    }

}
