package com.poker.poker.controllers;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<AuthResponseModel> authorize(@RequestBody AuthRequestModel authRequestModel) {
        return ResponseEntity.ok(userService.authenticate(authRequestModel));
    }
}
