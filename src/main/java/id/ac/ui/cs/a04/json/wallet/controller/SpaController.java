package id.ac.ui.cs.a04.json.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SpaController {
    @GetMapping("/app/{path:[^.]*}")
    public String redirect(@PathVariable String path) {
        return "forward:/app/index.html";
    }
}
