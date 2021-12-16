package com.philips.project.msdb.moh;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class BaseData {

	private String help;
	private Result result;
}
