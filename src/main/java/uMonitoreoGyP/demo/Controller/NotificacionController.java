package uMonitoreoGyP.demo.Controller;

import java.util.UUID;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uMonitoreoGyP.demo.Model.Notificacion;
import uMonitoreoGyP.demo.Service.CorreoService;
import uMonitoreoGyP.demo.Service.NotificacionService;

@RestController
@RequestMapping("api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private CorreoService correoService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping("/crea")
    public ResponseEntity<Notificacion> crear(@RequestBody Notificacion notificacion) {
        return ResponseEntity.status(201).body(notificacionService.crear(notificacion));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<Notificacion>> obtenerTodas() {
        return ResponseEntity.ok(notificacionService.obtenerTodas());
    }

    @GetMapping("/una/{id}")
    public ResponseEntity<Notificacion> obtenerUna(@PathVariable UUID id) {
        return notificacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualiza/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable UUID id, @RequestBody Notificacion detalles) {
        Notificacion actualizada = notificacionService.actualizar(id, detalles);
        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        boolean eliminado = notificacionService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/enviar")
public ResponseEntity<?> enviarAlerta(@RequestBody Map<String, String> request) {

    String alertaId      = request.get("alertaId");
    String medioEnvio    = request.get("medioEnvio");
    String correoDestino = request.get("correoDestino");
    String usuarioIdStr  = request.get("usuarioId");
    
    // 🚀 CAPTURAMOS LOS NUEVOS CAMPOS DINÁMICOS DEL FRONT
    String asunto        = request.get("asunto");
    String mensaje       = request.get("mensaje");

    if (correoDestino == null || correoDestino.isBlank()) {
        return ResponseEntity.badRequest()
                .body("{\"success\": false, \"mensaje\": \"El campo correoDestino es requerido\"}");
    }

    if (medioEnvio != null && "EMAIL".equalsIgnoreCase(medioEnvio.trim())) {
        
        // 🔍 Si el front no mandó asunto o mensaje, le ponemos uno por defecto para que no falle
        if (asunto == null || asunto.isBlank()) {
            asunto = "🚨 ALERTA DE MONITOREO: Actualización de Incendio";
        }
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = "Se ha registrado una nueva alerta en el sistema de monitoreo. ID: " + alertaId;
        }

        // Se envía el texto dinámico que viene desde el Front/Postman
        correoService.enviarCorreo(correoDestino, asunto, mensaje);

        // Guardar el registro histórico en Postgres
        Notificacion notificacion = new Notificacion();
        notificacion.setMedioEnvio(medioEnvio.trim().toUpperCase());
        if (alertaId != null && !alertaId.isBlank()) {
            notificacion.setAlertaId(UUID.fromString(alertaId));
        }
        if (usuarioIdStr != null && !usuarioIdStr.isBlank()) {
            notificacion.setUsuarioId(UUID.fromString(usuarioIdStr));
        }
        notificacionService.crear(notificacion);

        return ResponseEntity.ok()
                .body("{\"success\": true, \"mensaje\": \"Correo dinámico procesado de forma exitosa\"}");
    }

    return ResponseEntity.badRequest()
            .body("{\"success\": false, \"mensaje\": \"Medio de envío no soportado\"}");
}
}