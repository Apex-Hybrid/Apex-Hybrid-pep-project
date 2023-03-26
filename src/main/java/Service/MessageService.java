package Service;

import DAO.MessageDAO;
import Model.Message;
import exception.ResourceNotFoundException;

import java.sql.SQLException;
import java.util.List;

public class MessageService {

    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public Message addMessage(Message message) {
        return messageDAO.newMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public List<Message> getAllMessagesById(int account_id) {
        return messageDAO.getAllMessagesByUser(account_id);
    }

    public Message getMessageById(int message_id) {
        try {
            return messageDAO.getMessageById(message_id);
        } catch (ResourceNotFoundException exception) {
            return null;
        }
    }

    public Message updateMessage(int message_id, Message message) {
        try {
            var currentMessage = messageDAO.getMessageById(message_id);
            currentMessage.setMessage_text(message.getMessage_text());
            messageDAO.updateMessage(message_id, currentMessage);
            return currentMessage;
        } catch (ResourceNotFoundException exception) {
            return null;
        }
    }

    public Message deleteMessage(int message_id) {
        try {
            var currentMessage = messageDAO.getMessageById(message_id);
            messageDAO.deleteMessage(message_id);
            return currentMessage;
        } catch (ResourceNotFoundException exception) {
            return null;
        }
    }
}




