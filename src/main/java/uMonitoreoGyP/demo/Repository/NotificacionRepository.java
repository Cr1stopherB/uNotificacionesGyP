package uMonitoreoGyP.demo.Repository;

import uMonitoreoGyP.demo.Model.Notificacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID; 

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {
    
}
