const CostePedidoApp = {
   getCostesPedidoData: (pedidoCompraId) => {
        return {
            idPedidoCompra: pedidoCompraId,
            n_operacion: document.getElementById('operacionCostes').value,
            n_contenedor: document.getElementById('contenedorCostes').value,
            arancel: document.getElementById('arancel').value,
            sanidad: document.getElementById('sanidad').value,
            plastico: document.getElementById('plastico').value,
            carga: document.getElementById('carga').value,
            inland: document.getElementById('inland').value,
            muellaje: document.getElementById('muellaje').value,
            pif: document.getElementById('pif').value,
            despacho: document.getElementById('despacho').value,
            conexiones: document.getElementById('conexiones').value,
            iva: document.getElementById('iva').value,
            dec_iva: document.getElementById('decIva').value,
        };
    }
};

export default CostePedidoApp;
