

const PedidoCompraFormApp = {
getPedidoCompraData: () => {
        return {
            n_operacion: document.getElementById('operacion').value.trim(),
            n_contenedor: document.getElementById('contenedor').value.trim(),
            proforma: document.getElementById('proforma').value.trim(),
            proveedor: document.getElementById('proveedor').value.trim(),
            cliente: document.getElementById('cliente').value.trim(),
            incoterm: document.getElementById('incoterm').value.trim(),
            referenciaProveedor: document.getElementById('referencia').value.trim()
        };
    }
    };

export default PedidoCompraFormApp;