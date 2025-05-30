package pe.edu.vallegrande.issue.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.issue.dto.IssueKafkaEventDto;

@Service
@RequiredArgsConstructor
@Slf4j

public class kafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "issue-events";

    /**
     * 🔹 Envía un evento relacionado a un taller al topic de Kafka "issue-events".
     * Convierte el DTO a JSON, lo publica en Kafka, y muestra logs de éxito o error.
     */

     public void sendWorkshopEvent(IssueKafkaEventDto eventDto) {
        try {
            String message = objectMapper.writeValueAsString(eventDto);
            kafkaTemplate.send(TOPIC, String.valueOf(eventDto.getId()), message);
            // ✅ Log de éxito
            log.info("✅ Evento enviado a Kafka -> Topic: {}, Key: {}, Payload: {}", TOPIC, eventDto.getId(), message);
        } catch (Exception e) {
            // ❌ Log de error si falla la serialización o envío
            log.error("❌ Error al enviar evento a Kafka: {}", e.getMessage(), e);
        }
    }
}
