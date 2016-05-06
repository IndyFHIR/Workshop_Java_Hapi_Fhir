package org.indyfhir.hapicli;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Hapi-FHIR Client Sample Project
 *
 */
public class App {

	String serverBase = "http://fhirtest.uhn.ca/baseDstu2";

	public static void main(String[] args) {
	
		// FHIR Context that is a heavy object to create so try to have just one instance shared per application.
		FhirContext fhirContext = new FhirContext(FhirVersionEnum.DSTU2);		// This explicitly tells the context you are working with DSTU2 requests / model.

		// Configure RestfulClientFactory settings like timeouts.
		fhirContext.getRestfulClientFactory().setConnectTimeout(60000); // Set timeout for connecting to resource provider.
		fhirContext.getRestfulClientFactory().setConnectionRequestTimeout(60000);	// Set timeout for waiting for request to be completed.
																										// If you are accessing a service with large results you will want to set this.
		App app = new App();

		app.retrieveAndDumpObservation(fhirContext);
	}
 
	protected void retrieveAndDumpObservation(FhirContext fhirContext) {

		// Create the Client for a target resource provider.
		IGenericClient client = fhirContext.newRestfulGenericClient(serverBase);

		// Retrieve Observations. 
		Bundle bundle = client
					.search()								// Search, vs. .read(), .create(), .update(), .delete(), .history(), ...
					.forResource(Observation.class)	// Specifying this search is for a specific type of Resource - in this case Observation, vs. MedicationOrder, DiagnosticReport, ....
																// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
																// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
					.returnBundle(Bundle.class)		// This specifies to return a Bundle of specific type. You can also control this via the fhir Context - see above.
																// You can also search a Named search that can be used for non-resource such as for custom CDS event based queries.
					.execute();								// Execute the search! Returns a Bundle Object. A read() would return just that resource instance.

		for (Entry entry : bundle.getEntry()) {

			Observation obs = (Observation)entry.getResource();

			QuantityDt obsValue = (QuantityDt)obs.getValue();
			String obsValueStr = obsValue == null ? "N/A" : (obsValue.getValue().toPlainString() + " " + obsValue.getUnit());

			DateTimeDt obsEffectiveDate = (DateTimeDt)obs.getEffective();
			String obsEffectiveDateStr = obsEffectiveDate == null ? "N/A" : obsEffectiveDate.getValueAsString();

			System.out.println(String.format("\n-----\nURL=%s\nText=%s\nFirst code=%s\nvalueQty=%s\neffectiveDate=%s", serverBase,
						obs.getText(), obs.getCode().getText(), obsValueStr, obsEffectiveDateStr));
		}
	}
}
