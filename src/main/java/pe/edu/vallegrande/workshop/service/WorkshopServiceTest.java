package pe.edu.vallegrande.workshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.workshop.dto.WorkshopKafkaEventDto;
import pe.edu.vallegrande.workshop.model.Workshop;
import pe.edu.vallegrande.workshop.repository.WorkshopRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkshopServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private WorkshopService workshopService;

    private Workshop sampleWorkshop;

    @BeforeEach
    void setUp() {
        sampleWorkshop = new Workshop();
        sampleWorkshop.setId(1L);
        sampleWorkshop.setName("Test Workshop");
        sampleWorkshop.setDescription("Descripción");
        sampleWorkshop.setStartDate(LocalDate.now());
        sampleWorkshop.setEndDate(LocalDate.now().plusDays(1));
        sampleWorkshop.setState("A");
        sampleWorkshop.setPersonId(100L);
    }

    @Test
    void testCreateWorkshop() {
        when(workshopRepository.save(any(Workshop.class))).thenReturn(Mono.just(sampleWorkshop));
        doNothing().when(kafkaProducerService).sendWorkshopEvent(any(WorkshopKafkaEventDto.class));

        Mono<Workshop> result = workshopService.createWorkshop(sampleWorkshop);

        StepVerifier.create(result)
            .expectNextMatches(w -> w.getName().equals("Test Workshop"))
            .verifyComplete();

        verify(kafkaProducerService, times(1)).sendWorkshopEvent(any());
    }

    @Test
    void testFindAllWorkshop() {
        when(workshopRepository.findAll()).thenReturn(Flux.just(sampleWorkshop));

        Flux<Workshop> result = workshopService.findAllWorkshop();

        StepVerifier.create(result)
            .expectNext(sampleWorkshop)
            .verifyComplete();
    }
}
