package uz.pdp.controller;

import uz.pdp.dto.TodoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

    @RestController
    @RequestMapping("/api/todos")
    public class ToDoController {

        private final List<TodoDto> todoList = new ArrayList<>();


        @PostMapping
        public ResponseEntity<String> create(@RequestBody TodoDto dto) {
            if (dto.getTitle() == null || dto.getTitle().isBlank()) {
                return ResponseEntity.badRequest().body("Sarlavha (title) yozilishi shart!");
            }
            todoList.add(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Vazifa qo'shildi: " + dto.getTitle());
        }


        @GetMapping
        public ResponseEntity<List<TodoDto>> getAll() {
            return ResponseEntity.ok(todoList);
        }


    }


