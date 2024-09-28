

const DetallePedidoFormApp = {
    getDetallesPedidoData: (pedidoCompraId) => {
        return {
            idPedidoCompra: document.getElementById('idPedido').value,
            n_operacion: document.getElementById('operacionDetalle').value,
            contratoCompra: document.getElementById('contrato').value,
            terminado: document.getElementById('terminado').value,
            factProveedor: document.getElementById('factProveedor').value,
            n_fact_flete: document.getElementById('factFlete').value,
            fecha_pago_flete: document.getElementById('fechaPago').value,
            n_bl: document.getElementById('bl').value,
            observaciones: document.getElementById('observaciones').value
        };
    }
};

export default DetallePedidoFormApp;
