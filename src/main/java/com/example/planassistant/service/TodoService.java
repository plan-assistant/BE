package com.example.planassistant.service;

import com.example.planassistant.domain.Todo;
import com.example.planassistant.dto.TodoReqDto;
import com.example.planassistant.dto.TodoResDto;
import com.example.planassistant.repository.CategoryRepository;
import com.example.planassistant.repository.MemberRepository;
import com.example.planassistant.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodoService {
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    // todo 저장
    @Transactional
    public void addTodo(TodoReqDto todoReqDto, String memberId){
        log.info("todo의 정보: " + todoReqDto.toString());
        var member = memberRepository.findById(memberId)
                .orElseThrow(()->new NoSuchElementException("cannot find member"));

        var todo = Todo.builder()
                .priority(todoReqDto.getPriority())
                .place(todoReqDto.getPlace())
                .deadline(todoReqDto.getDeadline())
                .content(todoReqDto.getContent())
                .latitude(todoReqDto.getLatitude())
                .longitude(todoReqDto.getLongitude())
                .category(todoReqDto.getCategory())
                .member(member)
                .build();

        todoRepository.save(todo);
    }

    // 멤버의 모든 todo 가져오기 or complete 값에 따라 가져오기
    @Transactional(readOnly = true)
    public List<TodoResDto> getAllTodo(String memberId, Boolean complete){

        var member = memberRepository.findById(memberId)
                .orElseThrow(()-> new NoSuchElementException("cannot find member"));
        List<Todo> todos;
        if (complete == null){
            todos = todoRepository.findTodoByMemberOrderByUpdateDate(member);
        }
        else {
            todos = todoRepository.findByMemberAndCompleteOrderByUpdateDate(member, complete);
        }

        List<TodoResDto> todoResDtoList = new ArrayList<>();
        for(Todo x: todos){


            // category 없는 경우 디폴트 설정 추가
            if (x.getCategory() == null || x.getCategory().equals("")){
                var resTodoDto = new TodoResDto(x);
                resTodoDto.setExpectTime(60.0);
                todoResDtoList.add(resTodoDto);
            }
            // category 있는 expectTime 값을 가져오기

            else {
                var category = categoryRepository.findByMemberAndName(member, x.getCategory())
                        .orElseThrow(()->new NoSuchElementException("cannot find category"));
                var todoDto =new TodoResDto(x);
                todoDto.setCategory(category.getName());
                if (category.getExpectTime() == null || category.getExpectTime() == 0){
                    todoDto.setExpectTime(60.0);
                }
                else{

                    var expectTime = (Math.round(category.getExpectTime() / 10.0) * 10);
                    todoDto.setExpectTime((double) expectTime);

                }
                todoResDtoList.add(todoDto);
            }



        }
        return todoResDtoList;
    }

    // 특정 todo 가져오기
    @Transactional(readOnly = true)
    public TodoResDto getTodo(Long todoId){
        var todo =todoRepository.findById(todoId).orElseThrow(
                ()->new NoSuchElementException("cannot find todo"));


        
        return new TodoResDto(todo);
    }

    @Transactional
    public void deleteTodo(Long id) {

        todoRepository.deleteById(id);
    }

    @Transactional
    public Boolean changeComplete(Long id){
        var todo =todoRepository.findById(id).orElseThrow(
                ()-> new NoSuchElementException("cannot find todo")
        );
        // todo 가 true면 false로 변경
        if (!todo.getComplete()){
            todo.setComplete(true);
        }
        // todo 가 false면 true로 변경
        else {
            todo.setComplete(false);
        }

        return todo.getComplete();
    }

    @Transactional
    public TodoResDto changeTodo(Long id, TodoReqDto todoReqDto){
        var todo =todoRepository.findById(id).orElseThrow(
                ()-> new NoSuchElementException("cannot find todo")
        );
        todo.setContent(todoReqDto.getContent());
        todo.setLatitude(todoReqDto.getLatitude());
        todo.setDeadline(todoReqDto.getDeadline());
        todo.setPriority(todoReqDto.getPriority());

        // 장소 값은 받은 값이 다른 경우에만 수정하도록 하기
        if (!todoReqDto.getPlace().equals(todo.getPlace())){
            todo.setPlace(todoReqDto.getPlace());
        }
        if (!todoReqDto.getLatitude().equals(todo.getLatitude())){
            todo.setLatitude(todoReqDto.getLatitude());
        }
        if (!todoReqDto.getLongitude().equals(todo.getLongitude())){
            todo.setLongitude(todoReqDto.getLongitude());
        }

        return new TodoResDto(todo);
    }

    public Long getTotalTodo(String username, Boolean complete) {
        var member = memberRepository.findById(username).orElseThrow(
                ()-> new NoSuchElementException("cannot find member")
        );
        if (complete == null){
            return todoRepository.countByMember(member);
        } else {
            return todoRepository.countByMemberAndComplete(member, complete);
        }
    }
}
