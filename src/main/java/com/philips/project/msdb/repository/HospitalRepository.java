package com.philips.project.msdb.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.philips.project.msdb.beans.AreaEnum;
import com.philips.project.msdb.beans.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

	public Hospital findById(int id);
	public Hospital findByName(String name);
	public Hospital findByContactEmail(String email);
	public Hospital findByNumOfBeds(int numOfBeds);
	public Hospital findByArea(AreaEnum area);

	
	@Transactional
	@Modifying
	@Query(value = "update hospital set num_Of_Beds=:number_Of_Beds where id=:id", nativeQuery=true)
	public int updateNumber_Of_Beds(int id,int number_Of_Beds);
	
	@Query(value = "select SUM(num_of_beds) from hospital", nativeQuery=true)
	public int getNumber_Of_Beds();
}
