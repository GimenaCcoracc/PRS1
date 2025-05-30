package pe.edu.vallegrande.attendance.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.attendance.dto.IssueKafkaEventDto;
import pe.edu.vallegrande.attendance.model.Issue;
import pe.edu.vallegrande.attendance.repository.IssueRepository;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final IssueRepository issueRepository;
    private final ObjectMapper objectMapper;
    private final R2dbcEntityTemplate template;

    /**
     * 🔹 Escucha eventos del topic "issue-events" y sincroniza la información.
     */
    @KafkaListener(topics = "issue-events", groupId = "attendance-group")
    public void consumeIssueEvent(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            IssueKafkaEventDto dto = objectMapper.readValue(json, IssueKafkaEventDto.class);
            log.info("📥 Recibido evento Kafka (Issue): {}", dto);

            // 🔄 Construye la entidad Issue desde el DTO
            Issue issue = Issue.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .workshopId(dto.getWorkshopId())
                    .sesion(dto.getSesion())
                    .scheduledTime(dto.getScheduledTime())
                    .state(dto.getState())
                    .build();

            // 💾 Si ya existe, actualiza; si no, inserta nuevo registro
            issueRepository.findById(dto.getId())
                .flatMap(existing -> {
                    existing.setName(issue.getName());
                    existing.setWorkshopId(issue.getWorkshopId());
                    existing.setSesion(issue.getSesion());
                    existing.setScheduledTime(issue.getScheduledTime());
                    existing.setState(issue.getState());
                    return issueRepository.save(existing); // ✅ UPDATE
                })
                .switchIfEmpty(Mono.defer(() ->
                        template.insert(Issue.class).using(issue) // ✅ INSERT
                ))
                .subscribe(saved ->
                        log.info("✅ Issue insertado/actualizado: {}", saved)
                );

        } catch (Exception e) {
            log.error("❌ Error procesando evento Kafka {}", e.getMessage(), e);
        }
    }
}
