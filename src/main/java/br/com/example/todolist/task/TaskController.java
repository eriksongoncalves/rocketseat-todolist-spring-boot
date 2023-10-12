package br.com.example.todolist.task;

import br.com.example.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var task = this.taskRepository.save(taskModel);

        task.setIdUser((UUID) request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/término deve ser maior do que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor do que a data término");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = (UUID) request.getAttribute("idUser");
        return this.taskRepository.findByIdUser(idUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = (UUID) request.getAttribute("idUser");
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null || task.getIdUser() != idUser){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada!");
        }

        Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }
}
