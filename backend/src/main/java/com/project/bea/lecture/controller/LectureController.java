package com.project.bea.lecture.controller;

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
    public List<ClassResponse> getClasses() {
        return lectureService.getClasses();
    }

    @GetMapping("/getClass/{id}")
    public ClassResponse getClass(@PathVariable Long id) {
        return lectureService.getClass(id);
    }
}
