package uz.pdp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uz.pdp.dto.TodoDto;
import uz.pdp.dto.TodoResponseDto;
import uz.pdp.entity.ToDoEntity;
import uz.pdp.entity.UserEntity;
import uz.pdp.repository.ToDoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToDoService {

    private final ToDoRepository toDoRepository;

    private UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public TodoResponseDto create(TodoDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sarlavha (title) majburiy");
        }
        ToDoEntity todo = ToDoEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .completed(dto.isCompleted())
                .user(getCurrentUser())
                .build();
        return mapToDto(toDoRepository.save(todo));
    }

    public List<TodoResponseDto> getAllMyTodos() {
        return toDoRepository.findAllByUserId(getCurrentUser().getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public TodoResponseDto getById(Long id) {
        return mapToDto(getOwnedTodo(id));
    }

    public TodoResponseDto update(Long id, TodoDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sarlavha (title) majburiy");
        }
        ToDoEntity todo = getOwnedTodo(id);
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.isCompleted());
        return mapToDto(toDoRepository.save(todo));
    }

    public void delete(Long id) {
        toDoRepository.delete(getOwnedTodo(id));
    }

    private ToDoEntity getOwnedTodo(Long id) {
        ToDoEntity todo = toDoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vazifa topilmadi"));

        if (!todo.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu vazifa sizga tegishli emas!");
        }
        return todo;
    }

    private TodoResponseDto mapToDto(ToDoEntity todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .createdAt(todo.getCreatedAt())
                .build();
    }
}
