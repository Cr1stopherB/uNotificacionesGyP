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

    public Notificacion crear(Notificacion notificacion) {
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setLeida(false);
        return repository.save(notificacion);
    }

    public List<Notificacion> obtenerTodas() {
        return repository.findAll();
    }

    public Optional<Notificacion> obtenerPorId(UUID id) {
        return repository.findById(id);
    }

    public Notificacion actualizar(UUID id, Notificacion detalles) {
        return repository.findById(id).map(notif -> {
            // Lógica inteligente: Si pasa de 'no leída' a 'leída', grabamos la hora exacta
            if (detalles.isLeida() && !notif.isLeida()) {
                notif.setFechaLectura(LocalDateTime.now());
            }
            notif.setLeida(detalles.isLeida());
            notif.setMedioEnvio(detalles.getMedioEnvio());
            
            return repository.save(notif);
        }).orElse(null); // Retorno de null para que el Controller lance el 404
    }

    public boolean eliminar(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
