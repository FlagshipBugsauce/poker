package com.poker.poker.controllers;

import com.poker.poker.models.ApiError;
import com.poker.poker.models.AuthRequestModel;
import com.poker.poker.models.AuthResponseModel;
import com.poker.poker.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "users", description = "Users API handles all user account related requests, such as authentication, " +
        "registration, etc...")
public class UserController {
    private UserService userService;

    @Operation(
            summary = "Authenticate",
            description = "The client must call this endpoint in order to obtain a JWT, which must be passed in the " +
                    "header of most requests.",
            tags = "users"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authorization was successful. A JWT should be returned, which can be used " +
                                    "to access secured endpoints.",
                            content = @Content(schema = @Schema(implementation = AuthResponseModel.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials were provided.",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Invalid credentials were provided.",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<AuthResponseModel> authorize(@RequestBody AuthRequestModel authRequestModel) {
        return ResponseEntity.ok(userService.authenticate(authRequestModel));
    }
}
