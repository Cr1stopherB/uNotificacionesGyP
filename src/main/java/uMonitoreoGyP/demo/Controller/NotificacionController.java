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
        
        String alertaId = request.get("alertaId");
        String medioEnvio = request.get("medioEnvio");
        String correoDestino = "jperezcaniulef.21@gmail.com"; 

        if ("EMAIL".equalsIgnoreCase(medioEnvio)) {
            String asunto = "🚨 ALERTA DE INCENDIO: Nuevo foco registrado";
            String mensaje = "Se ha reportado un nuevo foco o actualización en el monitoreo.\n\n"
                           + "ID de la alerta: " + alertaId + "\n"
                           + "Por favor, revise el panel de monitoreo inmediatamente.";

            correoService.enviarCorreo(correoDestino, asunto, mensaje);
            
            return ResponseEntity.ok().body("{\"success\": true, \"mensaje\": \"Correo enviado exitosamente\"}");
        }

        return ResponseEntity.badRequest().body("{\"success\": false, \"mensaje\": \"Medio de envío no soportado\"}");
    }
}