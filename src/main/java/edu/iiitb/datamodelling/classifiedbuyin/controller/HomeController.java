package edu.iiitb.datamodelling.classifiedbuyin.controller;

import edu.iiitb.datamodelling.classifiedbuyin.model.Product;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private List<Product> products;
    public static final Logger logger = LogManager.getLogger(HomeController.class);

    private final static String price = "http://www.semanticweb.org/pooja/Ontology1.owl#Price";
    private final static String color = "http://www.semanticweb.org/pooja/Ontology1.owl#Color";
    private final static String type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private final static String equivalent = "http://www.w3.org/2002/07/owl#equivalentClass";
    private final static String definedBy = "http://www.w3.org/2000/01/rdf-schema#isDefinedBy";
    private final static String annotatedTarget = "http://www.w3.org/2002/07/owl#annotatedTarget";
    private final static String annotatedSource = "http://www.w3.org/2002/07/owl#annotatedSource";
    private final static String source1 = "http://www.semanticweb.org/pooja/Ontology1.owl";
    private final static String source2 = "http://www.spsclassifiedbuyin.tk/Ontology2.owl";
    private final static String namespace1 = source1 + "#";
    private final static String namespace2 = source2 + "#";
    private final static String owlFile = "src/main/resources/static/Ontology1.owl";

    @RequestMapping( value = { "/", "/home"}, method = RequestMethod.GET )
    public ModelAndView home()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping( value = { "/onto1"}, method = RequestMethod.GET )
    public ModelAndView onto1()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("onto1");
        return modelAndView;
    }


    @RequestMapping( value = "/onto1/{product_name}", method = RequestMethod.GET )
    public ModelAndView onto1home(@PathVariable("product_name") String product)
    {
        ModelAndView modelAndView = new ModelAndView();
        products = HomeController.fetchSubject(namespace1, product);
        StringBuffer sb = new StringBuffer();
        modelAndView.addObject("products", products);
        modelAndView.setViewName("table");
        return modelAndView;
    }


    @RequestMapping( value = { "/onto2"}, method = RequestMethod.GET )
    public ModelAndView onto2()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("onto2");
        return modelAndView;
    }

    @RequestMapping( value = "/onto2/{product_name}", method = RequestMethod.GET )
    public ModelAndView onto2home(@PathVariable("product_name") String product)
    {
        ModelAndView modelAndView = new ModelAndView();
        products = HomeController.fetchSubject(namespace2, product);
        StringBuffer sb = new StringBuffer();
        modelAndView.addObject("products", products);
        modelAndView.setViewName("table");
        return modelAndView;
    }

    public static List<Product> fetchSubject(String namespace, String product)
    {
        List<Product> listProd = new ArrayList<>();
        Product prod;
        BasicConfigurator.configure(new NullAppender());

        Model model = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open( owlFile );
        if (in == null) {
            throw new IllegalArgumentException("File: " + owlFile + " not found");
        }

        // read the RDF/XML file
        model.read(in, null);

        RDFNode r = model.getResource(namespace + product);

        ResIterator resIterator = model.listSubjectsWithProperty( model.getProperty(type), r);

        StmtIterator stmtIterator = model.listStatements();

        while( resIterator.hasNext() )
        {
            prod = new Product();

            Resource res = resIterator.next();
            String[] str = res.toString().split("/");
            if( str[3].equals("pooja") )
            {
                String[] str2 = str[4].split(".owl");
                prod.setSource(str2[0]);
            }
            else
            {
                String[] str2 = str[3].split(".owl");
                prod.setSource(str2[0]);
            }
            StmtIterator properties = res.listProperties();
            String[] sb = res.toString().split("#");

            prod.setName(sb[1]);
            while( properties.hasNext() )
            {
                Statement statement = properties.next();
                String[] sb3 = statement.getPredicate().toString().split("#");
                if( sb3[1].equalsIgnoreCase("type") )
                    continue;
                String[] sb2 = statement.getLiteral().toString().split("\\^\\^");
                if( sb3[1].equals("Price") ) prod.setPrice(sb2[0]);
                else if( sb3[1].equals("Color") ) prod.setColor(sb2[0]);
                else if( sb3[1].equals("CondensorType") ) prod.setCondensorType(sb2[0]);
                else if( sb3[1].equals("ChannelType") ) prod.setChannelType(sb2[0]);
                else prod.setPlayerType(sb2[0]);
            }
            listProd.add(prod);
        }
        return listProd;
    }
}
