package com.bosunbello.restful.services;

import com.bosunbello.restful.domain.Customer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/cars/{make}")
public class CustomerResource {
   private Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
   private AtomicInteger idCounter = new AtomicInteger();

   public CustomerResource() {
   }

   @POST
   @Path("/{model}/{year}")
   @Consumes("application/xml")
   public String createCustomer(@Context UriInfo info) {
	   String make = info.getPathParameters().getFirst("make");
	   String model1 = info.getPathParameters().getFirst("model");
	   String year = info.getPathParameters().getFirst("year");
	   PathSegment model = info.getPathSegments().get(2);
	   String color = model.getMatrixParameters().getFirst("color");
	   Map myMap = info.getQueryParameters();
     // Customer customer = readCustomer(is);
     // customer.setId(idCounter.incrementAndGet());
     // customerDB.put(customer.getId(), customer);
     // System.out.println("Created customer " + customer.getId());
      return color;// Response.created(URI.create("/customers/" + customer.getId())).build();

   }

   @GET
   @Path("{id}")
   @Produces("application/xml")
   public StreamingOutput getCustomer(@PathParam("id") int id) {
      final Customer customer = customerDB.get(id);
      if (customer == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      return new StreamingOutput() {
         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            outputCustomer(outputStream, customer);
         }
      };
   }
   
   @GET
   @Path("test/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response test(@PathParam("id") int id, @Context UriInfo info, @Context HttpHeaders headers,
		   @Context Request request) {
      Customer cust = new Customer();
      cust.setFirstName("Bosun");
      cust.setLastName("Bello");
      cust.setId(1234);      
     
      Date date = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();      
     
      CacheControl cc = new CacheControl();
      cc.setMaxAge(300);
      cc.setPrivate(true);
      cc.setNoStore(true);
      ResponseBuilder builder = Response.ok(cust, MediaType.APPLICATION_JSON);  
      builder.expires(date);
      builder.cacheControl(cc);
      EntityTag tag = new EntityTag(Integer.toString(cust.hashCode()));
      builder = request.evaluatePreconditions(tag);
      if (builder != null) {
    	  builder.cacheControl(cc);
    	  return builder.build();
      }
      
      return builder.build(); 
   }

   @PUT
   @Path("{id}")
   @Consumes("application/xml")
   public void updateCustomer(@PathParam("id") int id, InputStream is) {
      Customer update = readCustomer(is);
      Customer current = customerDB.get(id);
      if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND);

      current.setFirstName(update.getFirstName());
      current.setLastName(update.getLastName());
      current.setStreet(update.getStreet());
      current.setState(update.getState());
      current.setZip(update.getZip());
      current.setCountry(update.getCountry());
   }


   protected void outputCustomer(OutputStream os, Customer cust) throws IOException {
      PrintStream writer = new PrintStream(os);
      writer.println("<customer id=\"" + cust.getId() + "\">");
      writer.println("   <first-name>" + cust.getFirstName() + "</first-name>");
      writer.println("   <last-name>" + cust.getLastName() + "</last-name>");
      writer.println("   <street>" + cust.getStreet() + "</street>");
      writer.println("   <city>" + cust.getCity() + "</city>");
      writer.println("   <state>" + cust.getState() + "</state>");
      writer.println("   <zip>" + cust.getZip() + "</zip>");
      writer.println("   <country>" + cust.getCountry() + "</country>");
      writer.println("</customer>");
   }

   protected Customer readCustomer(InputStream is) {
      try {
         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = builder.parse(is);
         Element root = doc.getDocumentElement();
         Customer cust = new Customer();
         if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals(""))
            cust.setId(Integer.valueOf(root.getAttribute("id")));
         NodeList nodes = root.getChildNodes();
         for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getTagName().equals("first-name")) {
               cust.setFirstName(element.getTextContent());
            }
            else if (element.getTagName().equals("last-name")) {
               cust.setLastName(element.getTextContent());
            }
            else if (element.getTagName().equals("street")) {
               cust.setStreet(element.getTextContent());
            }
            else if (element.getTagName().equals("city")) {
               cust.setCity(element.getTextContent());
            }
            else if (element.getTagName().equals("state")) {
               cust.setState(element.getTextContent());
            }
            else if (element.getTagName().equals("zip")) {
               cust.setZip(element.getTextContent());
            }
            else if (element.getTagName().equals("country")) {
               cust.setCountry(element.getTextContent());
            }
         }
         return cust;
      }
      catch (Exception e) {
         throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
      }
   }

}
