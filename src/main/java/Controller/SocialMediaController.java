package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ResourceNotFoundException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    MessageService messageService;
    AccountService accountService;

    public SocialMediaController() {
        messageService = new MessageService();
        accountService = new AccountService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     *
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerUserHandler);
        app.post("/login", this::loginUserHandler);
        app.get("/messages", this::getMessagesHandler);
        app.post("/messages", this::postMessagesHandler);
        app.get("/messages/{message_id}", this::getMessagesHandlerById);
        app.delete("/messages/{message_id}", this::deleteMessagesHandlerById);
        app.patch("/messages/{message_id}", this::updateMessagesHandlerById);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesHandlerById);
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void registerUserHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        if (account.getUsername().isEmpty() || account.getPassword().length() < 4) {
            ctx.status(400);
            return;
        }

        try {
            String currentUsername = accountService.getUsername(account.getUsername());
            if (Objects.equals(currentUsername, account.getUsername())) {
                ctx.status(400);
            }
        } catch (ResourceNotFoundException exception) {
            Account savedAccount = accountService.addUser(account);
            ctx.json(savedAccount);
            ctx.status(200);
        }
    }

    private void loginUserHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        try {
            Account savedAccount = accountService.login(account);
            ctx.json(savedAccount);
            ctx.status(200);
        } catch (ResourceNotFoundException exception) {
            ctx.status(401);
        }
    }

    private void getAllMessagesHandlerById(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
//        if(account_id == 0){
//            ctx.status(200);
//            return;
//        }
        List<Message> messages = messageService.getAllMessagesById(account_id);
        ctx.json(mapper.writeValueAsString(messages));
    }

    private void getMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();

        ctx.json(messages);
    }

    private void postMessagesHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        if (message.getMessage_text().length() > 255 || message.getMessage_text().isEmpty() || message.getTime_posted_epoch() == 0) {
            ctx.status(400);
            return;
        }

        try {
            accountService.getAccount(message.getPosted_by());
            Message newMessage = messageService.addMessage(message);
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(newMessage));
        } catch (ResourceNotFoundException exception) {
            ctx.status(400);
        }
    }

    private void getMessagesHandlerById(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(message_id);
        if(Objects.isNull(message)){
            ctx.status(200);
        }else {
            ctx.json(mapper.writeValueAsString(message));
        }
    }

    private void updateMessagesHandlerById(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message messageRequest = mapper.readValue(ctx.body(), Message.class);
        if (messageRequest.getMessage_text().length() > 255 || messageRequest.getMessage_text().isEmpty()) {
            ctx.status(400);
            return;
        }

        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message updatedMessage = messageService.updateMessage(message_id, messageRequest);
        if (updatedMessage == null) {
            ctx.status(400);
        } else {
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(updatedMessage));
        }
    }

    private void deleteMessagesHandlerById(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        Message updatedMessage = messageService.deleteMessage(message_id);

        if (updatedMessage == null) {
            ctx.status(200);
        } else {
            ctx.status(200);
            ctx.json(mapper.writeValueAsString(updatedMessage));
        }
    }
}


