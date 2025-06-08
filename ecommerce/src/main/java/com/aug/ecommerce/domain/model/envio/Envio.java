package com.aug.ecommerce.domain.model.envio;

import java.time.Instant;
import java.util.*;

public class Envio {

    private final Long id;
    private final Long ordenId;
    private final String direccionEnvio;
    private EstadoEnvio estado;
    private int intentos;
    private String razonFallo;
    private String trackingNumber;
    private final List<EnvioEstadoHistorial> historial;

    public static Envio create(Long ordenId, String direccionEnvio) {
        List<EnvioEstadoHistorial> historial = new ArrayList<>();
        historial.add(EnvioEstadoHistorial.create(getEstadoInicial()));
        return new Envio(null, ordenId, direccionEnvio, getEstadoInicial(),
                null, null, 0, historial);
    }

    public static Envio fromPersistence(Long id, Long ordenId, String direccionEnvio,
                                        EstadoEnvio estado, String trackingNumber,
                                        String razonFallo, int intentos, List<EnvioEstadoHistorial> historial) {
        if (id == null) throw new IllegalArgumentException("El idEnvio no puede ser nulo");
        return new Envio(id, ordenId, direccionEnvio, estado, trackingNumber, razonFallo, intentos, historial);
    }

    //Cuando se Mapea desde la Infra
    public void restoreHistorial(
            Long idEstadoHistorial, EstadoEnvio estadoEnvio, String observacion, Instant fechaCambio) {
        if (this.id == null) throw new IllegalArgumentException("El ordenId no puede ser nulo");
        historial.add(EnvioEstadoHistorial.fromPersistence(idEstadoHistorial, estadoEnvio, observacion, fechaCambio));
    }

    private Envio(Long id, Long ordenId, String direccionEnvio, EstadoEnvio estado,
                  String trackingNumber, String razonFallo, int intentos, List<EnvioEstadoHistorial> historial) {
        this.id = id;
        this.ordenId = Objects.requireNonNull(ordenId, "La orden no puede ser nula");
        this.direccionEnvio = Objects.requireNonNull(direccionEnvio, "La dirección no puede ser nula");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.trackingNumber = trackingNumber;
        this.razonFallo = razonFallo;
        this.intentos = intentos;
        this.historial = historial;

        if (this.id == null) validarUnicoEstadoConFechaNula();
    }

    public Long getId() { return id; }
    public Long getOrdenId() { return ordenId; }
    public String getDireccionEnvio() { return direccionEnvio; }
    public EstadoEnvio getEstado() { return estado; }
    public String getTrackingNumber() { return trackingNumber; }
    public int getIntentos() { return intentos; }
    public String getRazonFallo() { return razonFallo; }
    public void incrementarReintentos() { this.intentos++; }
    public List<EnvioEstadoHistorial> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    public static EstadoEnvio getEstadoInicial(){
        return EstadoEnvio.PENDIENTE;
    }

    //Se cambia el estado cuando el proveeddor de envíos recibió la petición
    public void iniciarPreparacionEnvio(String trackingNumber) {
        if (estado != EstadoEnvio.PENDIENTE)
            throw new IllegalStateException("Solo puede preparar un envío que está pendiente");
        this.trackingNumber = Objects.requireNonNull(trackingNumber, "El número de seguimiento no puede ser nulo");
        this.razonFallo = null;
        agregarNuevoEstado(EstadoEnvio.PREPARANDO, null);
    }

    public void actualizarEstadoFromSupplier(EstadoEnvio nuevoEstado, String observacion) {
        if (estado == EstadoEnvio.PREPARANDO || estado == EstadoEnvio.DESPACHADO) {
            agregarNuevoEstado(nuevoEstado, observacion);
        } else {
            throw new IllegalStateException("Solo se puede entregar un envío en estado PREPARANDO o DESPACHADO");
        }
    }

    public void marcarComoFallido(String razon) {
        this.estado = EstadoEnvio.FALLIDO;
        this.razonFallo = razon;
        agregarNuevoEstado(EstadoEnvio.FALLIDO, razon);
    }

    public void marcarComoPendiente(String razon) {
        if (estado != EstadoEnvio.PENDIENTE)
            throw new IllegalStateException("Solo puede preparar un envío que está pendiente");
        this.razonFallo = razon;
        agregarNuevoEstado(EstadoEnvio.PENDIENTE, razon);
    }

    public void agregarNuevoEstado(EstadoEnvio nuevoEstado, String observacion) {
        this.estado = nuevoEstado;
        historial.add(EnvioEstadoHistorial.create(nuevoEstado, observacion));
        validarUnicoEstadoConFechaNula();
    }

    //Valida que el historial tenga un solo estado con fecha nula
    private void validarUnicoEstadoConFechaNula() {
        if (historial == null || historial.isEmpty()) {
            throw new IllegalStateException("El envío no tiene historial registrado.");
        }
        long sinFecha = historial.stream()
                .filter(h -> h.getFechaCambio() == null)
                .count();

        if (sinFecha > 1) {
            throw new IllegalStateException("Solo puede haber un estado en historial sin fecha de cambio.");
        }
    }

    //Obtiene el último estado con fecha registrada, si no hay devuelve el estado que no tiene fecha
    public EnvioEstadoHistorial getUltimoEstadoHistorial() {
        validarUnicoEstadoConFechaNula();

        return historial.stream()
                .filter(h -> h.getFechaCambio() != null)
                .max(Comparator.comparing(EnvioEstadoHistorial::getFechaCambio))
                .orElseGet(historial::getLast);
    }
}
