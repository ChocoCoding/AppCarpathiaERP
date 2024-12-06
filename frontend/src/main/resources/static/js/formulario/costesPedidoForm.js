const CostePedidoApp = {
    getCostesPedidoData: (pedidoCompraId) => {
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

        const arancelVal = document.getElementById('arancel').value;
        const sanidadVal = document.getElementById('sanidad').value;
        const plasticoVal = document.getElementById('plastico').value;
        const cargaVal = document.getElementById('carga').value;
        const inlandVal = document.getElementById('inland').value;
        const muellajeVal = document.getElementById('muellaje').value;
        const pifVal = document.getElementById('pif').value;
        const despachoVal = document.getElementById('despacho').value;
        const conexionesVal = document.getElementById('conexiones').value;
        const ivaVal = document.getElementById('iva').value;
        const decIvaVal = document.getElementById('decIva').value;

        // Validar todos:
        if (!validarCampoNumerico(arancelVal, 'Arancel', false)) return null;
        if (!validarCampoNumerico(sanidadVal, 'Sanidad', false)) return null;
        if (!validarCampoNumerico(plasticoVal, 'Plástico', false)) return null;
        if (!validarCampoNumerico(cargaVal, 'Carga', false)) return null;
        if (!validarCampoNumerico(inlandVal, 'Inland', false)) return null;
        if (!validarCampoNumerico(muellajeVal, 'Muellaje', false)) return null;
        if (!validarCampoNumerico(pifVal, 'PIF', false)) return null;
        if (!validarCampoNumerico(despachoVal, 'Despacho', false)) return null;
        if (!validarCampoNumerico(conexionesVal, 'Conexiones', false)) return null;
        if (!validarCampoNumerico(ivaVal, 'IVA', false)) return null;
        if (!validarCampoNumerico(decIvaVal, 'Dec. IVA', false)) return null;

        return {
            idPedidoCompra: pedidoCompraId,
            arancel: arancelVal,
            sanidad: sanidadVal,
            plastico: plasticoVal,
            carga: cargaVal,
            inland: inlandVal,
            muellaje: muellajeVal,
            pif: pifVal,
            despacho: despachoVal,
            conexiones: conexionesVal,
            iva: ivaVal,
            dec_iva: decIvaVal
        };
    }
};

export default CostePedidoApp;
