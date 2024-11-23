let lineCounter = 0;
const LineaPedidoFormApp = {
    agregarLineaPedido: (pedidoVentaId) => {
        lineCounter++;
        const container = document.getElementById('orderLinesContainer');
        const lineId = `line-${lineCounter}`;
        const line = document.createElement('div');

        line.id = lineId;
        line.classList.add('position-relative', 'mb-3', 'card', 'p-3');

        line.innerHTML = `
            <button type="button" class="btn btn-danger btn-sm position-absolute top-0 end-0 m-2">
                            <i class="fas fa-trash-alt"></i>
            </button>
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="idPedidoLinea" class="form-label">ID Pedido de Venta</label>
                    <input type="text" class="form-control" id="idPedidoLinea-${lineCounter}" value="${pedidoVentaId}" placeholder="ID Pedido de Venta" readonly>
                </div>

                <div class="col-md-6">
                    <label for="numLinea" class="form-label">Nº Línea</label>
                    <input type="text" class="form-control" name="numLinea" value="${lineCounter}" readonly>
                </div>
                <div class="col-md-6">
                    <label for="numOperacion" class="form-label">Nº Operación</label>
                    <input type="text" class="form-control" name="numOperacion" placeholder="Nº Operación">
                </div>
                <div class="col-md-6">
                    <label for="proveedorLinea" class="form-label">Proveedor</label>
                    <input type="text" class="form-control" name="proveedorLinea" placeholder="Proveedor">
                </div>
                <div class="col-md-6">
                    <label for="clienteLinea" class="form-label">Cliente</label>
                    <input type="text" class="form-control" name="clienteLinea" placeholder="Cliente">
                </div>
                <div class="col-md-6">
                    <label for="contratoVenta" class="form-label">Contrato Venta</label>
                    <input type="text" class="form-control" name="contratoVenta" placeholder="Contrato Venta">
                </div>
                <div class="col-md-6">
                    <label for="facturaVenta" class="form-label">Factura Venta</label>
                    <input type="text" class="form-control" name="facturaVenta" placeholder="Factura Venta">
                </div>
                <div class="col-md-6">
                    <label for="numContenedor" class="form-label">Nº Contenedor</label>
                    <input type="text" class="form-control" name="numContenedor" placeholder="Nº Contenedor">
                </div>
                <div class="col-md-6">
                    <label for="producto" class="form-label">Producto</label>
                    <input type="text" class="form-control" name="producto" placeholder="Producto">
                </div>
                <div class="col-md-6">
                    <label for="talla" class="form-label">Talla</label>
                    <input type="text" class="form-control" name="talla" placeholder="Talla">
                </div>
                    <div class="col-md-6">
                    <label for="paisOrigen" class="form-label">País de Origen</label>
                    <input type="text" class="form-control" name="paisOrigen" placeholder="País de Origen">
                </div>
                <div class="col-md-6">
                    <label for="pesoNeto" class="form-label">Peso Neto</label>
                    <input type="number" class="form-control" name="pesoNeto" placeholder="Peso Neto">
                </div>
                <div class="col-md-6">
                    <label for="unidad" class="form-label">Unidad</label>
                    <input type="text" class="form-control" name="unidad" placeholder="Unidad">
                </div>
                <div class="col-md-6">
                    <label for="bultos" class="form-label">Bultos</label>
                    <input type="number" class="form-control" name="bultos" placeholder="Bultos">
                </div>
                <div class="col-md-6">
                    <label for="precio" class="form-label">Precio</label>
                    <input type="number" class="form-control" name="precio" placeholder="Precio">
                </div>
                <div class="col-md-6">
                    <label for="incoterm" class="form-label">Incoterm</label>
                    <input type="text" class="form-control" name="incoterm" placeholder="Incoterm">
                </div>
                <div class="col-md-6">
                    <label for="moneda" class="form-label">Moneda</label>
                    <input type="text" class="form-control" name="moneda" placeholder="Moneda">
                </div>
                <div class="col-md-6">
                    <label for="comerciales" class="form-label">Comerciales</label>
                    <input type="text" class="form-control" name="comerciales" placeholder="Comerciales">
                </div>
                <div class="col-md-6">
                    <label for="transporte" class="form-label">Transporte</label>
                    <input type="number" class="form-control" name="transporte" placeholder="Transporte">
                </div>
            </div>
        `;
        container.appendChild(line);

        const deleteButton = line.querySelector('.btn-danger');
                deleteButton.addEventListener('click', () => {
                    LineaPedidoFormApp.removeLine(lineId);
                });
    },

    removeLine: (lineId) => {
            const lineElement = document.getElementById(lineId);
            if (lineElement) {
                lineElement.remove();
                lineCounter--;
            } else {
                console.error(`No se encontró la línea con ID: ${lineId}`);
            }
        },

    getLineasPedidoData: (pedidoVentaId) => {
        const lineas = [];
        document.querySelectorAll('#orderLinesContainer .card').forEach(card => {
            const linea = {
                idPedidoVenta: pedidoVentaId,
                n_linea: card.querySelector('input[name="numLinea"]').value || '',
                n_operacion: card.querySelector('input[name="numOperacion"]').value || '',
                proveedor: card.querySelector('input[name="proveedorLinea"]').value || '',
                cliente: card.querySelector('input[name="clienteLinea"]').value || '',
                contratoVenta: card.querySelector('input[name="contratoVenta"]').value || '',
                facturaVenta: card.querySelector('input[name="facturaVenta"]').value || '',
                n_contenedor: card.querySelector('input[name="numContenedor"]').value || '',
                producto: card.querySelector('input[name="producto"]').value || '',
                talla: card.querySelector('input[name="talla"]').value || '',
                paisOrigen: card.querySelector('input[name="paisOrigen"]').value || '',
                p_neto: card.querySelector('input[name="pesoNeto"]').value || '',
                unidad: card.querySelector('input[name="unidad"]').value || '',
                bultos: card.querySelector('input[name="bultos"]').value || '',
                precio: card.querySelector('input[name="precio"]').value || '',
                incoterm: card.querySelector('input[name="incoterm"]').value || '',
                moneda: card.querySelector('input[name="moneda"]').value || '',
                comerciales: card.querySelector('input[name="comerciales"]').value || '',
                transporte: card.querySelector('input[name="transporte"]').value || '',

            };

                lineas.push(linea);

        });
        return lineas;
    }
};

export default LineaPedidoFormApp;
