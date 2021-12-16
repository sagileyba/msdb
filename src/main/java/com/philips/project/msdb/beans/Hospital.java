package com.philips.project.msdb.beans;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    int cityId;
    int numOfBeds;
    String contactEmail;
    AreaEnum area;

}


