package pe.edu.vallegrande.attendance.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.attendance.model.Issue;

@Repository
public interface IssueRepository extends ReactiveCrudRepository<Issue, Long>{
    
}
