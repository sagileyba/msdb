package com.philips.project.msdb;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.philips.project.msdb.services.HospitalService;

@SpringBootApplication
@EnableScheduling
public class MsdbApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(MsdbApplication.class, args);
		/*HospitalService hospitalService = context.getBean(HospitalService.class);
		Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
				while(true){
					hospitalService.sendWarningReports();
                try {
					Thread.sleep(50000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
            }
		 }, "Thread 1");
        thread1.start();*/
	}

	@Bean
	public RestTemplate initRestTemplate()
	{
		return new RestTemplate();
	}
	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");  //SMTP (Simple Mail Transfer Protocol)
	    mailSender.setPort(587); //Port 587, coupled with TLS encryption, ensures that email is submitted securely and following the guidelines set out by the IETF.
	     
	    mailSender.setUsername("system.report.07@gmail.com"); //gmail mail to send from (mahdi created)
	    mailSender.setPassword("system077");
	    
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	    
	    return mailSender;
	}
}
