package com.philips.project.msdb.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.philips.project.msdb.beans.Person;
import org.springframework.data.repository.query.Param;


public interface PersonRepository extends JpaRepository<Person, Integer>{

//	public Person findBy_Id(int id);

//	public Person findByResult(String result);
//	public Person findByGender(String gender);
//	public Person findByResultDate(String date);


	@Transactional
	@Modifying
	@Query(value = "update person set result=:result where id=:id", nativeQuery=true)
	public int updatePersonResult(int id,String result);

	@Transactional
	@Modifying
	@Query(value = "update person set date=:date where id=:id", nativeQuery=true)
	public int updatePersonDate(int id,String date);

	@Query(value = "SELECT * FROM person where date=:date", nativeQuery=true)
	public List<Person> findByResultDate(String date);

//	@Transactional
//	@Modifying
//	@Query("select case when count(p)> 0 then true else false end from Person p where p.date=:date")
//	public int existsPersonByResultDate(String date);

	@Query(value = "select COUNT(result) from person where area=:area and date=:date and result=:boolresult", nativeQuery=true)
	public int getPostiveOfCorona(int area,String date,boolean boolresult);
	
	@Query(value = "select COUNT(result) from person where result=:postive", nativeQuery=true)
	public int getPostive(boolean postive);
	
	@Query(value = "select COUNT(result) from person where result=:negative", nativeQuery=true)
	public int getnegative(boolean negative);
}
