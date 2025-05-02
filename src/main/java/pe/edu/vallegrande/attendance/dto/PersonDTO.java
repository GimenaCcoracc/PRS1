package pe.edu.vallegrande.attendance.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PersonDTO {
    private Integer idPerson;
    private String name;
    private String surname;
    private Integer age;
    private LocalDate birthdate;
    private String typeDocument;
    private String documentNumber;
    private String typeKinship;
    private String sponsored;
    private String state;
    private Integer familyIdFamily;
}