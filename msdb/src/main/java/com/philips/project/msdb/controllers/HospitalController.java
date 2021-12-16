package com.philips.project.msdb.controllers;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philips.project.msdb.services.HospitalService;

@RestController
@RequestMapping("hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/randomize")
    public void randomizeHospitals(){
        hospitalService.setRandomData();
    }


    //option = north / south / central / all
    @GetMapping("/beds/{option}")
    public ResponseEntity<Integer> sendNumOfBeds(@PathVariable String option){
        int result = hospitalService.calcNumOfBeds(option);
        return new ResponseEntity<Integer>(result,HttpStatus.OK);
    }
    
    @GetMapping("send")
    public ResponseEntity<Integer> sendWarningReports(){
    	int result = hospitalService.sendWarningReports();
        return new ResponseEntity<Integer>(result,HttpStatus.OK);
    }
   
    
    @GetMapping("sendEmail")
    public ResponseEntity<Integer> sendEmail(){
    	try {
			hospitalService.sendEmail("mahde19950@gmail.com",10,9,"North");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
        return new ResponseEntity<Integer>(1,HttpStatus.OK);
    }
    
    @PutMapping("update/patient/result")
	public ResponseEntity<?> updateNumber_Of_Beds(int hospitalId, int number_Of_Beds)
	{
		try {
			hospitalService.updateNumber_Of_Beds(hospitalId, number_Of_Beds);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Exception>(e,HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
    
    @GetMapping("get/Number_Of_Beds")
    public ResponseEntity<Integer> getNumber_Of_Beds()
    {
    	int negative = hospitalService.getNumber_Of_Beds();
		return new ResponseEntity<Integer>(negative,HttpStatus.OK);
    }
    
}
