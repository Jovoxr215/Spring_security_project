package uz.pdp.repository;

import uz.pdp.entity.ToDoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDoEntity, Long> {


    List<ToDoEntity> findAllByUserId(Long userId);



}
