const PedidoVentaFormApp = {
    getPedidoVentaData: () => {
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

        const nOperacionVal = document.getElementById('operacion').value.trim();
        // Validar Nº Operación como entero
        if (!validarCampoNumerico(nOperacionVal, 'Nº Operación', true)) return null;

        return {
            n_operacion: nOperacionVal,
            n_contenedor: document.getElementById('contenedor').value.trim(),
            proforma: document.getElementById('proforma').value.trim(),
            proveedor: document.getElementById('proveedor').value.trim(),
            incoterm: document.getElementById('incoterm').value.trim(),
            referenciaProveedor: document.getElementById('referencia').value.trim()
        };
    }
};

export default PedidoVentaFormApp;
