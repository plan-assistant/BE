package com.example.planassistant.controller;

import com.example.planassistant.common.CommonController;
import com.example.planassistant.dto.PlanReqDto;
import com.example.planassistant.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Plan", description = "Plan 관련 API")
public class PlanController extends CommonController {

    private final PlanService planService;

    @Operation(summary = "plan 생성", description = "plan을 생성 해줍니다.")
    @ApiResponse(responseCode = "201", description = "plan 생성 성공", content = @Content(schema = @Schema(implementation = PlanReqDto.class)))
    @PostMapping
    public ResponseEntity makePlan(@AuthenticationPrincipal User user, @RequestBody PlanReqDto planReqDto){
        log.info(planReqDto.toString());
        planService.makePlan(user.getUsername(), planReqDto);
        return CreatedReturn("created");
    }
    @Operation(summary = "plan 상세 조회", description = "plan 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity getPlan(@AuthenticationPrincipal User user, @PathVariable Long id){
        return OkReturn(planService.getPlanById(id));
    }
    @Operation(summary = "plan 전체 조회", description = "plan 전체 조회")
    @GetMapping()
    public ResponseEntity getAllPlan(@AuthenticationPrincipal User user){
        return OkReturn(planService.getPlanByMember(user.getUsername()));
    }
    @GetMapping("/date/{addDate}")
    public ResponseEntity getPlanByAddDate(@AuthenticationPrincipal User user, @PathVariable Integer addDate){
        log.info(addDate.toString());
        return OkReturn(planService.getPlansByAddDate(user.getUsername(), addDate));
    }

    @Operation(summary = "plan 수정", description = "plan을 수정 해줍니다.")
    @PutMapping("/{id}")
    public ResponseEntity changePlan(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody PlanReqDto planReqDto){
        planService.changePlan(id, planReqDto);
        return AcceptedReturn("changed");
    }

    @Operation(summary = "plan 삭제", description = "plan을 삭제 해줍니다.")
    @DeleteMapping("{id}")
    public ResponseEntity deletePlan(@AuthenticationPrincipal User user, @PathVariable Long id){
        return AcceptedReturn(planService.deletePlan(id));
    }

    @Operation(summary = "plan을 date로 받아서 보여줍니다. ", description = "startDate에 맞게 plan을 보여줍니다" +
            "yyyy-MM-dd 형식")
    @GetMapping("/calendar/{date}")
    public ResponseEntity getPlansByStartDate(@AuthenticationPrincipal User user, @PathVariable String date){
        log.info("getPlansByStartDate Call" + date);

        LocalDate localDate = LocalDate.parse(date);
        return OkReturn(planService.getPlansByStartDate(user.getUsername(),localDate));
    }

    @Operation(summary = "plan을 year, month로 받아서 list로 보여줍니다. ", description = "year, month는 Integer")
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity getPlansByMonthAndYear(@AuthenticationPrincipal User user, @PathVariable Integer year,
        @PathVariable Integer month){

        log.info("getPlansByMonthAndYear Call");
        return OkReturn(planService.getPlansByYearAndMonth(user.getUsername(), year, month));
    }
}
