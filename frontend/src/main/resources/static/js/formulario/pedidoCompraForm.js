const PedidoCompraFormApp = {
    getPedidoCompraData: () => {
        // Insertamos la función de validación aquí mismo o la importamos
        function validarCampoNumerico(valor, nombreCampo, esEntero = true) {
            if (valor === '') return true; // permitir vacío si fuera opcional
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
        if (!validarCampoNumerico(nOperacionVal, 'Nº Operación', true)) {
            return null; // Si falla la validación, no retornes datos
        }

        return {
            n_operacion: nOperacionVal,
            n_contenedor: document.getElementById('contenedor').value.trim(),
            proforma: document.getElementById('proforma').value.trim(),
            proveedor: document.getElementById('proveedor').value.trim(),
            cliente: document.getElementById('cliente').value.trim(),
            incoterm: document.getElementById('incoterm').value.trim(),
            referenciaProveedor: document.getElementById('referencia').value.trim()
        };
    }
};

export default PedidoCompraFormApp;
