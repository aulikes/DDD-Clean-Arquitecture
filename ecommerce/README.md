
# Ecommerce - Arquitectura de Dominio y Aplicación (DDD)

Este proyecto modela un sistema e-commerce completo bajo los principios de Domain-Driven Design (DDD). Se ha implementado de forma estructurada la capa de dominio y la capa de aplicación, cada una con sus respectivas responsabilidades bien separadas.

---

## 1. Capa de Dominio
Contiene toda la lógica del negocio expresada a través de aggregates, entidades, objetos de valor, eventos de dominio y servicios de dominio.

### Aggregates Root modelados:
- `Orden`: ciclo de vida de la compra (creación, pago, envío, entrega, cancelación)
- `Pago`: estado del pago (pendiente, confirmado, fallido)
- `Envio`: proceso logístico (preparando, despachado, entregado)
- `Inventario`: stock disponible de productos
- `Notificacion`: mensajes generados hacia el cliente
- `Cliente`: datos del usuario y sus direcciones
- `Producto` y `Categoria`: referencia a catálogo de productos

### Objetos de valor:
- `EstadoOrden`: controla transiciones válidas de estado en una orden
- `Direccion`: entidad embebida dentro de cliente, controlada solo desde el agregado

### Servicios de dominio:
- `ProcesadorDePago`
- `ProcesadorDeEnvio`
- `ProcesadorDeEntrega`
- `ProcesadorDeCancelacion`

### Eventos de dominio:
- `OrdenPagada`
- `OrdenEnviada`
- `OrdenEntregada`
- `OrdenCancelada`

---

## 2. Capa de Aplicación
Contiene servicios que orquestan los casos de uso del sistema. No contiene lógica de negocio propia, solo delega a la capa de dominio y coordina la ejecución de los procesos.

### Servicios de aplicación:

#### `ServicioAplicacionPago`
- Ejecuta confirmación de pagos
- Llama a `ProcesadorDePago`
- Devuelve `OrdenPagada`

#### `ServicioAplicacionOrden`
- Crear orden
- Agregar/remover/cambiar ítems
- Calcular total
- Cancelar orden (devuelve `OrdenCancelada`)

#### `ServicioAplicacionEnvio`
- Despachar orden (tracking) y devolver `OrdenEnviada`
- Entregar orden y devolver `OrdenEntregada`

#### `ServicioAplicacionInventario`
- Reservar stock
- Liberar stock
- Consultar disponibilidad

#### `ServicioAplicacionNotificacion`
- Crear notificación pendiente
- Marcar como enviada

#### `ServicioAplicacionCliente`
- Crear cliente
- Actualizar nombre y email
- Agregar/actualizar/eliminar direcciones

---

## 3. Principios aplicados
- Separación de responsabilidades clara entre dominio y aplicación
- Eventos de dominio generados **solo como resultado de una acción del negocio**
- No hay lógica de infraestructura en ninguna de las capas modeladas hasta ahora
- Los servicios de dominio **no publican eventos**, solo modifican el estado del modelo
- La aplicación genera los eventos cuando corresponde

---

> Este sistema está diseñado para crecer hacia infraestructura, APIs REST, mensajería o persistencia sin afectar la lógica del negocio. Todo se basa en un modelo rico, autocontenido y coherente con las reglas del negocio.
