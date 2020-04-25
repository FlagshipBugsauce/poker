package com.poker.poker.controllers;

import com.poker.poker.models.AuthRequestModel;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "test", method = RequestMethod.POST)
    public ResponseEntity<?> test001(@RequestBody AuthRequestModel authRequestModel) {
        return ResponseEntity.ok("Success!");
    }


}
