package uMonitoreoGyP.demo.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - CorreoService")
class CorreoServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private CorreoService correoService;

    @BeforeEach
    void setUp() {
        // Simular el valor inyectado por @Value
        ReflectionTestUtils.setField(correoService, "remitente", "noreply@sistema.com");
    }

    @Test
    @DisplayName("Debe enviar un correo con los datos correctos")
    void testEnviarCorreo_enviaMensajeCorrectamente() {
        // Arrange
        String destinatario = "usuario@ejemplo.com";
        String asunto = "Asunto de prueba";
        String mensaje = "Cuerpo del mensaje de prueba";

        // Act
        correoService.enviarCorreo(destinatario, asunto, mensaje);

        // Assert: verificar que mailSender.send() fue llamado exactamente una vez
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage emailCapturado = captor.getValue();
        assert emailCapturado.getTo() != null;
        assert emailCapturado.getTo()[0].equals(destinatario);
        assert asunto.equals(emailCapturado.getSubject());
        assert mensaje.equals(emailCapturado.getText());
        assert "noreply@sistema.com".equals(emailCapturado.getFrom());
    }

    @Test
    @DisplayName("Debe llamar a mailSender.send() exactamente una vez por invocación")
    void testEnviarCorreo_llamaSendUnaVez() {
        correoService.enviarCorreo("a@b.com", "test", "test");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("No debe enviar si ocurre una excepción en mailSender")
    void testEnviarCorreo_propagaExcepcionDeMailSender() {
        doThrow(new RuntimeException("SMTP no disponible"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                correoService.enviarCorreo("a@b.com", "Error", "Mensaje"));
    }
}
