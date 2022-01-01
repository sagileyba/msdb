package com.philips.project.msdb.services;

import com.philips.project.msdb.beans.Person;
import com.philips.project.msdb.repository.HospitalRepository;
import com.philips.project.msdb.repository.PersonRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Component
public class Task implements CommandLineRunner {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    PersonService personService;
    @Autowired
    private RestTemplate client;
    @Autowired
    HospitalRepository hospitalRepository;
    @Autowired
    HospitalService hospitalService;
    private static String report_URL = "https://covid21analytics.herokuapp.com/report/";
//    private static String report_URL = "http://localhost:8081/report/";

    public Task() {

    }

    //Init DB with data from 15 past days
    @Override
    public void run(String... args) throws Exception {
        LocalDate date = LocalDate.now();
        date = date.minusDays(21L); // DEV: API get update once a week so ignore lsat 6 days.
//        date = date.minusDays(14L);

        for (int i = 0; i <= 21; i++) {
            List<Person> existing = this.personRepository.findByResultDate(date.toString());
            List<Person> personListAPI = new ArrayList<>();
            if (existing.size() == 0) {
                System.out.println(">>FETCH DATA FOR DATE: " + date);
                personListAPI = this.personService.fetchAPIData(date.toString());
                this.sendDailyParams(personListAPI, date.toString());
            } else {
                this.sendDailyParams(existing, date.toString());
                System.out.println(">>Date already exist in DB: " + date);
            }
            date = date.plusDays(1L);
        }

        if(this.hospitalRepository.findAll().size() == 0 ){
            System.out.println(">>Set random hospitals");
            this.hospitalService.setRandomData();
        }


        System.out.println("Done!");
    }  
   
    // https://stackoverflow.com/questions/26147044/spring-cron-expression-for-every-day-101am
    @Scheduled(cron = "0 0 1 * * ?")         // this code will be executed every day at 1AM
    public void resetCache() {
    	LocalDate date = LocalDate.now();
        List<Person> existing = this.personRepository.findByResultDate(date.toString());
        List<Person> personListAPI = new ArrayList<>();
        if (existing.size() == 0) {
            System.out.println(">>FETCH DATA FOR DATE: " + date);
            personListAPI = this.personService.fetchAPIData(date.toString());
            this.sendDailyParams(personListAPI, date.toString());
        } else {
            this.sendDailyParams(existing, date.toString());
            System.out.println(">>Date already exist in DB: " + date);
        }        

    }
    
    
    private void sendDailyParams( List<Person> personList, String date) {

        int positives = 0;
        int numberOfPCRs = personList.size();
        int north = 0, central = 0, south = 0;

        // Count the number of positive PCRs
        for (Person p : personList) {
            if (p.isBool_of_corona()) {
                positives++;
                switch (p.getArea().name().toLowerCase(Locale.ROOT)) {
                    case "north":
                        north++;
                        break;
                    case "south":
                        south++;
                        break;
                    case "central":
                        central++;
                        break;
                }
            }
        }

        JSONObject jo = new JSONObject();
        jo.put("date", date);
        jo.put("positives", positives);
        jo.put("numberOfPCRs", numberOfPCRs);
        jo.put("north", north);
        jo.put("south", south);
        jo.put("central", central);


        String url = report_URL + "daily";

        try {
            this.client.postForObject(url, jo, String.class);
        } catch (Exception e) {
            System.out.println("Error posting to analytics" + e);
        }

    }
}
