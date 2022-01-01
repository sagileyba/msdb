package com.philips.project.msdb.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.javafaker.Faker;
import com.philips.project.msdb.beans.AreaEnum;
import com.philips.project.msdb.beans.Hospital;
import com.philips.project.msdb.repository.HospitalRepository;
import com.philips.project.msdb.repository.PersonRepository;

@Service
public class HospitalService {
    @Autowired
    HospitalRepository hospitalRepository;
	@Autowired
	private PersonRepository personRepo;
	@Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RestTemplate client;
	int cntHospital=0;
	String dateToday="";
	private static String report_URL = "https://covid21analytics.herokuapp.com/report/report/";
	
    public void addHospital(Hospital hospital) throws Exception {
        Hospital existing = this.hospitalRepository.findById(hospital.getId());
        if (existing!=null) {
            throw new Exception("Hospital with id " + hospital.getId() + " already exists");
        }
        this.hospitalRepository.save(hospital);
    }

    public void updateNumber_Of_Beds(int id, int number_Of_Beds) throws Exception {
		if (number_Of_Beds < 0) {
			throw new Exception("Error number_Of_Beds");
		}
		int res = this.hospitalRepository.updateNumber_Of_Beds(id, number_Of_Beds);
		if (res == 0) {
			throw new Exception("Hospital was not found");
		}
	}

	public void removeHospital(int hospitalId) {
		this.hospitalRepository.deleteById(hospitalId);
	}

	public Iterable<Hospital> getAllHospitals() {
		return this.hospitalRepository.findAll();
	}
    // https://stackoverflow.com/questions/26147044/spring-cron-expression-for-every-day-101am
    @Scheduled(cron = "0 0 0/1 1/1 * ?")         // this code will be executed every hour
	public synchronized int sendWarningReports() {	
		int [] NumOfBeds = new int[3];  // NumOfBeds in North , South , Center
		int [] postiveRes = new int [3];  // postiveRes in North , South , Center
		int i=0;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate now = LocalDate.now();
		now = now.minusDays(14L);
		for(AreaEnum areaEnum:AreaEnum.values())
		{
			if(NumOfBeds[i] == 0)
				NumOfBeds[i] = calcNumOfBeds(areaEnum.name());
			// get postive person number in area in date 
			System.out.println("Hospital two weeks ago: " +dtf.format(now));
			//postiveRes[i] = personRepo.getPostiveOfCorona(areaEnum.ordinal(),dtf.format(now),true);
	        String url = report_URL + dtf.format(now);

	        try {
	        	ResponseEntity<JSONObject> response = client.getForEntity(url ,JSONObject.class);
	        	switch(i) {
	        	  case 0:
	        		  postiveRes[i] = (int) response.getBody().get("northCount");
	        	    break;
	        	  case 1:
	        		  postiveRes[i] = (int) response.getBody().get("southCount");
	        	    break;
	        	  case 2:
	        		  postiveRes[i] = (int) response.getBody().get("centerCount");
		        	    break;
	        	  default:
	        	    // code block
	        	}
	        } catch (Exception e) {
	            System.out.println("Error getting from analytics" + e);
	        }

			System.out.println("postiveRes  "+postiveRes[i] + "  NumOfBeds " + NumOfBeds[i]);
			
			if(cntHospital > 0 && (dateToday.equals(dtf.format(now))==false))
			{
				cntHospital=0;
			}
			if(postiveRes[i] > NumOfBeds[i] && NumOfBeds[i]!=0 && cntHospital == 0)
			{
				Iterable<Hospital> hospitals = getAllHospitals();
					for (Hospital hospital : hospitals) {
						if(hospital.getArea().ordinal() == areaEnum.ordinal())
						{
							String email = hospital.getContactEmail();
						try {
							sendEmail(email , postiveRes[i] , NumOfBeds[i] , areaEnum.name());
						} catch (MessagingException e) {
							e.printStackTrace();
						}
						cntHospital++;
					}
				}
			}
			if(cntHospital > 0)
			{
				dateToday = dtf.format(now);
			}
			i++;
		}
		return cntHospital;
	}
	
	public void sendEmail(String toemail,int postiveRes , int NumOfBeds , String areaName) throws MessagingException
	{
		MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		
		
        helper.setTo(toemail);

        helper.setSubject("Warning from the number of people sick with corona COVID-19");

        helper.setText("<h1>Warning from the number of people sick with corona COVID-19 in area : "+areaName+" </h1></br>"
        		+ "<h2>Number of people who received a positive answer in the test : "+postiveRes+"</br></h2>"+
        		"<h2>The number of beds in hospitals in "+areaName+" : "+NumOfBeds+"</h2>", true);

        helper.addAttachment("covid-19.jpg", new ClassPathResource("covid-19.jpg"));

        javaMailSender.send(msg);		
        System.out.println("Send mail Done");
	}
    
    
    public void setRandomData() {
        List<Hospital> hospitals = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            hospitals.add(this.getRandomHospital());
        }
        hospitalRepository.saveAll(hospitals);
    }

    private Hospital getRandomHospital() {
        Faker faker = new Faker();
        Hospital hospital = new Hospital();
        hospital.setName(faker.company().name());
        hospital.setArea(AreaEnum.generateRandomArea());
        hospital.setCityId(faker.number().numberBetween(1, 150));
        hospital.setContactEmail("mahdi19950@gmail.com");
        hospital.setNumOfBeds(faker.number().numberBetween(50, 300));

        return hospital;
    }

    public int calcNumOfBeds(String option) {
        int result = 0;
        List<Hospital> hospitals = hospitalRepository.findAll();

        option = option.toLowerCase(Locale.ROOT);

        switch (option) {
            case "north":
                for (Hospital h : hospitals) {
                    if (h.getArea() == AreaEnum.North) {
                        result += h.getNumOfBeds();
                    }
                }
                break;
            case "south":
                for (Hospital h : hospitals) {
                    if (h.getArea() == AreaEnum.South) {
                        result += h.getNumOfBeds();
                    }
                }
                break;
            case "central":
                for (Hospital h : hospitals) {
                    if (h.getArea() == AreaEnum.Central) {
                        result += h.getNumOfBeds();
                    }
                }
                break;
            default:
                for (Hospital h : hospitals) {
                    result += h.getNumOfBeds();
                }
                break;
        }
        return result;
    }

	public int getNumber_Of_Beds()
	{
		return hospitalRepository.getNumber_Of_Beds();
	}
}
