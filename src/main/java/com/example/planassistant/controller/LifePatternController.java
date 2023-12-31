package com.example.planassistant.controller;

import com.example.planassistant.common.CommonController;
import com.example.planassistant.dto.lifepattern.LifePatternReqDto;
import com.example.planassistant.service.LifePatternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/life")
@Tag(name = "LifePattern", description = "생활패턴 관련 API")
public class LifePatternController extends CommonController {
    private final LifePatternService lifePatternService;
    @PostMapping
    public ResponseEntity createLifePattern(@AuthenticationPrincipal User user, @RequestBody LifePatternReqDto dto){
        lifePatternService.createLifePattern(user.getUsername(), dto);
        return CreatedReturn("created");
    }

    @GetMapping
    public ResponseEntity getLifePatterns(@AuthenticationPrincipal User user, @RequestParam(required = false) String life){
        System.out.println(life);
        return OkReturn(lifePatternService.getLifePatterns(user.getUsername(), life));
    }

    @Operation(summary = "생활패턴 여러개 생성")
    @PostMapping("/list")
    public ResponseEntity createLifePatterns(@AuthenticationPrincipal User user, @RequestBody List<LifePatternReqDto> dto){
        return CreatedReturn(lifePatternService.createLifePatterns(user.getUsername(), dto));
    }
    @Operation(description = "life pattern id에 따라 수정", summary = "수정")
    @PutMapping("/{id}")
    public ResponseEntity updateLifePattern(@AuthenticationPrincipal User user, @PathVariable Long id,@RequestBody LifePatternReqDto dto){
        return AcceptedReturn(lifePatternService.updateLifePattern(user.getUsername(), dto, id));
    }
}
