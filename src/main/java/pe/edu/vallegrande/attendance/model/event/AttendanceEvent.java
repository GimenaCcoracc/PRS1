package pe.edu.vallegrande.attendance.model.event;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AttendanceEvent {
    private Long id;
    private Long issueId;
    private Long personId;
    private LocalDateTime entryTime;
    private String record;
    private String justificationDocument;
    private String state;
}
