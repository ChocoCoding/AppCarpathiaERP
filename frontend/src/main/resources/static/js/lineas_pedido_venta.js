// lineas_pedido_venta.js
import middleware from '/js/middleware.js';

let config = {};
let currentPage = 1; // Página inicial
let size = 10;        // Tamaño de página inicial
let sortBy = 'pedidoVenta.idPedidoVenta'; // Campo de ordenamiento inicial
let sortDir = 'desc'; // Dirección de ordenamiento inicial
let proveedor = '';
let cliente = '';
let search = '';
let searchFields = []; // Campos de búsqueda seleccionados

// Mapeo de índices de columnas a nombres de campos
const columnasAtributos = {
    1: 'pedidoVenta.idPedidoVenta',
    2: 'nLinea',
    3: 'nOperacion',
    4: 'proveedor',
    5: 'cliente',
    6: 'contratoVenta',
    7: 'facturaVenta',
    8: 'nContenedor',
    9: 'producto',
    10: 'talla',
    11: 'paisOrigen',
    12: 'pNeto',
    13: 'unidad',
    14: 'bultos',
    15: 'precio',
    16: 'valorVenta',
    17: 'incoterm',
    18: 'moneda',
    19: 'comerciales',
    20: 'transporte',
    21: 'status'
};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8703/api/ventas/config')  // Asegúrate de que la URL sea correcta y accesible
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar la configuración del backend');
            }
            return response.json();
        })
        .then(data => {
            config = data;
            console.log('Configuración cargada:', config);
            currentPage = config.currentPage || 1;
            size = config.size || 10;
            sortBy = config.sortBy || 'pedidoVenta.idPedidoVenta';
            sortDir = config.sortDir || 'desc';
            proveedor = config.proveedor || '';
            cliente = config.cliente || '';
            return config;
        })
        .catch(error => {
            console.error('Error al cargar la configuración:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudo cargar la configuración de la aplicación.',
                toast: true,
                position: 'top-end',
                timer: 3000,
                timerProgressBar: true,
                showConfirmButton: false
            });
        });
}

