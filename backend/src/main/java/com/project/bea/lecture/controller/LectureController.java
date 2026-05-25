package com.project.bea.lecture.controller;

import com.project.bea.lecture.domain.ClassStatus;
import com.project.bea.lecture.dto.ClassResponse;
import com.project.bea.lecture.dto.CreateClassRequest;
import com.project.bea.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/classes")
public class LectureController {
    private  final  LectureService lectureService;

    @PostMapping("/createClass")
    public ClassResponse createClass(@RequestBody CreateClassRequest request) {
        return lectureService.createClass(request);
    }

    @GetMapping("/getClasses")
    public List<ClassResponse> getClasses(@RequestParam(required = false) ClassStatus status) {
        return lectureService.getClasses(status);
    }

    @GetMapping("/getClass/{id}")
    public ClassResponse getClass(@PathVariable Long id) {
        return lectureService.getClass(id);
    }

    @PatchMapping("/{classId}/open")
    public ClassResponse openClass(@PathVariable Long classId, @RequestParam Long creatorId) {
        return lectureService.openClass(classId, creatorId);
    }

    @PatchMapping("/{classId}/close")
    public ClassResponse closeClass(@PathVariable Long classId, @RequestParam Long creatorId) {
        return lectureService.closeClass(classId, creatorId);
    }
}
