

const PedidoVentaFormApp = {
getPedidoVentaData: () => {
        return {
            n_operacion: document.getElementById('operacion').value.trim(),
            proforma: document.getElementById('proforma').value.trim(),
            proveedor: document.getElementById('proveedor').value.trim(),
            incoterm: document.getElementById('incoterm').value.trim(),
            referenciaProveedor: document.getElementById('referencia').value.trim()
        };
    }
    };

export default PedidoVentaFormApp;