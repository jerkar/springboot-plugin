package your.basepackage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class Controller {

    @GetMapping("/")
    String helloWorld() {
        return "Hello Word";
    }

}


