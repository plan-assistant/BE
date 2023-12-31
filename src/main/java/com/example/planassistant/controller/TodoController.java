package com.example.planassistant.controller;


import com.example.planassistant.common.CommonController;
import com.example.planassistant.domain.Todo;
import com.example.planassistant.dto.TodoReqDto;
import com.example.planassistant.dto.TodoResDto;
import com.example.planassistant.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/todo")
@Tag(name = "Todo", description = "Todo 관련 API")
public class TodoController extends CommonController {
    private final TodoService todoService;

    @PostMapping()
    @Operation(summary = "todo 생성", description = "todo를 생성 해줍니다.")
    @ApiResponse(responseCode = "201", description = "todo 생성 성공", content = @Content(schema = @Schema(implementation = TodoReqDto.class)))
    public ResponseEntity makeTodo(@AuthenticationPrincipal User user, @RequestBody TodoReqDto todoReqDto){
        log.info("todo  " + todoReqDto.toString());
        todoService.addTodo(todoReqDto, user.getUsername());
        return CreatedReturn("created");
    }
    @GetMapping()
    @Operation(summary = "todo list", description = "멤버가 만들은 todo의 list를 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "todo list를 가져옵니다.", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = TodoResDto.class)))
    })
    public ResponseEntity getAllTodo(@AuthenticationPrincipal User user, @RequestParam(required = false) Boolean complete){
        log.info("getAllTodo Call");
//        System.out.println(complete);
        var res = todoService.getAllTodo((user.getUsername()), complete);
        return OkReturn(res);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get todo by id", description = "todo를 id로 가져옵니다.")
    public ResponseEntity getTodo(@AuthenticationPrincipal User user, @PathVariable Long id){
        return OkReturn(todoService.getTodo(id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete todo by id", description = "todo를 id로 지웁니다")
    public ResponseEntity deleteTodo(@PathVariable Long id){
        todoService.deleteTodo(id);
        return OkReturn("deleted");
    }

    @Operation(summary = "Change todo complete by id", description = "todo의 complete 상태를 수정합니다")
    @PatchMapping("/{id}")
    public ResponseEntity changeTodoComplete(@PathVariable Long id){

        return AcceptedReturn(todoService.changeComplete(id));
    }

    @Operation(summary = "Change todo by id", description = "todo의 내용을 수정합니다")
    @PutMapping("/{id}")
    public ResponseEntity changeTodo(@PathVariable Long id, @RequestBody TodoReqDto todoReqDto){
        log.info("changeTodo call");
        log.info(todoReqDto.toString());
        return AcceptedReturn(todoService.changeTodo(id, todoReqDto));
    }

    @Operation(summary = "Get Todo count",
            description = "complete에 따라 todo의 개수를 가져옵니다. complete가 없으면 전체 todo의 개수" +
                    "반환값 정수")
    @GetMapping("/count")
    public ResponseEntity getTotalTodo(@AuthenticationPrincipal User user, @RequestParam(required = false) Boolean complete){
        log.info("getTotalTodo Call");
        return OkReturn(todoService.getTotalTodo(user.getUsername(), complete));
    }

}
