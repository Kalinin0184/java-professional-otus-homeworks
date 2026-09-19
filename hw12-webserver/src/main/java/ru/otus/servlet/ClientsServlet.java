package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;
import ru.otus.web.ClientView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";

    private final TemplateProcessor templateProcessor;
    private final DBServiceClient dbServiceClient;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ClientView> clients = dbServiceClient.findAll().stream()
                .map(ClientView::from)
                .collect(Collectors.toList());

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, Map.of("clients", clients)));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String street = request.getParameter("street");
        String numbers = request.getParameter("numbers");

        List<Phone> phones = parsePhones(numbers);
        Address address = (street == null || street.isBlank()) ? null : new Address(null, street.trim());
        dbServiceClient.saveClient(new Client(null, name, address, phones));

        response.sendRedirect("/clients");
    }

    private static List<Phone> parsePhones(String numbers) {
        if (numbers == null || numbers.isBlank()) {
            return List.of();
        }
        return Arrays.stream(numbers.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(number -> new Phone(null, number))
                .collect(Collectors.toList());
    }
}
