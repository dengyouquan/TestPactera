package com.dyq.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {
    @GetMapping("/course")
    public String course(){
        return "course";
    }
    @GetMapping("/course/main")
    public String coursedetail(){
        return "coursedetail";
    }
    @GetMapping("/course/study")
    public String courseLearn(){
        return "courseLearn";
    }
    @GetMapping("/course/list")
    public String courses(){
        return "courses";
    }
}
