package ru.otus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.dto.ClientForm;
import ru.otus.service.DBServiceClient;

import java.util.List;

@Controller
public class ClientController {

    private final DBServiceClient dbServiceClient;

    public ClientController(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @GetMapping({"/", "/clients"})
    public String clients(Model model) {
        List<ClientForm> clients = dbServiceClient.findAll().stream().map(ClientForm::new).toList();
        model.addAttribute("clients", clients);
        model.addAttribute("clientForm", new ClientForm());
        return "clients";
    }

    @PostMapping("/clients")
    public RedirectView createClient(@ModelAttribute ClientForm clientForm) {
        dbServiceClient.saveClient(clientForm.toClient());
        return new RedirectView("/clients", true);
    }
}
