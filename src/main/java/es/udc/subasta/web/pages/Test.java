package es.udc.subasta.web.pages;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Test
{
   
   @Inject
   private Messages messages;
   
   @Property
   @Persist
   private CarMaker carMaker;
   
   public enum CarMaker
   {
       MERCEDES, AUDI, BMW;
   }
   
   @Property
   @Persist
   private String carModel;

   @InjectComponent
   private Zone modelZone;
   
   @Property
   @Persist
   private List<String> availableModels;
    
    public Object onValueChanged(CarMaker maker) 
    {
       availableModels = findAvailableModels(maker);
       
       return modelZone.getBody();
    }
    
    public List<String> findAvailableModels(final CarMaker maker) 
    {
      switch (maker) 
      {
         case AUDI:
            return Arrays.asList("A4", "A6", "A8");
         case BMW:
            return Arrays.asList("3 Series", "5 Series", "7 Series");
         case MERCEDES:
            return Arrays.asList("C-Class", "E-Class", "S-Class");
         default:
            return Arrays.asList();
       }
    }    
}