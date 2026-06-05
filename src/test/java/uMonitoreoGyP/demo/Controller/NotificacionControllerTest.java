package uMonitoreoGyP.demo.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import uMonitoreoGyP.demo.Model.Notificacion;
import uMonitoreoGyP.demo.Service.CorreoService;
import uMonitoreoGyP.demo.Service.NotificacionService;

@WebMvcTest(NotificacionController.class)
@DisplayName("Tests de integración - NotificacionController")
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @MockBean
    private CorreoService correoService;

    private Notificacion notificacionEjemplo;
    private UUID idFijo;

    @BeforeEach
    void setUp() {
        idFijo = UUID.randomUUID();
        notificacionEjemplo = new Notificacion();
        notificacionEjemplo.setId(idFijo);
        notificacionEjemplo.setMedioEnvio("EMAIL");
        notificacionEjemplo.setAlertaId(UUID.randomUUID());
        notificacionEjemplo.setUsuarioId(UUID.randomUUID());
        notificacionEjemplo.setLeida(false);
    }

    // ──────────────────────────────────────────────────────────────
    // POST /crea
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /crea debe retornar 201 con la notificación creada")
    void testCrear_retorna201() throws Exception {
        when(notificacionService.crear(any(Notificacion.class))).thenReturn(notificacionEjemplo);

        mockMvc.perform(post("/api/notificaciones/crea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"medioEnvio\": \"EMAIL\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medioEnvio").value("EMAIL"));
    }

    // ──────────────────────────────────────────────────────────────
    // GET /todas
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /todas debe retornar 200 con la lista de notificaciones")
    void testObtenerTodas_retorna200() throws Exception {
        when(notificacionService.obtenerTodas()).thenReturn(List.of(notificacionEjemplo));

        mockMvc.perform(get("/api/notificaciones/todas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medioEnvio").value("EMAIL"));
    }

    // ──────────────────────────────────────────────────────────────
    // GET /una/{id}
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /una/{id} debe retornar 200 si la notificación existe")
    void testObtenerUna_existente_retorna200() throws Exception {
        when(notificacionService.obtenerPorId(idFijo)).thenReturn(Optional.of(notificacionEjemplo));

        mockMvc.perform(get("/api/notificaciones/una/" + idFijo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idFijo.toString()));
    }

    @Test
    @DisplayName("GET /una/{id} debe retornar 404 si no existe")
    void testObtenerUna_noExiste_retorna404() throws Exception {
        when(notificacionService.obtenerPorId(idFijo)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notificaciones/una/" + idFijo))
                .andExpect(status().isNotFound());
    }

    // ──────────────────────────────────────────────────────────────
    // PUT /actualiza/{id}
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /actualiza/{id} debe retornar 200 si existe")
    void testActualizar_existente_retorna200() throws Exception {
        when(notificacionService.actualizar(any(UUID.class), any(Notificacion.class)))
                .thenReturn(notificacionEjemplo);

        mockMvc.perform(put("/api/notificaciones/actualiza/" + idFijo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"leida\": true, \"medioEnvio\": \"EMAIL\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /actualiza/{id} debe retornar 404 si no existe")
    void testActualizar_noExiste_retorna404() throws Exception {
        when(notificacionService.actualizar(any(UUID.class), any(Notificacion.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/notificaciones/actualiza/" + idFijo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"leida\": true, \"medioEnvio\": \"EMAIL\"}"))
                .andExpect(status().isNotFound());
    }

    // ──────────────────────────────────────────────────────────────
    // DELETE /eliminar/{id}
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /eliminar/{id} debe retornar 204 si se eliminó")
    void testEliminar_existente_retorna204() throws Exception {
        when(notificacionService.eliminar(idFijo)).thenReturn(true);

        mockMvc.perform(delete("/api/notificaciones/eliminar/" + idFijo))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /eliminar/{id} debe retornar 404 si no existe")
    void testEliminar_noExiste_retorna404() throws Exception {
        when(notificacionService.eliminar(idFijo)).thenReturn(false);

        mockMvc.perform(delete("/api/notificaciones/eliminar/" + idFijo))
                .andExpect(status().isNotFound());
    }

    // ──────────────────────────────────────────────────────────────
    // POST /enviar
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /enviar debe retornar 200 y persistir notificación con datos válidos")
    void testEnviar_emailValido_retorna200() throws Exception {
        when(notificacionService.crear(any(Notificacion.class))).thenReturn(notificacionEjemplo);
        doNothing().when(correoService).enviarCorreo(anyString(), anyString(), anyString());

        String body = """
                {
                  "alertaId": "%s",
                  "medioEnvio": "EMAIL",
                  "correoDestino": "usuario@ejemplo.com",
                  "usuarioId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(correoService, times(1)).enviarCorreo(anyString(), anyString(), anyString());
        verify(notificacionService, times(1)).crear(any(Notificacion.class));
    }

    @Test
    @DisplayName("POST /enviar debe retornar 400 si correoDestino está ausente")
    void testEnviar_sinCorreoDestino_retorna400() throws Exception {
        String body = """
                {
                  "medioEnvio": "EMAIL"
                }
                """;

        mockMvc.perform(post("/api/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(correoService, never()).enviarCorreo(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("POST /enviar debe retornar 400 si el medioEnvio no es soportado")
    void testEnviar_medioNoSoportado_retorna400() throws Exception {
        String body = """
                {
                  "medioEnvio": "SMS",
                  "correoDestino": "usuario@ejemplo.com"
                }
                """;

        mockMvc.perform(post("/api/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(correoService, never()).enviarCorreo(anyString(), anyString(), anyString());
    }
}
