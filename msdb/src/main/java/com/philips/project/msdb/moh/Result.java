package com.philips.project.msdb.moh;

import java.util.List;

import org.springframework.stereotype.Component;

import com.philips.project.msdb.beans.Person;

import lombok.Data;

@Component
@Data
public class Result {

	private List<Person> records;
}
