package org.indyfhir.hapicli;

import java.util.Date;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.EncodingEnum;

/**
 * Hapi-FHIR Client Sample Project
 *
 */
public class App {

        static String serverBaseEpic = "https://open-ic.epic.com/FHIR/api/FHIR/DSTU2";
        static String serverBaseCerner = "https://fhir-open.sandboxcernerpowerchart.com/dstu2/d075cf8b-3261-481d-97e5-ba6c48d3b41f";

        public static void main(String[] args) {
        	
        		String cmdCall = "1";
        		
        		if ( args.length > 0 ) {
        			cmdCall = args[0];
        		}

                // FHIR Context that is a heavy object to create so try to have just one instance shared per application.
                FhirContext fhirContext = new FhirContext(FhirVersionEnum.DSTU2);               // This explicitly tells the context you are working with DSTU2 requests / model.

                // Configure RestfulClientFactory settings like timeouts.
                fhirContext.getRestfulClientFactory().setConnectTimeout(60000); // Set timeout for connecting to resource provider.
                fhirContext.getRestfulClientFactory().setConnectionRequestTimeout(60000);       // Set timeout for waiting for request to be completed.
               																// If you are accessing a service with large results you will want to set this.
                App app = new App();

                
                if ( "1".equals(cmdCall)) app.retrieveAndDumpObservation(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB", "http://loinc.org|8310-5", true); //"5149");
                if ( "2".equals(cmdCall)) app.retrieveAndDumpObservation(fhirContext, serverBaseCerner, "2744010", "http://loinc.org|3094-0", true);
                if ( "3".equals(cmdCall)) app.retrieveAndDumpMedications(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
                if ( "4".equals(cmdCall)) app.retrieveAndDumpMedications(fhirContext, serverBaseCerner, "2744010");
//                if ( "5".equals(cmdCall)) app.retrieveAndDumpConditions(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
//                if ( "6".equals(cmdCall)) app.retrieveAndDumpConditions(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
        }

        protected void retrieveAndDumpObservation(FhirContext fhirContext, String serverURL, String patientId, String code, boolean split) {

                // Create the Client for a target resource provider.
                IGenericClient client = fhirContext.newRestfulGenericClient(serverURL);
                
                client.setEncoding(EncodingEnum.JSON);
                
                
                
                String[] sysCode = code.split("\\|");
                
//                System.out.println(String.format("\n\nSystem=%s code=%s", sysCode[0], sysCode[1]));

                // Retrieve Observations.
                IQuery<Bundle> query = client
                                    .search()                                                               // Search, vs. .read(), .create(), .update(), .delete(), .history(), ...
                                    .forResource(Observation.class) // Specifying this search is for a specific type of Resource - in this case Observation, vs. MedicationOrder, DiagnosticReport, ....
                                    								// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
                                    								// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
                                    .encodedJson()
                                    .returnBundle(Bundle.class)		// This specifies to return a Bundle of specific type. You can also control this via the fhir Context - see above.
                                    								// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.

                                    .and(Observation.PATIENT.hasId(patientId));
                if ( split ) {
                    query.and(Observation.CODE.exactly().systemAndCode(sysCode[0], sysCode[1]));
                } else {
//                    query.and(Observation.CODE.exactly().code(code));
                }
                                    
                Bundle bundle = query.execute();                     // Execute the search! Returns a Bundle Object. A read() would return just that resource instance.

                dumpObservationBundle(serverURL, bundle);
        }

        protected void retrieveAndDumpMedications(FhirContext fhirContext, String serverURL, String patientId) {

            // Create the Client for a target resource provider.
            IGenericClient client = fhirContext.newRestfulGenericClient(serverURL);
            
            client.setEncoding(EncodingEnum.JSON);
            
            // Retrieve Observations.
            Bundle bundle = client
                                .search()                                                               // Search, vs. .read(), .create(), .update(), .delete(), .history(), ...
                                .forResource(MedicationOrder.class) // Specifying this search is for a specific type of Resource - in this case Observation, vs. MedicationOrder, DiagnosticReport, ....
                                								// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
                                								// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
                                .returnBundle(Bundle.class)		// This specifies to return a Bundle of specific type. You can also control this via the fhir Context - see above.
                                								// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
                                .encodedJson()
                                .and(MedicationOrder.PATIENT.hasId(patientId))
                                .and(MedicationOrder.STATUS.exactly().code("active"))
                                .execute();                     // Execute the search! Returns a Bundle Object. A read() would return just that resource instance.

            dumpMedicationBundle(serverURL, bundle);
    }

		/**
		 * @param serverURL
		 * @param bundle
		 */
		protected void dumpObservationBundle(String serverURL, Bundle bundle) {
			for (Entry entry : bundle.getEntry()) {

				Observation obs = (Observation)entry.getResource();
				
				QuantityDt obsValue = (QuantityDt)obs.getValue();
				String obsValueStr = obsValue == null ? "N/A" : (obsValue.getValue().toPlainString() + " " + obsValue.getUnit());
				
				DateTimeDt obsEffectiveDate = (DateTimeDt)obs.getEffective();
				String obsEffectiveDateStr = obsEffectiveDate == null ? "N/A" : obsEffectiveDate.getValueAsString();
				
				System.out.println(String.format("\n-----\nURL=%s\nText=%s\nFirst code=%s\nvalueQty=%s\neffectiveDate=%s", 
										serverURL,
				                        obs.getText().getDivAsString(), 
				                        obs.getCode().getCodingFirstRep().getDisplay(), 
				                        obsValueStr, 
				                        obsEffectiveDateStr));
            }
		}
		/**
		 * @param serverURL
		 * @param bundle
		 */
		protected void dumpMedicationBundle(String serverURL, Bundle bundle) {
			for (Entry entry : bundle.getEntry()) {

				MedicationOrder medOrder = (MedicationOrder)entry.getResource();

				String dosageIns = medOrder.getDosageInstructionFirstRep().getText();
				
				String medStr = "";
				IDatatype dataType = medOrder.getMedication();
				if ( dataType instanceof ResourceReferenceDt ) {

					ResourceReferenceDt ref = (ResourceReferenceDt)dataType;
//					if ( ref.getResource() != null ) {
//						Medication med = (Medication)ref.getResource();
//						medStr = med.get
//								//.getText().getDivAsString();
//						
//					} else {
						medStr = ref.getDisplay().getValue();
//					}
					
				}
				
				Date obsEffectiveDate = medOrder.getDateWritten();
				String obsEffectiveDateStr = obsEffectiveDate == null ? "N/A" : obsEffectiveDate.toLocaleString();
				
				System.out.println(String.format("\n-----\nURL=%s\nText=%s\nFirst code=%s\nvalueQty=%s\neffectiveDate=%s", 
										serverURL,
				                        "",
				                        medStr,
				                        dosageIns, 
				                        obsEffectiveDateStr));
            }
		}

}
