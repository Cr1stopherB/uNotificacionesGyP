package uMonitoreoGyP.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("jperezcaniulef.21@gmail.com"); 
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);

        mailSender.send(email);
        System.out.println("Correo enviado exitosamente a: " + destinatario);
    }
}
