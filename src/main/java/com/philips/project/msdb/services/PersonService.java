package com.philips.project.msdb.services;


import java.util.*;

import com.philips.project.msdb.beans.AreaEnum;

import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.philips.project.msdb.beans.Person;
import com.philips.project.msdb.moh.BaseData;
import com.philips.project.msdb.repository.PersonRepository;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class PersonService implements Validator {

	@Autowired
	private PersonRepository personRepo;
	@Autowired
	private RestTemplate client;


	public void addPerson(Person person) throws Exception {
		Optional<Person> existing = this.personRepo.findById(person.get_id());
		if (!existing.isPresent()) {
			throw new Exception("Person with id " + person.get_id() + " already exists");
		}
		this.personRepo.save(person);
	}

	public void updatePersonResult(int id, String result) throws Exception {
		if (result.length() == 0) {
			throw new Exception("Error quantity");
		}
		int res = this.personRepo.updatePersonResult(id, result);
		if (res == 0) {
			throw new Exception("Person was not found");
		}
	}

	public void updatePersonDate(int id, String date) throws Exception {
		if (!this.validateDateFormat(date)) {
			throw new Exception("Error date");
		}
		int res = this.personRepo.updatePersonDate(id, date);
		if (res == 0) {
			throw new Exception("Person was not found");
		}
	}

	public void removePerson(int productId) {
		this.personRepo.deleteById(productId);
	}

	public Iterable<Person> getAllPersons() {
		return this.personRepo.findAll();
	}

	//date format: yyyy-MM-dd
	public List<Person> fetchAPIData(String date) {

		List<Person> personsList = new ArrayList<>();
		//Validate date format
		if (!this.validateDateFormat(date)) {
			return personsList;
		}
		String criterion = "{\"result_date\":" + "\"" + date + "\"" + "}";
		String url = "https://data.gov.il/api/action/datastore_search?resource_id=dcf999c1-d394-4b57-a5e0-9d014a62e046&limit=20000";

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters", criterion);

		ResponseEntity<BaseData> baseData = this.client.getForEntity(builder.build().toUri(), BaseData.class);

		List<Person> persons = baseData.getBody().getResult().getRecords();

		for (Person person : persons) {
			//Get random fields: (Dev operation)
			this.randomizePersonData(person);
			if (person.getTest_for_corona_diagnosis().equals("0")) {
				person.setBool_of_corona(true);
			} else
				person.setBool_of_corona(false);

			personsList.add(person);
//			personRepo.save(person);
		}
		personRepo.saveAll(personsList);
		return personsList;
	}

	/**
	 * Get a summary of data for a requested date
	 *
	 * @param date - requested date
	 * @return positives, totalNumberOfPCRs
	 */
	public HashMap<String, Integer> sendDailyParams(String date) {
		if (!this.validateDateFormat(date)) {
			return new HashMap<>();
		}
		int positives = 0, numberOfPCRs = 0;
		HashMap<String, Integer> results = new HashMap<>();
		List<Person> persons = personRepo.findByResultDate(date);
		numberOfPCRs = persons.size();

		// Count the number of positive PCRs
		for (Person p : persons) {
			if (p.isBool_of_corona()) {
				positives++;
			}
		}

		results.put("positives", positives);
		results.put("numberOfPCRs", numberOfPCRs);
		return results;
	}

	@Override
	public boolean validateDateFormat(String dateStr) {
		return GenericValidator.isDate(dateStr, "yyyy-MM-dd", true);
	}


	public class PersonList {
		private List<Person> persons;

		public PersonList() {
			persons = new ArrayList<>();
		}

		public List<Person> getPersons() {
			return persons;
		}

		public void setPersons(List<Person> persons) {
			this.persons = persons;
		}
	}


	private Person randomizePersonData(Person person) {
		person.setArea(AreaEnum.generateRandomArea());
		return person;
	}
	
	public int getPostive()
	{
		return personRepo.getPostive(true);
	}
	public int getNegative()
	{
		return personRepo.getnegative(false);
	}
}
