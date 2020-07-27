package com.poker.poker.controllers;

import com.poker.poker.models.ModelModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is serving as a place to return models that aren't returned anywhere else, so that the
 * client model generation will generate client models automatically.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

  /*
     Endpoint so that schemas are generated which are used to generate client models.
  */
  @GetMapping("/models")
  public ResponseEntity<ModelModel> models() {
    return ResponseEntity.ok(new ModelModel());
  }
}
