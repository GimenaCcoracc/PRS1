package pe.edu.vallegrande.workshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.workshop.dto.WorkshopKafkaEventDto;
import pe.edu.vallegrande.workshop.model.Workshop;
import pe.edu.vallegrande.workshop.repository.WorkshopRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkshopServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private WorkshopService workshopService;

    private Workshop workshop;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        workshop = new Workshop();
        workshop.setId(1L);
        workshop.setName("Curso de Java");
        workshop.setDescription("Aprende Java desde cero");
        workshop.setStartDate(LocalDate.of(2025, 5, 1));
        workshop.setEndDate(LocalDate.of(2025, 5, 31));
        workshop.setObservation("Incluye proyectos");
        workshop.setState("A");
        workshop.setPersonId("2,10,11,19,18,17,25,24");
    }

    @Test
    public void testFindAllWorkshop() {
        when(workshopRepository.findAll()).thenReturn(Flux.just(workshop));

        StepVerifier.create(workshopService.findAllWorkshop())
                .expectNext(workshop)
                .verifyComplete();
    }

    @Test
    public void testFindStatus() {
        when(workshopRepository.findAllByState("A")).thenReturn(Flux.just(workshop));

        StepVerifier.create(workshopService.findStatus("A"))
                .expectNext(workshop)
                .verifyComplete();
    }

    @Test
public void testCreateWorkshopWithDefaultState() {
    Workshop newWorkshop = new Workshop();
    newWorkshop.setName("Nuevo Taller");

    System.out.println("Before createWorkshop: state=" + newWorkshop.getState()); // Debug

    when(workshopRepository.save(any())).thenAnswer(invocation -> {
        Workshop arg = invocation.getArgument(0);
        System.out.println("Saving workshop with state: " + arg.getState()); // Debug
        return Mono.just(arg);
    });

    doNothing().when(kafkaProducerService).sendWorkshopEvent(any());

    StepVerifier.create(workshopService.createWorkshop(newWorkshop))
            .expectNextMatches(w -> {
                System.out.println("Returned workshop with state: " + w.getState()); // Debug
                return "A".equals(w.getState());
            })
            .verifyComplete();

    verify(kafkaProducerService, times(1)).sendWorkshopEvent(any());
}



    @Test
    public void testUpdateWorkshop() {
        when(workshopRepository.save(any())).thenReturn(Mono.just(workshop));

        StepVerifier.create(workshopService.updateWorkshop(workshop))
                .expectNext(workshop)
                .verifyComplete();

        verify(kafkaProducerService, times(1)).sendWorkshopEvent(any(WorkshopKafkaEventDto.class));
    }

    @Test
    public void testLogicalDelete() {
        when(workshopRepository.findById(1L)).thenReturn(Mono.just(workshop));
        when(workshopRepository.save(any())).thenReturn(Mono.just(workshop));

        StepVerifier.create(workshopService.logicalDelete(1L))
                .expectNext(workshop)
                .verifyComplete();

        verify(kafkaProducerService, times(1)).sendWorkshopEvent(any(WorkshopKafkaEventDto.class));
    }

    @Test
    public void testRestoreWorkshop() {
        workshop.setState("I");
        when(workshopRepository.findById(1L)).thenReturn(Mono.just(workshop));
        when(workshopRepository.save(any())).thenReturn(Mono.just(workshop));

        StepVerifier.create(workshopService.restoreWorkshop(1L))
                .expectNext(workshop)
                .verifyComplete();

        verify(kafkaProducerService, times(1)).sendWorkshopEvent(any(WorkshopKafkaEventDto.class));
    }

    @Test
    public void testDeleteById() {
        when(workshopRepository.findById(1L)).thenReturn(Mono.just(workshop));
        when(workshopRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(workshopService.deleteById(1L))
                .verifyComplete();

        verify(kafkaProducerService, times(1)).sendWorkshopEvent(any(WorkshopKafkaEventDto.class));
    }

    @Test
    public void testInactiveWorkshop() {
        when(workshopRepository.inactiveWorkshop(1L)).thenReturn(Mono.empty());

        StepVerifier.create(workshopService.inactiveWorkshop(1L))
                .verifyComplete();
    }

    @Test
    public void testFindById() {
        when(workshopRepository.findById(1L)).thenReturn(Mono.just(workshop));

        StepVerifier.create(workshopService.findById(1L))
                .expectNext(workshop)
                .verifyComplete();
    }
}
