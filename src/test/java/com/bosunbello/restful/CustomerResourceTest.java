package com.bosunbello.restful;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;



/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
   @Test
   public void testCustomerResource() throws Exception
   {	   
	   Client client = ClientBuilder.newClient();
      try {
         System.out.println("*** Create a new Customer ***");

         String xml = "<customer>"
                 + "<first-name>Bill</first-name>"
                 + "<last-name>Burke</last-name>"
                 + "<street>256 Clarendon Street</street>"
                 + "<city>Boston</city>"
                 + "<state>MA</state>"
                 + "<zip>02115</zip>"
                 + "<country>USA</country>"
                 + "</customer>";
         
         WebTarget target = client.target("http://localhost:8080/RestfulJava/services/cars/mercedes/");//.path("cars/bmw/535/2011");
         Response response = target.request().post(Entity.xml(xml));

         //Response response = client.target("http://localhost:8080/RestfulJava/services/cars/mercedes/")
         //        .request().post(Entity.xml(xml));
         if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
         String location = response.getLocation().toString();
         System.out.println("Location: " + location);
         response.close();

         System.out.println("*** GET Created Customer **");
         String customer = client.target(location).request().get(String.class);
         System.out.println(customer);

         String updateCustomer = "<customer>"
                 + "<first-name>William</first-name>"
                 + "<last-name>Burke</last-name>"
                 + "<street>256 Clarendon Street</street>"
                 + "<city>Boston</city>"
                 + "<state>MA</state>"
                 + "<zip>02115</zip>"
                 + "<country>USA</country>"
                 + "</customer>";
         response = client.target(location).request().put(Entity.xml(updateCustomer));
         if (response.getStatus() != 204) throw new RuntimeException("Failed to update");
         response.close();
         System.out.println("**** After Update ***");
         customer = client.target(location).request().get(String.class);
         System.out.println(customer);         
      } catch(Exception excp){
    	  System.out.println(excp);
      }
      finally {
         client.close();
      }
   }
}
