package org.indyfhir.hapicli;

import java.util.Date;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Appointment;
import ca.uhn.fhir.model.dstu2.resource.Appointment.Participant;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Schedule;
import ca.uhn.fhir.model.dstu2.resource.Slot;
import ca.uhn.fhir.model.dstu2.valueset.ParticipantRequiredEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
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

                
                if ( "1".equals(cmdCall)) app.retrieveAndDumpObservation(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB", "http://loinc.org|3094-0", true); //"5149");
                if ( "2".equals(cmdCall)) app.retrieveAndDumpObservation(fhirContext, serverBaseCerner, "2744010", "http://loinc.org|3094-0", true);
                if ( "3".equals(cmdCall)) app.retrieveAndDumpMedications(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
                if ( "4".equals(cmdCall)) app.retrieveAndDumpMedications(fhirContext, serverBaseCerner, "2744010");
                if ( "5".equals(cmdCall)) app.addAppointmentEpic(fhirContext, serverBaseEpic, "T3Mz3KLBDVXXgaRoee3EKAAB");
//                if ( "5".equals(cmdCall)) app.retrieveAndDumpConditions(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
//                if ( "6".equals(cmdCall)) app.retrieveAndDumpConditions(fhirContext, serverBaseEpic, "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
        }

        protected void addAppointmentEpic(FhirContext fhirContext, String serverURL, String patientId) {

        	/*
        			{
						  "resourceType": "Appointment",
						  "id": "",
						  "status": "proposed",
						  "reason": {
						    "text": "Regular checkup"
						  },
						  "description": "",
						  "slot": [
						    {
						      "reference": "[SlotIdentifier]"
						    }
						  ],
						  "comment": "Regular yearly visit",
						  "participant": [
						    {
						      "actor": {
						        "reference": "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB"
						      },
						      "required": "required"
						    },
						    {
						      "actor": {
						        "reference": "T3Mz3KLBDVXXgaRoee3EKAAB"
						      },
						      "required": "required"
						    },
						    {
						      "actor": {
						        "reference": "TESU2bZxkH4ZTTEZCHK.p1wB"
						      },
						      "required": "required"
						    }
						  ]
					}
			*/ 

            Schedule schedule = lookupSchedule(fhirContext, serverURL, patientId);
            
            Slot slot = lookupSlot(fhirContext, serverURL, patientId, schedule);
        	
            IGenericClient client = fhirContext.newRestfulGenericClient(serverURL);
            
            client.setEncoding(EncodingEnum.JSON);
            
            Appointment apt = new Appointment();
            
    		Participant patient = new Participant();
    		patient.setActor(new ResourceReferenceDt(patientId));
    		patient.setRequired(ParticipantRequiredEnum.REQUIRED);
            apt.getParticipant().add(patient);
            
            Participant doctor1 = new Participant();
            doctor1.setActor(new ResourceReferenceDt("T3Mz3KLBDVXXgaRoee3EKAAB"));
    		doctor1.setRequired(ParticipantRequiredEnum.REQUIRED);
            apt.getParticipant().add(doctor1);
            
            Participant doctor2 = new Participant();
            doctor2.setActor(new ResourceReferenceDt("TESU2bZxkH4ZTTEZCHK.p1wB"));
    		doctor2.setRequired(ParticipantRequiredEnum.OPTIONAL);
            apt.getParticipant().add(doctor1);

            apt.setComment("Regular yearly visit");
            CodeableConceptDt reasonCode = new CodeableConceptDt();
            reasonCode.setText("Regular checkup");
            apt.setReason(reasonCode);
            
            apt.getSlot().add(new ResourceReferenceDt(slot.getId().getIdPart()));
            
            MethodOutcome outcome = client.create().resource(apt).execute();
            System.out.println("Appointment Result Created = " + outcome.getCreated());
        }

        
		/**
		 * @param fhirContext
		 * @param serverURL
		 * @param patientId
		 */
		protected Slot lookupSlot(FhirContext fhirContext, String serverURL, String patientId, Schedule schedule) {
			IGenericClient client = fhirContext.newRestfulGenericClient(serverURL);
            
            client.setEncoding(EncodingEnum.JSON);
            
            Bundle slotBundle = 
            			client
            			.search()
            			.forResource(Slot.class)
            			.and(Slot.SCHEDULE.hasId(schedule.getId().getIdPart()))
            			.returnBundle(Bundle.class)
            			.execute();
            
            
            Slot slot = null;
            
            for (Entry entry : slotBundle.getEntry()) {
            	IResource slotRes = entry.getResource();
            	if ( slotRes instanceof Slot) {
            		slot = (Slot)slotRes;
            		System.out.println("slot found = " + slot.getId().getIdPart());
            		break;
            	}
            }
            
            return slot;
		}

		
		
		/**
		 * @param fhirContext
		 * @param serverURL
		 * @param patientId
		 */
		protected Schedule lookupSchedule(FhirContext fhirContext, String serverURL, String patientId) {
			IGenericClient client = fhirContext.newRestfulGenericClient(serverURL);
            
            client.setEncoding(EncodingEnum.JSON);
            
            Bundle scheduleBundle = client
            			.search()
            			.forResource(Schedule.class)
            			.where(Schedule.ACTOR.hasId(patientId))
            			.and(Schedule.TYPE.exactly().code("1004"))
            			.and(Schedule.DATE.exactly().day("20160718"))
            			.returnBundle(Bundle.class)
            			.execute();
            
            
            Schedule schedule = null;
            
            for (Entry entry : scheduleBundle.getEntry()) {
            	IResource scheduleRes = entry.getResource();
            	if ( scheduleRes instanceof Schedule) {
            		schedule = (Schedule)scheduleRes;
            		System.out.println("schedule found = " + schedule.getId().getIdPart());
            		break;
            	}
            }
            
            return schedule;
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
                    query.and(Observation.CODE.exactly().code(code));
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
