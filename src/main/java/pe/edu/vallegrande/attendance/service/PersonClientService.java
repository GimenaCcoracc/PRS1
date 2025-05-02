package pe.edu.vallegrande.attendance.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.attendance.dto.PersonDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonClientService {

    private final WebClient personWebClient;

    public Flux<PersonDTO> getAllPersons() {
        return personWebClient.get()
                .retrieve()
                .bodyToFlux(PersonDTO.class);
    }

    public Mono<PersonDTO> getPersonById(Long id) {
        return personWebClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(PersonDTO.class);
    }
}