package uMonitoreoGyP.demo.Service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uMonitoreoGyP.demo.Model.Notificacion;
import uMonitoreoGyP.demo.Repository.NotificacionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - NotificacionService")
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacionBase;
    private UUID idFijo;

    @BeforeEach
    void setUp() {
        idFijo = UUID.randomUUID();
        notificacionBase = new Notificacion();
        notificacionBase.setId(idFijo);
        notificacionBase.setAlertaId(UUID.randomUUID());
        notificacionBase.setUsuarioId(UUID.randomUUID());
        notificacionBase.setMedioEnvio("EMAIL");
        notificacionBase.setLeida(false);
        notificacionBase.setFechaEnvio(LocalDateTime.now());
    }

    // ──────────────────────────────────────────────────────────────
    // crear()
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear() debe establecer fechaEnvio y leida=false antes de guardar")
    void testCrear_estableceValoresPorDefecto() {
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionBase);

        Notificacion nueva = new Notificacion();
        nueva.setMedioEnvio("EMAIL");

        Notificacion resultado = notificacionService.crear(nueva);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repository).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertThat(guardada.isLeida()).isFalse();
        assertThat(guardada.getFechaEnvio()).isNotNull();
        assertThat(resultado).isNotNull();
    }

    // ──────────────────────────────────────────────────────────────
    // obtenerTodas()
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerTodas() debe retornar todas las notificaciones del repositorio")
    void testObtenerTodas() {
        when(repository.findAll()).thenReturn(List.of(notificacionBase));

        List<Notificacion> resultado = notificacionService.obtenerTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMedioEnvio()).isEqualTo("EMAIL");
        verify(repository, times(1)).findAll();
    }

    // ──────────────────────────────────────────────────────────────
    // obtenerPorId()
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId() debe retornar Optional con la notificación si existe")
    void testObtenerPorId_existe() {
        when(repository.findById(idFijo)).thenReturn(Optional.of(notificacionBase));

        Optional<Notificacion> resultado = notificacionService.obtenerPorId(idFijo);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(idFijo);
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar Optional vacío si no existe")
    void testObtenerPorId_noExiste() {
        when(repository.findById(idFijo)).thenReturn(Optional.empty());

        Optional<Notificacion> resultado = notificacionService.obtenerPorId(idFijo);

        assertThat(resultado).isEmpty();
    }

    // ──────────────────────────────────────────────────────────────
    // actualizar()
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar() debe asignar fechaLectura al marcar como leída")
    void testActualizar_marcaFechaLecturaAlLeer() {
        notificacionBase.setLeida(false);
        when(repository.findById(idFijo)).thenReturn(Optional.of(notificacionBase));
        when(repository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Notificacion detalles = new Notificacion();
        detalles.setLeida(true);
        detalles.setMedioEnvio("EMAIL");

        Notificacion resultado = notificacionService.actualizar(idFijo, detalles);

        assertThat(resultado.isLeida()).isTrue();
        assertThat(resultado.getFechaLectura()).isNotNull();
    }

    @Test
    @DisplayName("actualizar() no debe cambiar fechaLectura si ya estaba leída")
    void testActualizar_noSobreescribeFechaLectura() {
        LocalDateTime fechaAntigua = LocalDateTime.now().minusDays(1);
        notificacionBase.setLeida(true);
        notificacionBase.setFechaLectura(fechaAntigua);
        when(repository.findById(idFijo)).thenReturn(Optional.of(notificacionBase));
        when(repository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Notificacion detalles = new Notificacion();
        detalles.setLeida(true);
        detalles.setMedioEnvio("SMS");

        Notificacion resultado = notificacionService.actualizar(idFijo, detalles);

        // La fecha de lectura no debe cambiar porque ya estaba leída
        assertThat(resultado.getFechaLectura()).isEqualTo(fechaAntigua);
    }

    @Test
    @DisplayName("actualizar() debe retornar null si el id no existe")
    void testActualizar_retornaNullSiNoExiste() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Notificacion resultado = notificacionService.actualizar(UUID.randomUUID(), new Notificacion());

        assertThat(resultado).isNull();
    }

    // ──────────────────────────────────────────────────────────────
    // eliminar()
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar() debe retornar true y llamar deleteById si existe")
    void testEliminar_existente() {
        when(repository.existsById(idFijo)).thenReturn(true);

        boolean resultado = notificacionService.eliminar(idFijo);

        assertThat(resultado).isTrue();
        verify(repository, times(1)).deleteById(idFijo);
    }

    @Test
    @DisplayName("eliminar() debe retornar false si el id no existe")
    void testEliminar_noExistente() {
        when(repository.existsById(idFijo)).thenReturn(false);

        boolean resultado = notificacionService.eliminar(idFijo);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
