import { formatDateToDDMMYYYY } from '/js/utils.js'
const DatosBarcoFormApp = {
    getDatosBarcoData: (pedidoCompraId) => {
        function validarCampoNumerico(valor, nombreCampo, esEntero = false) {
            if (valor === '') return true;
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
            return true;
        }

        const fleteVal = document.getElementById('flete').value;
        if (!validarCampoNumerico(fleteVal, 'Flete', false)) return null;

        return {
            idPedidoCompra: pedidoCompraId,
            nombreBarco: document.getElementById('nombreBarco').value,
            viaje: document.getElementById('viaje').value,
            naviera: document.getElementById('naviera').value,
            puertoEmbarque: document.getElementById('puertoEmbarque').value,
            puertoLlegada: document.getElementById('puertoLlegada').value,
            fecha_embarque: formatDateToDDMMYYYY(document.getElementById('fechaEmbarque').value),
            fecha_llegada: formatDateToDDMMYYYY(document.getElementById('fechaLlegada').value),
            flete: fleteVal,
            fecha_pago_flete: formatDateToDDMMYYYY(document.getElementById('fechaPagoFleteBarco').value),
            facturaFlete: document.getElementById('facturaFlete').value
        };
    }
};

export default DatosBarcoFormApp;
