package com.aug.ecommerce.domain.model.orden;

import java.util.*;

/**
 * Objeto de Valor que representa el estado de una orden y sus transiciones válidas.
 * Usa un enum interno para definir todos los estados posibles, y encapsula
 * las reglas de cambio de estado dentro del mismo objeto de valor.
 */
public final class EstadoOrden {

    /**
     * Enum que contiene todos los posibles estados de una orden.
     */
    public enum Tipo {
        PENDIENTE_VALIDACION, // La orden fue creada y se encuentra en proceso de validación (cliente, productos, stock).
        VALIDACION_FALLIDA, // Una o más validaciones fallaron. La orden no puede continuar.
        LISTA_PARA_PAGO, // Todas las validaciones pasaron. La orden está lista para que el usuario inicie el pago.
        PAGO_EN_PROCESO, // El usuario inició el proceso de pago. Se descuenta stock y se invoca la pasarela de pago.
        PAGO_RECHAZADO, // La pasarela rechazó el pago. Se permite reintentar o cancelar.
        PAGADA, // El pago fue exitoso. La orden puede proceder a ser enviada.
        INICIANDO_ENVIO, // Inicia el proceso logístico de envío. El pedido aún no ha salido.
        ENVIADA, // El pedido ha sido enviado al cliente.
        ENTREGADA, // El pedido fue recibido por el cliente.
        CANCELADA // La orden fue cancelada, ya sea manual o automáticamente.
    }

    // Inicialización estática de estados y transiciones
    static {
        registrar(Tipo.PENDIENTE_VALIDACION, List.of(Tipo.PENDIENTE_VALIDACION, Tipo.LISTA_PARA_PAGO, Tipo.VALIDACION_FALLIDA));
        registrar(Tipo.VALIDACION_FALLIDA, List.of(Tipo.CANCELADA));
        registrar(Tipo.LISTA_PARA_PAGO, List.of(Tipo.PAGO_EN_PROCESO, Tipo.VALIDACION_FALLIDA, Tipo.PENDIENTE_VALIDACION, Tipo.CANCELADA));
        registrar(Tipo.PAGO_EN_PROCESO, List.of(Tipo.PAGADA, Tipo.PAGO_RECHAZADO, Tipo.CANCELADA));
        registrar(Tipo.PAGO_RECHAZADO, List.of(Tipo.PAGO_EN_PROCESO, Tipo.CANCELADA));
        registrar(Tipo.PAGADA, List.of(Tipo.INICIANDO_ENVIO, Tipo.CANCELADA));
        registrar(Tipo.INICIANDO_ENVIO, List.of(Tipo.ENVIADA));
        registrar(Tipo.ENVIADA, List.of(Tipo.ENTREGADA));
        registrar(Tipo.ENTREGADA, List.of());
        registrar(Tipo.CANCELADA, List.of());
    }

    // Mapa de instancias únicas de EstadoOrden por tipo
    private static final Map<Tipo, EstadoOrden> INSTANCIAS = new EnumMap<>(Tipo.class);

    private final Tipo tipo;
    private final List<Tipo> transicionesValidas;

    private EstadoOrden(Tipo tipo, List<Tipo> transicionesValidas) {
        this.tipo = tipo;
        this.transicionesValidas = transicionesValidas;
    }

    /**
     * Registra una instancia única de EstadoOrden junto con sus transiciones válidas.
     */
    private static void registrar(Tipo tipo, List<Tipo> transiciones) {
        INSTANCIAS.put(tipo, new EstadoOrden(tipo, transiciones));
    }

    /**
     * Devuelve una instancia de EstadoOrden a partir de su nombre en texto.
     */
    public static EstadoOrden desde(String nombre) {
        try {
            Tipo tipo = Tipo.valueOf(nombre);
            return INSTANCIAS.get(tipo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + nombre);
        }
    }

    /**
     * Devuelve la instancia de EstadoOrden asociada al tipo dado.
     */
    public static EstadoOrden deTipo(Tipo tipo) {
        return INSTANCIAS.get(tipo);
    }

    /**
     * Retorna el nombre textual del estado.
     */
    public String getValor() {
        return tipo.name();
    }

    /**
     * Valida si se puede hacer una transición al nuevo estado.
     * Lanza excepción si no es válida.
     */
    public void validarTransicionA(EstadoOrden nuevoEstado) {
        if (!transicionesValidas.contains(nuevoEstado.tipo)) {
            throw new IllegalStateException("Transición inválida de " + tipo + " a " + nuevoEstado.tipo);
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EstadoOrden otro) && this.tipo == otro.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo);
    }

    @Override
    public String toString() {
        return tipo.name();
    }
}
