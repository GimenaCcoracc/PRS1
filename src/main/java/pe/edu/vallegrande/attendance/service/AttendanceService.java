package pe.edu.vallegrande.attendance.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.attendance.model.Attendance;
import pe.edu.vallegrande.attendance.repository.AttendanceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Flux<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Mono<Attendance> getAttendanceById
    (Long id) {
        return attendanceRepository.findById(id);
    }

    public Flux<Attendance> getAttendanceByPerson(Long personId) {
        return attendanceRepository.findByPersonId(personId);
    }

    public Flux<Attendance> getAttendanceByTopic(Long topicId) {
        return attendanceRepository.findByIssueId(topicId);
    }

    public Mono<Attendance> saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Mono<Attendance> updateAttendance(Long id, Attendance updatedAttendance) {
        return attendanceRepository.findById(id)
                .flatMap(existingAttendance -> {
                    existingAttendance.setIssueId(updatedAttendance.getIssueId());
                    existingAttendance.setPersonId(updatedAttendance.getPersonId());
                    existingAttendance.setEntryTime(updatedAttendance.getEntryTime());
                    existingAttendance.setJustificationDocument(updatedAttendance.getJustificationDocument());
                    return attendanceRepository.save(existingAttendance);
                });
    }

    public Mono<Void> deleteById(Long id) {
        return attendanceRepository.deleteById(id);  
    }
    

    public Mono<Attendance> logicalDelete(Long id) {
        return attendanceRepository.findById(id)
        .flatMap(attendance -> {
            attendance .setState("I");
            return attendanceRepository.save(attendance);
        });
    }

}
