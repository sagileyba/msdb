package com.philips.project.msdb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.philips.project.msdb.beans.Person;
import com.philips.project.msdb.services.PersonService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;


@RestController
@RequestMapping("api")
public class PersonController {

	@Autowired
	private PersonService personService;
	@Autowired
	private RestTemplate client;
	private static String report_URL = "http://localhost:8081/report/";


	@GetMapping("get/All")
	public Iterable<Person> getAll()
	{
		return this.personService.getAllPersons();
	}

	@PostMapping("add/person")
	public ResponseEntity<?> addPerson(@RequestBody Person person)
	{
		try {
			personService.addPerson(person);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("Error");
			return new ResponseEntity<Exception>(e,HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@DeleteMapping("remove/patient/{personId}")
	public ResponseEntity<?> removePerson(@PathVariable int personId)
	{
		personService.removePerson(personId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping("update/patient/result/{personId}/{result}")
	public ResponseEntity<?> updatePersonResult(@PathVariable int personId,@PathVariable String result)
	{
		try {
			personService.updatePersonResult(personId, result);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Exception>(e,HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}


	@PutMapping("update/patient/result/{personId}/{date}")
	public ResponseEntity<?> updatePersonDate(@PathVariable int id,@PathVariable String date)
	{
		try {
			personService.updatePersonDate(id, date);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Exception>(e,HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * Update db from MOH API by date.
	 * this will not replace existing data
	 * //TODO: this func should replace existing data (?)
	 * @param date
	 * @return Response
	 */
	@PostMapping("update/db/{date}")
	public ResponseEntity<?> updateDBFromAPIByDate(@PathVariable String date) {
		List<Person> result = new ArrayList<>();

		if (!date.isEmpty()) {
			result = this.personService.fetchAPIData(date);
		}

		if (result.size()>0) {
			return new ResponseEntity<String>("Updated results for date: " + date, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Wrong date format", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * This function send daily data to Analytics
	 * @param date
	 */
	@GetMapping("get/db/daily/{date}")
	public void sendDailyParams(@PathVariable String date){

		System.out.println("Date: " + date );

		HashMap<String, Integer> result ;
		result = personService.sendDailyParams(date);


		int positives = result.get("positives");
		int numberOfPCRs = result.get("numberOfPCRs");
		String url = report_URL+"daily/"+date+"/"+positives+"/"+numberOfPCRs;

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

		this.client.postForEntity(url,HttpMethod.POST,String.class,result);

	}
	
	@GetMapping("get/postive")
	public ResponseEntity<Integer> getPostive()
	{
		int postive = personService.getPostive();
		return new ResponseEntity<Integer>(postive,HttpStatus.OK);

	}
	@GetMapping("get/negative")
	public ResponseEntity<Integer> getNegative()
	{
		int negative = personService.getNegative();
		return new ResponseEntity<Integer>(negative,HttpStatus.OK);
	}
}

