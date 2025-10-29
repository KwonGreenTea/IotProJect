package com.iot.web.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TEST_DATA")  // JPA @Table 사용
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_SEQ")
    @SequenceGenerator(name = "TEST_SEQ", sequenceName = "TEST_DATA_SEQ", allocationSize = 1)
    private int dataCd;

    private Double data1;
    private Double data2;
    private Double data3;
}
