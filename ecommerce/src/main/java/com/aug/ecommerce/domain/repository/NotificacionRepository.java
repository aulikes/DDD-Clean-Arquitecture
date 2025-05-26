package com.aug.ecommerce.domain.repository;

import com.aug.ecommerce.domain.model.notificacion.Notificacion;
import java.util.List;
import java.util.Optional;

public interface NotificacionRepository {
    void save(Notificacion notificacion);
    Optional<Notificacion> findById(String id);
    List<Notificacion> findAll();
    void deleteById(String id);

    List<Notificacion> findByClienteId(String clienteId);
}
