# Modelado DDD - E-commerce: Proceso completo de compra y recepción

## Contexto del Problema

Una tienda online desea modelar el proceso completo que sigue un cliente desde que selecciona productos hasta que los recibe en su domicilio. Este proceso debe contemplar la gestión de catálogos, carritos, cupones de descuento, validación de stock, pagos, logística de envío y atención postventa (como devoluciones o reclamos).

El sistema debe ser capaz de:
- Representar adecuadamente cada actor y objeto del proceso (cliente, producto, orden, pago, etc.)
- Manejar reglas de negocio como disponibilidad de stock, cálculo de precios finales, seguimiento de estados de orden y envío
- Mantener independencia y consistencia en los diferentes módulos del dominio (por ejemplo, pagos, inventario, envío, etc.)
- Soportar la trazabilidad de todo el ciclo de vida de una compra, desde el carrito hasta la entrega final

## Requisitos Funcionales

### Navegación y selección de productos

1. Un **cliente** puede navegar el catálogo y ver productos disponibles.
2. Puede **agregar productos al carrito de compras**.
3. Puede **modificar cantidades o eliminar ítems del carrito**.

### Promociones y descuentos

4. El cliente puede **aplicar un cupón** de descuento válido al carrito o a la orden.
5. El sistema debe validar que el cupón **no esté vencido**, **no haya sido usado previamente** por ese cliente y **cumpla condiciones mínimas de compra**.

### Proceso de compra

6. Al confirmar la compra:
   - Se **verifica la disponibilidad en inventario** de los productos.
   - Se **crea una orden de compra** con ítems, totales y descuentos aplicados.
   - Se **reserva temporalmente el stock** de los productos.

7. El cliente procede al **pago de la orden**.
   - El sistema debe registrar el **pago realizado**, su método (tarjeta, PSE, etc.) y estado (`PENDIENTE`, `CONFIRMADO`, `FALLIDO`).
   - Al confirmarse el pago, se **confirma la orden** y el stock reservado se descuenta definitivamente.

### Logística de envío

8. Se genera un **registro de envío**, incluyendo:
   - Dirección de entrega (puede ser objeto de valor)
   - Estado del envío (`PREPARANDO`, `ENVIADO`, `ENTREGADO`)
   - Información de rastreo si aplica

9. El cliente puede **consultar el estado de su envío** en cualquier momento.

### Postventa

10. El cliente puede **realizar un reclamo** o **solicitar una devolución** si el producto:
    - No llegó
    - Llegó dañado
    - No corresponde a lo comprado

11. El sistema debe **registrar reclamos y devoluciones**, y vincularlos a la orden original.

### Notificaciones

12. El sistema debe enviar **notificaciones** al cliente en puntos clave del proceso:
    - Orden confirmada
    - Pago recibido
    - Producto enviado
    - Producto entregado
