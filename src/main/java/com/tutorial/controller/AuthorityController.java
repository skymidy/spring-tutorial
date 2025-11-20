package com.tutorial.controller;

import com.tutorial.model.dto.AuthorityDto;
import com.tutorial.model.dto.UsernameDto;
import com.tutorial.service.AuthorityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/authority")
public class AuthorityController {

    private final AuthorityService authorityService;

    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<String> getAllAvailableAuthorities() {
        return authorityService.getAllAvailableAuthorities();
    }

    @GetMapping("/me")
    public Set<String> getAllAuthoritiesOfCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return authorityService.getAllUserAuthorities(userDetails.getUsername());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<String> getAllAuthoritiesOfUser(@PathVariable("username") String username) {
        return authorityService.getAllUserAuthorities(username);
    }

    @PostMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<String> assignAuthoritiesToUser(@PathVariable("username") String username,
                                             @RequestBody AuthorityDto authorityDto) {
        return authorityService.assignAuthorityToUser(username, authorityDto);
    }

    @DeleteMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<String> removeAuthoritiesFromUser(@PathVariable("username") String username,
                                               @RequestBody AuthorityDto authorityDto) {
        return authorityService.removeAuthorityFromUser(username, authorityDto);
    }

}
