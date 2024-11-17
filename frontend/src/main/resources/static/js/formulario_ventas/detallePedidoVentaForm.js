import { formatDateToDDMMYYYY } from '/js/utils.js'
const DetallePedidoFormApp = {
    getDetallesPedidoData: (pedidoVentaId) => {
        return {
            idPedidoVenta: pedidoVentaId,
            importador: document.getElementById('importador').value
        };
    }
};
export default DetallePedidoFormApp;
