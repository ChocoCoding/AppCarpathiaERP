

let lineCounter = 0;

const LineaPedidoFormApp = {
    agregarLineaPedido: () => {
        lineCounter++;
        const container = document.getElementById('orderLinesContainer');
        const lineId = `line-${lineCounter}`;
        const line = document.createElement('div');

        line.id = lineId;
        line.classList.add('position-relative', 'mb-3', 'card', 'p-3');

        line.innerHTML = `
            <button type="button" class="btn btn-danger btn-sm position-absolute top-0 end-0 m-2" onclick="LineaPedidoFormApp.removeLine(${lineCounter})">
                <i class="fas fa-trash-alt"></i>
            </button>
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="lineaIdPedido" class="form-label">ID Pedido de Compra</label>
                    <input type="text" class="form-control" name="lineaIdPedido" placeholder="ID Pedido de Compra" readonly>
                </div>
                <div class="col-md-6">
                    <label for="numLinea" class="form-label">Nº Línea</label>
                    <input type="text" class="form-control" name="numLinea" placeholder="Nº Línea">
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
                    <label for="moneda" class="form-label">Moneda</label>
                    <input type="text" class="form-control" name="moneda" placeholder="Moneda">
                </div>
                <div class="col-md-6">
                    <label for="paisOrigen" class="form-label">País de Origen</label>
                    <input type="text" class="form-control" name="paisOrigen" placeholder="País de Origen">
                </div>
            </div>
        `;
        container.appendChild(line);
    },

    removeLine: (lineId) => {
        const lineElement = document.getElementById(`line-${lineId}`);
        if (lineElement) {
            lineElement.remove();
        }
    },

    getLineasPedidoData: (pedidoCompraId) => {
        const lineas = [];
        document.querySelectorAll('#orderLinesContainer .card').forEach(card => {
            const linea = {
                idPedidoCompra: pedidoCompraId,
                n_operacion: card.querySelector('input[name="numOperacion"]').value || '',
                proveedor: card.querySelector('input[name="proveedorLinea"]').value || '',
                cliente: card.querySelector('input[name="clienteLinea"]').value || '',
                num_contenedor: card.querySelector('input[name="numContenedor"]').value || '',
                producto: card.querySelector('input[name="producto"]').value || '',
                talla: card.querySelector('input[name="talla"]').value || '',
                peso_neto: card.querySelector('input[name="pesoNeto"]').value || '',
                unidad: card.querySelector('input[name="unidad"]').value || '',
                bultos: card.querySelector('input[name="bultos"]').value || '',
                precio: card.querySelector('input[name="precio"]').value || '',
                moneda: card.querySelector('input[name="moneda"]').value || '',
                pais_origen: card.querySelector('input[name="paisOrigen"]').value || ''
            };

            // Solo agregar la línea si todos los campos requeridos están completos (opcional)
                lineas.push(linea);

        });
        return lineas;
    }
};

export default LineaPedidoFormApp;

