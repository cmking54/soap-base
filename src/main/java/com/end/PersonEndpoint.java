package com.end;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.example.person.CreatePersonRequest;
import org.example.person.CreatePersonResponse;
import org.example.person.DeletePersonRequest;
import org.example.person.DeletePersonResponse;
import org.example.person.GetPersonRequest;
import org.example.person.GetPersonResponse;
import org.example.person.Person;
import org.example.person.UpdatePersonRequest;
import org.example.person.UpdatePersonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.domain.PersonModel;
import com.domain.PersonRepository;
@Endpoint
public class PersonEndpoint {
	private static final String NAMESPACE_URI = "http://www.example.org/person";
	
	private PersonRepository personRepository; // use the PersonRepository to query database 
												// from the PersonEndpoint. 

	@Autowired
	public PersonEndpoint(PersonRepository personRepository) {
		this.personRepository = personRepository;
	} 
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreatePersonRequest")
	@ResponsePayload
	public CreatePersonResponse createPerson(@RequestPayload CreatePersonRequest request) {
		PersonModel personModel = new PersonModel(); // create a personModel (which is a representation of a table row)
		personModel.setFirstName(request.getFirstName());
		personModel.setLastName(request.getLastName()); // Populate it with data from the request
		personModel.setDob(toDate(request.getDob()));

		PersonModel model = personRepository.save(personModel); // Save the person model to the database
		// Save the person model to the database
		
		Person person = null;
		if (model != null) {
			person = toDto(model);
		}

		CreatePersonResponse response = new CreatePersonResponse();
		// Use the object to build a CreatePersonReponse
		response.setPerson(person);
		return response; // Return the response to the caller
	}


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPersonRequest")
	@ResponsePayload
	public GetPersonResponse getPerson(@RequestPayload GetPersonRequest request) {

		Optional<PersonModel> optionalPerson = personRepository.findById(Long.valueOf(request.getId()));

		PersonModel model = optionalPerson.get();

		Person person = null;
		if (model != null) {
			person = toDto(model);
		}

		GetPersonResponse response = new GetPersonResponse();
		response.setPerson(person);

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdatePersonRequest")
	@ResponsePayload
	public UpdatePersonResponse updatePerson(@RequestPayload UpdatePersonRequest request) {
		UpdatePersonResponse response = new UpdatePersonResponse();
		Optional<PersonModel> optionalPerson = personRepository.findById(Long.valueOf(request.getId()));
		PersonModel model = optionalPerson.get();

		Person person = null;
		if (model != null) {
			String firstName = request.getFirstName();
			if (firstName != null) {
				model.setFirstName(firstName);
			}
			String lastName = request.getLastName();
			if (lastName != null) {
				model.setLastName(lastName);
			}

			XMLGregorianCalendar dob = request.getDob();
			if (dob != null) {
				model.setDob(toDate(dob));
			}

			PersonModel updatedModel = personRepository.save(model);
			if (updatedModel != null) {
				person = toDto(updatedModel);
			}
		}

		response.setPerson(person);

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeletePersonRequest")
	@ResponsePayload
	public DeletePersonResponse deletePerson(@RequestPayload DeletePersonRequest request) {

		Optional<PersonModel> optionalPerson = personRepository.findById(Long.valueOf(request.getId()));
		PersonModel personModel = optionalPerson.get();
		Person person = null;
		if (personModel != null) {
			personRepository.deleteById(Long.valueOf(request.getId()));
			person = toDto(personModel);
		}

		DeletePersonResponse response = new DeletePersonResponse();
		response.setPerson(person);

		return response;
	}

	//---
	/*
	 * Converts XMLGregorianCalendar to java.util.Date in Java
	 */
	private static Date toDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private static Person toDto(PersonModel model) {
		Person person = new Person();
		person.setId(model.getId().intValue());
		person.setFirstName(model.getFirstName());
		person.setLastName(model.getLastName());
		if (model.getDob() != null) {
			person.setDob(toXMLGregorianCalendar(model.getDob()));
		}
		return person;
	}
	/*
	 * Converts java.util.Date to javax.xml.datatype.XMLGregorianCalendar
	 */
	private static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
		GregorianCalendar gCalendar = new GregorianCalendar();
		gCalendar.setTime(date);
		XMLGregorianCalendar xmlCalendar = null;
		try {
			xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
		} catch (DatatypeConfigurationException ex) {
		}
		return xmlCalendar;
	}
}
