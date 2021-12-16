package com.philips.project.msdb.moh;

import java.util.List;

import org.springframework.stereotype.Component;

import com.philips.project.msdb.beans.Person;

import lombok.Data;
import lombok.NoArgsConstructor;
@Component
@Data
@NoArgsConstructor
public class Record {
	private List<Person> persons;

}
