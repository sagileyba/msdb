package com.philips.project.msdb.beans;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {	
	
	@Id
	@Column(name="id")
	private int _id;
	@Column(name="date")
	private String result_date;
	@Column(name="result")
	private boolean bool_of_corona;
	private String test_for_corona_diagnosis;
	private String gender;
	private int idCity; // E-relevant
	private AreaEnum area;

}

