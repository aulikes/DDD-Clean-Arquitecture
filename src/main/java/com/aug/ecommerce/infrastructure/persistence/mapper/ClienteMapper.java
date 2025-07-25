package com.aug.ecommerce.infrastructure.persistence.mapper;

import com.aug.ecommerce.domain.models.cliente.Cliente;
import com.aug.ecommerce.infrastructure.persistence.entity.ClienteEntity;
import com.aug.ecommerce.infrastructure.persistence.entity.DireccionEntity;

import java.util.List;

public class ClienteMapper {

    private ClienteMapper() {}

    public static ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNombre(cliente.getNombre());
        entity.setEmail(cliente.getEmail());

        List<DireccionEntity> direcciones = cliente.getDirecciones().stream().map(dir -> {
            DireccionEntity e = new DireccionEntity();
            e.setId(dir.getId());
            e.setCalle(dir.getCalle());
            e.setCiudad(dir.getCiudad());
            e.setPais(dir.getPais());
            e.setCodigoPostal(dir.getCodigoPostal());
            return e;
        }).toList();

        entity.setDirecciones(direcciones);
        return entity;
    }

    public static Cliente toDomain(ClienteEntity entity) {
        Cliente cliente = new Cliente(entity.getId(), entity.getNombre(), entity.getEmail());

        for (DireccionEntity dir : entity.getDirecciones()) {
            cliente.agregarDireccion(
                    dir.getCalle(),
                    dir.getCiudad(),
                    dir.getPais(),
                    dir.getCodigoPostal()
            );
        }
        return cliente;
    }
}
