import { formatDateToDDMMYYYY } from '/js/utils.js'
const DetallePedidoFormApp = {
    getDetallesPedidoData: (pedidoCompraId) => {
        function validarCampoNumerico(valor, nombreCampo, esEntero = true) {
            if (valor === '') return true;
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
            return true;
        }


        return {
            idPedidoCompra: pedidoCompraId,
            contratoCompra: document.getElementById('contrato').value,
            terminado: document.getElementById('terminado').value,
            factProveedor: document.getElementById('factProveedor').value,
            n_fact_flete: document.getElementById('factFlete').value,
            fecha_pago_flete: formatDateToDDMMYYYY(document.getElementById('fechaPago').value),
            n_bl: document.getElementById('bl').value,
            observaciones: document.getElementById('observaciones').value
        };
    }
};

export default DetallePedidoFormApp;
