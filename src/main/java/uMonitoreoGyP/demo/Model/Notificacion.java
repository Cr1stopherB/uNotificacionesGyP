package uMonitoreoGyP.demo.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // ID de la Alerta general
    @Column(name = "alerta_id", nullable = false)
    private UUID alertaId;

    // ID del usuario que recibe la notificación
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    // Solo para verificar si la persona leyo la notificacion
    @Column(name = "leida")
    private boolean leida = false;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    // SMS o EMAIL
    @Column(name = "medio_envio")
    private String medioEnvio;
}
