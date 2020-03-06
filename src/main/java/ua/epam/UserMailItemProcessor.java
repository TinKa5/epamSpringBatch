package ua.epam;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.mail.SimpleMailMessage;
import ua.epam.model.User;

public class UserMailItemProcessor implements ItemProcessor<User,SimpleMailMessage> {
    @Override
    public SimpleMailMessage process(User user) throws Exception {
        SimpleMailMessage mes=new SimpleMailMessage();
        mes.setFrom("tanua8282@gmail.com");
        mes.setTo(user.getMail());
        mes.setSubject("Warning account balance!");
        mes.setText("Dear "+user.getName()+", your account balance is low(<10$) = "
                +user.getBalance().toString()+"$.");
        return mes;
    }
}
