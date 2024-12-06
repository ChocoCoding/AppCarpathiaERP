import { formatDateToDDMMYYYY } from '/js/utils.js'
const DatosBarcoFormApp = {
    getDatosBarcoData: (pedidoCompraId) => {
       return {
            idPedidoCompra: pedidoCompraId,
            nombreBarco: document.getElementById('nombreBarco').value,
            viaje: document.getElementById('viaje').value,
            naviera: document.getElementById('naviera').value,
            puertoEmbarque: document.getElementById('puertoEmbarque').value,
            puertoLlegada: document.getElementById('puertoLlegada').value,
            fecha_embarque: formatDateToDDMMYYYY(document.getElementById('fechaEmbarque').value),
            fecha_llegada: formatDateToDDMMYYYY(document.getElementById('fechaLlegada').value),
            flete: document.getElementById('flete').value,
            fecha_pago_flete: formatDateToDDMMYYYY(document.getElementById('fechaPago').value),
            facturaFlete: document.getElementById('facturaFlete').value
        };
           }
       };

export default DatosBarcoFormApp;
