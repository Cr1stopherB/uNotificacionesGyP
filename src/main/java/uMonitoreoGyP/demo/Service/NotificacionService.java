package uMonitoreoGyP.demo.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uMonitoreoGyP.demo.Model.Notificacion;
import uMonitoreoGyP.demo.Repository.NotificacionRepository;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository repository;

    @Autowired
    private CorreoService correoService;

    public Notificacion crear(Notificacion notificacion) {
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setLeida(false);
        return repository.save(notificacion);
    }
    
    public void procesarYEnviarAlerta(String alertaId, String medioEnvio, String correoDestino, String usuarioIdStr) {
    
        String asunto = "🚨 ALERTA DE INCENDIO: Nuevo foco registrado";
        String cuerpo = "Se ha reportado un nuevo foco o actualización en el monitoreo.\n\n"
                      + "ID de la alerta: " + alertaId + "\n"
                      + "Por favor, revise el panel de monitoreo inmediatamente.";

        correoService.enviarCorreo(correoDestino, asunto, cuerpo);

        Notificacion notificacion = new Notificacion();
        notificacion.setMedioEnvio(medioEnvio);
        
        if (alertaId != null && !alertaId.isBlank()) {
            notificacion.setAlertaId(UUID.fromString(alertaId));
        }
        if (usuarioIdStr != null && !usuarioIdStr.isBlank()) {
            notificacion.setUsuarioId(UUID.fromString(usuarioIdStr));
        }
        
        this.crear(notificacion);
    }

    public List<Notificacion> obtenerTodas() { return repository.findAll(); }
    public Optional<Notificacion> obtenerPorId(UUID id) { return repository.findById(id); }

    public Notificacion actualizar(UUID id, Notificacion detalles) {
        return repository.findById(id).map(notif -> {
            if (detalles.isLeida() && !notif.isLeida()) {
                notif.setFechaLectura(LocalDateTime.now());
            }
            notif.setLeida(detalles.isLeida());
            notif.setMedioEnvio(detalles.getMedioEnvio());
            return repository.save(notif);
        }).orElse(null);
    }

    public boolean eliminar(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}