cargarConfiguraciones().then(() => {
    console.log('Configuración cargada:', config);

    const LineasPedidoApp = {
        // Navegación
        goBack: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');
            if (filasModificadas.length > 0) {
                Swal.fire({
                    title: 'Hay cambios sin guardar',
                    text: '¿Estás seguro de que quieres salir?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Sí, salir',
                    cancelButtonText: 'Cancelar'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = "/ventas";
                    }
                });
            } else {
                window.location.href = "/ventas";
            }
        },
        goHome: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');
            if (filasModificadas.length > 0) {
                Swal.fire({
                    title: 'Hay cambios sin guardar',
                    text: '¿Estás seguro de que quieres salir?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Sí, salir',
                    cancelButtonText: 'Cancelar'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = "/home";
                    }
                });
            } else {
                window.location.href = "/home";
            }
        },

        logout: () => {
            window.location.href = "/logout";
        },

        // Marcar fila como modificada
        marcarModificado: (elemento) => {
            const fila = elemento.closest('tr');
            fila.classList.add('modificado');
        },

        // Mostrar alertas usando SweetAlert2
        mostrarAlerta: (tipo, titulo, texto, timer = 2000) => {
            Swal.fire({
                icon: tipo,
                title: titulo,
                text: texto,
                toast: true,
                position: 'top-end',
                timer: timer,
                timerProgressBar: true,
                showConfirmButton: false
            });
        },

        // Cargar y renderizar líneas de pedido de venta
        cargarLineasPedidoVenta: () => {
            const url = new URL(config.lineasPedidosVentaEndpoint);
            url.searchParams.append('page', currentPage - 1); // Backend es zero-based
            url.searchParams.append('size', size);
            url.searchParams.append('proveedor', proveedor);
            url.searchParams.append('cliente', cliente);
            url.searchParams.append('sortBy', sortBy);
            url.searchParams.append('sortDir', sortDir);

            // Incluir parámetros de búsqueda si existen
            if (search && searchFields.length > 0) {
                url.searchParams.append('search', search);
                searchFields.forEach(field => url.searchParams.append('searchFields', field));
            }

            console.log('Solicitando URL:', url.toString());

            middleware.get(url.toString())
                .then(data => {
                    console.log('Datos recibidos:', data);
                    LineasPedidoApp.renderTabla(data.content);
                    LineasPedidoApp.actualizarPaginacion(data.number + 1, data.totalPages);
                })
                .catch(error => {
                    LineasPedidoApp.mostrarAlerta('error', 'Error', 'Error al cargar las líneas de pedido de venta.');
                    console.error('Error al cargar las líneas de pedido de venta:', error);
                });
        },

        // Renderizar la tabla con las líneas de pedido de venta
        renderTabla: (lineas) => {
            console.log('Renderizando tabla con líneas:', lineas); // Depuración

            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = ''; // Limpiar la tabla antes de renderizar

            lineas.forEach(linea => {
                LineasPedidoApp.renderFilaLineaPedido(linea);
            });
        },


        // Renderizar una fila de línea de pedido de venta
        renderFilaLineaPedido: (linea) => {
            console.log('Renderizando fila para línea de pedido:', linea); // Depuración
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-linea-pedido', linea.idLineaPedidoVenta);
            fila.setAttribute('data-id-pedido-venta', linea.idPedidoVenta);
                        fila.setAttribute('data-status', linea.status);
                            // Añadir clase basada en el estado
                           if (linea.status && linea.status.toUpperCase() === 'T') {
                               fila.classList.add('status-terminado');
                           } else {
                               fila.classList.remove('status-terminado'); // Asegurar que no tenga la clase si es 'P'
                           }

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="LineasPedidoApp.eliminarLineaPedido(${linea.idLineaPedidoVenta})">🗑️</button>
                </td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.idPedidoVenta || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.n_linea || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.n_operacion || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.proveedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.cliente || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.contratoVenta || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.facturaVenta || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.n_contenedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.producto || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.talla || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.paisOrigen || ''}</td>
                <td contenteditable="true" class="editable peso-neto" oninput="LineasPedidoApp.calcularValorVenta(this)">${linea.p_neto ? Number(linea.p_neto).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.unidad || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.bultos || ''}</td>
                <td contenteditable="true" class="editable precio" oninput="LineasPedidoApp.calcularValorVenta(this)">${linea.precio ? Number(linea.precio).toFixed(4) : ''}</td>
                <td contenteditable="false" class="editable valor-venta-total">${linea.valor_venta ? Number(linea.valor_venta).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.incoterm || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.moneda || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.comerciales || ''}</td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.transporte ? Number(linea.transporte).toFixed(4) : ''}</td>
            `;

            tbody.appendChild(fila);
        },

        // Actualizar los controles de paginación
        actualizarPaginacion: (currentPageFromBackend, totalPagesFromBackend) => {
            currentPage = currentPageFromBackend;
            config.totalPages = totalPagesFromBackend;

            console.log(`Página actual: ${currentPage} / Total páginas: ${config.totalPages}`); // Depuración

            // Obtener los elementos de paginación
            const primeraPaginaSpan = document.getElementById('primera-pagina-span');
            const anteriorPaginaSpan = document.getElementById('anterior-span');
            const siguientePaginaSpan = document.getElementById('siguiente-span');
            const ultimaPaginaSpan = document.getElementById('ultima-pagina-span');

            if (!primeraPaginaSpan || !anteriorPaginaSpan || !siguientePaginaSpan || !ultimaPaginaSpan) {
                console.error('No se encontraron los elementos de paginación en el DOM.');
                return;
            }

            // Deshabilitar o habilitar "Primera Página" y "Anterior"
            if (currentPage <= 1) {
                primeraPaginaSpan.innerHTML = `<span class="disabled">Primera Página</span>`;
                anteriorPaginaSpan.innerHTML = `<span class="disabled">Anterior</span>`;
            } else {
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="LineasPedidoApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="LineasPedidoApp.irAPaginaAnterior()">Anterior</a>`;
            }

            // Deshabilitar o habilitar "Siguiente Página" y "Última Página"
            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="LineasPedidoApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="LineasPedidoApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

        // Funciones de Navegación de Paginación
        irAPrimeraPagina: () => {
            if (currentPage > 1) {
                currentPage = 1;
                LineasPedidoApp.cargarLineasPedidoVenta();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                LineasPedidoApp.cargarLineasPedidoVenta();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                LineasPedidoApp.cargarLineasPedidoVenta();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                LineasPedidoApp.cargarLineasPedidoVenta();
            }
        },

        // Función para guardar cambios (crear y actualizar)
        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');
            const formatoDecimal = /^-?\d+(\.\d+)?$/;

            const filasTerminado = Array.from(filasModificadas).filter(fila => fila.getAttribute('data-status') === 'T');

            if (filasTerminado.length > 0) {
                   const idsPedidosTerminado = [...new Set(Array.from(filasTerminado).map(fila => fila.getAttribute('data-id-pedido-compra')))];
                   LineasPedidoApp.mostrarAlerta('error', 'Error', `El pedido(s): ${idsPedidosTerminado.join(', ')} está(n) terminado(s). No se pueden guardar cambios en estos pedidos.`);
                   return; // Detener la función para prevenir el guardado
                }


            // Función auxiliar para validar un campo numérico
            function validarCampoNumerico(valor, nombreCampo, esEntero = true) {
                if (valor === '') {
                    // Campo vacío, podría ser permitido si es una nueva fila
                    return true;
                }

                if (esEntero) {
                    const numero = parseInt(valor, 10);
                    if (isNaN(numero)) {
                        return false;
                    }
                } else {
                    const numero = parseFloat(valor);
                    if (isNaN(numero)) {
                        return false;
                    }
                }
                return true;
            }

            // Validar todos los campos numéricos de todas las filas modificadas
            for (const fila of filasModificadas) {
                const idPedidoVenta = fila.children[1].innerText.trim();
                const nLinea = fila.children[2].innerText.trim();
                const nOperacion = fila.children[3].innerText.trim();
                const pNeto = fila.children[12].innerText.trim();
                const bultos = fila.children[14].innerText.trim();
                const precio = fila.children[15].innerText.trim();
                const transporte = fila.children[20].innerText.trim();

                // Validaciones
                if (!validarCampoNumerico(idPedidoVenta, 'ID Pedido Venta')) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "ID Pedido Venta" debe ser un número entero.');
                    return;
                }
                if (!validarCampoNumerico(nLinea, 'Nº Línea')) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "Nº Línea" debe ser un número entero.');
                    return;
                }
                if (!validarCampoNumerico(nOperacion, 'Nº Operación')) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "Nº Operación" debe ser un número entero.');
                    return;
                }
                if (!validarCampoNumerico(pNeto, 'Peso Neto', false)) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "Peso Neto" debe ser un número decimal.');
                    return;
                }
                if (!validarCampoNumerico(bultos, 'Bultos')) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "Bultos" debe ser un número entero.');
                    return;
                }
                if (!validarCampoNumerico(precio, 'Precio', false)) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "Precio" debe ser un número decimal.');
                    return;
                }
                if (!validarCampoNumerico(transporte, 'Transporte', false)) {
                    LineasPedidoApp.mostrarAlerta('error', 'Error de datos', 'El campo "Transporte" debe ser un número decimal.');
                    return;
                }
            }

            // Si todas las validaciones pasan, proceder con el guardado
            filasModificadas.forEach(fila => {
                const idLineaPedido = fila.getAttribute('data-id-linea-pedido');
                const idPedidoVenta = fila.children[1].innerText.trim();
                const nLinea = fila.children[2].innerText.trim();
                const nOperacion = fila.children[3].innerText.trim();
                const proveedor = fila.children[4].innerText.trim();
                const cliente = fila.children[5].innerText.trim();
                const contratoVenta = fila.children[6].innerText.trim();
                const facturaVenta = fila.children[7].innerText.trim();
                const nContenedor = fila.children[8].innerText.trim();
                const producto = fila.children[9].innerText.trim();
                const talla = fila.children[10].innerText.trim();
                const paisOrigen = fila.children[11].innerText.trim();
                const pNeto = parseFloat(fila.children[12].innerText.trim()) || 0;
                const unidad = fila.children[13].innerText.trim();
                const bultos = parseInt(fila.children[14].innerText.trim(), 10) || 0;
                const precio = parseFloat(fila.children[15].innerText.trim()) || 0;
                const valorVenta = parseFloat(fila.querySelector('.valor-venta-total').innerText.trim()) || 0;
                const incoterm = fila.children[17].innerText.trim();
                const moneda = fila.children[18].innerText.trim();
                const comerciales = fila.children[19].innerText.trim();
                const transporte = parseFloat(fila.children[20].innerText.trim()) || 0;

                const datos = {
                    idPedidoVenta: idPedidoVenta ? parseInt(idPedidoVenta, 10) : null,
                    n_linea: nLinea ? parseInt(nLinea, 10) : null,
                    n_operacion: nOperacion ? parseInt(nOperacion, 10) : null,
                    proveedor: proveedor,
                    cliente: cliente,
                    contratoVenta: contratoVenta,
                    facturaVenta: facturaVenta,
                    n_contenedor: nContenedor,
                    producto: producto,
                    talla: talla,
                    paisOrigen: paisOrigen,
                    p_neto: pNeto,
                    unidad: unidad,
                    bultos: bultos,
                    precio: precio,
                    valor_venta: valorVenta,
                    incoterm: incoterm,
                    moneda: moneda,
                    comerciales: comerciales,
                    transporte: transporte
                };

                console.log(`Guardando línea de pedido: ${idLineaPedido ? 'Actualización' : 'Creación'}`, datos); // Depuración

                if (idLineaPedido) {
                    // Actualizar línea existente
                    LineasPedidoApp.validarExistenciaPedidoVenta(idPedidoVenta)
                        .then(existe => {
                            if (!existe) {
                                LineasPedidoApp.mostrarAlerta('error', 'Error', 'ID de Pedido de Venta inválido.', 3000);
                                return;
                            }

                            const url = config.lineasPedidosVentaIdEndpoint.replace('{id}', idLineaPedido);
                            middleware.put(url, datos)
                                .then(() => {
                                    LineasPedidoApp.mostrarAlerta('success', 'Éxito', 'Cambios guardados correctamente.', 2000);
                                    fila.classList.remove('modificado');
                                    LineasPedidoApp.cargarLineasPedidoVenta(); // Recargar la tabla
                                })
                                .catch(() => {
                                    LineasPedidoApp.mostrarAlerta('error', 'Error', 'No se pudo guardar los cambios.');
                                });
                        })
                        .catch(() => {
                            LineasPedidoApp.mostrarAlerta('error', 'Error', 'Error al validar el ID de Pedido de Venta.');
                        });
                } else {
                    // Crear nueva línea
                    middleware.post(config.lineasPedidosVentaEndpoint, datos)
                        .then((nuevaLinea) => {
                            LineasPedidoApp.mostrarAlerta('success', 'Éxito', 'Línea de pedido creada correctamente.', 2000);
                            LineasPedidoApp.cargarLineasPedidoVenta(); // Recargar la tabla
                        })
                        .catch(() => {
                            LineasPedidoApp.mostrarAlerta('error', 'Error', 'No se pudo crear la línea de pedido.');
                        });
                }
            });
        },

        // Función para validar la existencia de un Pedido de Venta
        validarExistenciaPedidoVenta: (idPedidoVenta) => {
            return middleware.get(`${config.pedidosVentaEndpoint}/${idPedidoVenta}/exists`)
                .then(data => data.existe)
                .catch(error => {
                    console.error('Error en la validación del ID del Pedido de Venta:', error);
                    return false;
                });
        },

        // Función para eliminar una línea de pedido
        eliminarLineaPedido: (idLineaPedido) => {
            if (!idLineaPedido) {
                LineasPedidoApp.mostrarAlerta('error', 'Error', 'ID de Línea de Pedido inválido.');
                return;
            }

            Swal.fire({
                title: '¿Estás seguro?',
                text: "¿Deseas eliminar esta línea de pedido?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.lineasPedidosVentaIdEndpoint.replace('{id}', idLineaPedido);
                    middleware.delete(url)
                        .then(() => {
                            LineasPedidoApp.mostrarAlerta('success', 'Éxito', 'Línea de pedido eliminada correctamente.', 2000);
                            LineasPedidoApp.cargarLineasPedidoVenta(); // Recargar la tabla
                        })
                        .catch(() => {
                            LineasPedidoApp.mostrarAlerta('error', 'Error', 'No se pudo eliminar la línea de pedido.');
                        });
                }
            });
        },

        // Función para crear una nueva línea de pedido editable
        crearLineaPedido: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="LineasPedidoApp.eliminarFila(this)">🗑️</button>
                </td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable peso-neto" oninput="LineasPedidoApp.calcularValorVenta(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable precio" oninput="LineasPedidoApp.calcularValorVenta(this)"></td>
                <td contenteditable="false" class="editable valor-venta-total"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
            `;

            tbody.insertBefore(nuevaFila, tbody.firstChild);
            nuevaFila.classList.add('modificado', 'new-row');
            console.log('Fila nueva creada y agregada al DOM'); // Depuración
        },

        // Función para eliminar una fila antes de guardarla
        eliminarFila: (boton) => {
            const fila = boton.closest('tr');
            console.log('Eliminando fila:', fila); // Depuración
            fila.remove();
        },

        // Función para calcular el valor de venta total
        calcularValorVenta: (elemento) => {
            const fila = elemento.closest('tr');
            const pesoNetoField = fila.querySelector('.peso-neto');
            const precioField = fila.querySelector('.precio');

            const corregirFormatoDecimal = (field) => {
                let selection = window.getSelection();
                let range = selection.getRangeAt(0);
                let startOffset = range.startOffset;

                const originalText = field.innerText;
                const newText = originalText.replace(',', '.');

                if (originalText !== newText) {
                    field.innerText = newText;

                    let textNode = field.firstChild;
                    if (textNode) {
                        let newOffset = Math.min(startOffset + (newText.length - originalText.length), newText.length);
                        range.setStart(textNode, newOffset);
                        range.setEnd(textNode, newOffset);
                        selection.removeAllRanges();
                        selection.addRange(range);
                    }
                }
            };

            corregirFormatoDecimal(pesoNetoField);
            corregirFormatoDecimal(precioField);

            const pesoNeto = parseFloat(pesoNetoField.innerText.trim()) || 0;
            const precio = parseFloat(precioField.innerText.trim()) || 0;

            const valorVentaTotal = (pesoNeto * precio).toFixed(6);

            fila.querySelector('.valor-venta-total').innerText = valorVentaTotal;

            fila.classList.add('modificado');
        },

        // Función para ordenar la tabla
        sortTable: (campo) => {
            if (sortBy === campo) {
                sortDir = sortDir === 'asc' ? 'desc' : 'asc';
            } else {
                sortBy = campo;
                sortDir = 'asc';
            }

            console.log(`Ordenando por: ${sortBy} (${sortDir})`);
            LineasPedidoApp.cargarLineasPedidoVenta();
        },

        // Funciones para mostrar y ocultar la búsqueda y filtros
        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
            searchInput.value = '';
            search = '';
            searchFields = [];
            currentPage = 1;
            LineasPedidoApp.cargarLineasPedidoVenta();
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        // Función para filtrar líneas de pedido
        filtrarLineas: () => {
            search = document.getElementById('search-input').value.trim();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked'))
                .map(input => parseInt(input.value));

            searchFields = columnasSeleccionadas.map(index => columnasAtributos[index]).filter(field => field);

            if (searchFields.length === 0 && search !== '') {
                LineasPedidoApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            currentPage = 1; // Reiniciar a la primera página
            LineasPedidoApp.cargarLineasPedidoVenta();
        },


        // Función para inicializar el scroll en la tabla
        initTableScroll: () => {
            const tableContainer = document.querySelector('.table-container');
            let isDown = false;
            let startX;
            let scrollLeft;

            tableContainer.addEventListener('mousedown', (e) => {
                isDown = true;
                tableContainer.classList.add('active');
                startX = e.pageX - tableContainer.offsetLeft;
                scrollLeft = tableContainer.scrollLeft;
            });

            tableContainer.addEventListener('mouseleave', () => {
                isDown = false;
                tableContainer.classList.remove('active');
            });

            tableContainer.addEventListener('mouseup', () => {
                isDown = false;
                tableContainer.classList.remove('active');
            });

            tableContainer.addEventListener('mousemove', (e) => {
                if (!isDown) return;
                e.preventDefault();
                const x = e.pageX - tableContainer.offsetLeft;
                const walk = (x - startX) * 2;
                tableContainer.scrollLeft = scrollLeft - walk;
            });
        }
    };

    // Función para convertir cadenas a números de manera segura
    function parseLong(value) {
        const parsed = parseInt(value, 10);
        return isNaN(parsed) ? null : parsed;
    }

    function parseFloatSafe(value) {
        const parsed = parseFloat(value);
        return isNaN(parsed) ? null : parsed;
    }

    // Exponer globalmente para que las funciones sean accesibles desde el HTML
    window.LineasPedidoApp = LineasPedidoApp;

    // Inicializar la tabla y cargar los datos después de que el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            LineasPedidoApp.initTableScroll();
            LineasPedidoApp.cargarLineasPedidoVenta();
        });
    } else {
        // DOM ya está cargado
        LineasPedidoApp.initTableScroll();
        LineasPedidoApp.cargarLineasPedidoVenta();
    }
});
