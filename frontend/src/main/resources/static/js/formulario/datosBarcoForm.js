

const DatosBarcoFormApp = {
    getDatosBarcoData: (pedidoCompraId) => {
       return {
        id_pedido_compra: pedidoCompraId,
            idPedidoCompra: document.getElementById('idPedidoBarco').value,
            n_operacion: document.getElementById('operacionBarco').value,
            nombreBarco: document.getElementById('nombreBarco').value,
            viaje: document.getElementById('viaje').value,
            naviera: document.getElementById('naviera').value,
            puertoEmbarque: document.getElementById('puertoEmbarque').value,
            puertoLlegada: document.getElementById('puertoLlegada').value,
            fechaEmbarque: document.getElementById('fechaEmbarque').value,
            fechaLlegada: document.getElementById('fechaLlegada').value,
            flete: document.getElementById('flete').value,
            fechaPagoFlete: document.getElementById('fechaPagoFleteBarco').value,
            facturaFlete: document.getElementById('facturaFlete').value
        };
           }
       };

export default DatosBarcoFormApp;
