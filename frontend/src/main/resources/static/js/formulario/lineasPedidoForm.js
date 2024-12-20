let lineCounter = 0;
const LineaPedidoFormApp = {
    agregarLineaPedido: (pedidoCompraId) => {
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
                    <label for="idPedidoLinea" class="form-label">ID Pedido de Compra</label>
                    <input type="text" class="form-control" id="idPedidoLinea-${lineCounter}" value="${pedidoCompraId}" placeholder="ID Pedido de Compra" readonly>
                </div>

                <div class="col-md-6">
                    <label for="numLinea" class="form-label">Nº Línea</label>
                    <input type="text" class="form-control" name="numLinea" value="${lineCounter}" readonly>
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

    getLineasPedidoData: (pedidoCompraId) => {
            function validarCampoNumerico(valor, nombreCampo, esEntero = true) {
                if (valor === '') return true;
                if (esEntero) {
                    const numero = parseInt(valor, 10);
                    if (isNaN(numero)) {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de datos',
                            text: `El campo "${nombreCampo}" debe ser un número entero.`,
                            toast: true,
                            position: 'top-end',
                            timer: 3000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                        return false;
                    }
                } else {
                    const numero = parseFloat(valor);
                    if (isNaN(numero)) {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de datos',
                            text: `El campo "${nombreCampo}" debe ser un número decimal.`,
                            toast: true,
                            position: 'top-end',
                            timer: 3000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                        return false;
                    }
                }
                return true;
            }

            const lineas = [];
            document.querySelectorAll('#orderLinesContainer .card').forEach(card => {
                const nLineaVal = card.querySelector('input[name="numLinea"]').value || '';
                const pNetoVal = card.querySelector('input[name="pesoNeto"]').value || '';
                const bultosVal = card.querySelector('input[name="bultos"]').value || '';
                const precioVal = card.querySelector('input[name="precio"]').value || '';

                if (!validarCampoNumerico(nLineaVal, 'Nº Línea', true)) return null;
                if (!validarCampoNumerico(pNetoVal, 'Peso Neto', false)) return null;
                if (!validarCampoNumerico(bultosVal, 'Bultos', true)) return null;
                if (!validarCampoNumerico(precioVal, 'Precio', false)) return null;

                const linea = {
                    idPedidoCompra: pedidoCompraId,
                    n_linea: nLineaVal,
                    proveedor: card.querySelector('input[name="proveedorLinea"]').value || '',
                    cliente: card.querySelector('input[name="clienteLinea"]').value || '',
                    producto: card.querySelector('input[name="producto"]').value || '',
                    talla: card.querySelector('input[name="talla"]').value || '',
                    p_neto: pNetoVal,
                    unidad: card.querySelector('input[name="unidad"]').value || '',
                    bultos: bultosVal,
                    precio: precioVal,
                    moneda: card.querySelector('input[name="moneda"]').value || '',
                    paisOrigen: card.querySelector('input[name="paisOrigen"]').value || ''
                };
                lineas.push(linea);
            });
            return lineas;
        }
    };

export default LineaPedidoFormApp;

