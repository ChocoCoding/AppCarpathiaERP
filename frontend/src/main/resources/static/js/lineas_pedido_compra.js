// lineas_pedido_compra.js
import middleware from '/js/middleware.js';

let config = {};
let currentPage = 1; // Página inicial
let size = 10;        // Tamaño de página inicial
let sortBy = 'pedidoCompra.idPedidoCompra'; // Campo de ordenamiento inicial
let sortDir = 'desc'; // Dirección de ordenamiento inicial
let proveedor = '';
let cliente = '';
let search = '';
let searchFields = []; // Debes definir cómo se manejan los campos de búsqueda

const columnasAtributos = {
    2: 'pedidoCompra.idPedidoCompra',
    3: 'nLinea',
    4: 'nOperacion',
    5: 'proveedor',
    6: 'cliente',
    7: 'nContenedor',
    8: 'producto',
    9: 'talla',
    10: 'pNeto',
    11: 'unidad',
    12: 'bultos',
    13: 'precio',
    14: 'valorCompra',
    15: 'moneda',
    16: 'paisOrigen',
    17: 'status'
};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8702/api/config')  // Asegúrate de que la URL sea correcta y accesible
        .then(response => {
        if (!response.ok) {
            throw new Error('Error al cargar la configuración del backend');
        }
        return response.json();
    })
        .then(data => {
        config = data;
        currentPage = config.currentPage || 1;
        size = config.size || 10;
        sortBy = config.sortBy || 'pedidoCompra.idPedidoCompra';
        sortDir = config.sortDir || 'asc';
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
                    // Verificar si hay cambios sin guardar
                    const filasModificadas = document.querySelectorAll('tbody tr.modificado');
                    if (filasModificadas.length > 0) {
                        // Mostrar alerta de confirmación usando SweetAlert2
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
                                // Si el usuario confirma, navegar atrás
                                window.location.href = "/compras";
                            }
                            // Si el usuario cancela, no hacer nada
                        });
                    } else {
                        // Si no hay cambios sin guardar, navegar directamente
                        window.location.href = "/compras";
                    }
                },

        goHome: () => {
                    // Verificar si hay cambios sin guardar
                    const filasModificadas = document.querySelectorAll('tbody tr.modificado');
                    if (filasModificadas.length > 0) {
                        // Mostrar alerta de confirmación usando SweetAlert2
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
                                // Si el usuario confirma, navegar atrás
                                window.location.href = "/compras";
                            }
                            // Si el usuario cancela, no hacer nada
                        });
                    } else {
                        // Si no hay cambios sin guardar, navegar directamente
                        window.location.href = "/compras";
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

        // Cargar y renderizar líneas de pedido de compra
        cargarLineasPedidoCompra: () => {
            const url = new URL(config.lineasPedidosCompraEndpoint);
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
                LineasPedidoApp.mostrarAlerta('error', 'Error', 'Error al cargar las líneas de pedido de compra.');
                console.error('Error al cargar las líneas de pedido de compra:', error);
            });
        },

        // Renderizar la tabla con las líneas de pedido de compra
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


        // Renderizar una fila de línea de pedido de compra
        renderFilaLineaPedido: (linea) => {
            console.log('Renderizando fila para línea de pedido:', linea); // Depuración
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-linea-pedido', linea.idNumeroLinea);
            fila.setAttribute('data-id-pedido-compra', linea.idPedidoCompra);
            fila.setAttribute('data-status', linea.status);
                console.log("Estado LINEA" + linea.status)
                // Añadir clase basada en el estado
               if (linea.status && linea.status.toUpperCase() === 'T') {
                   fila.classList.add('status-terminado');
               } else {
                   fila.classList.remove('status-terminado'); // Asegurar que no tenga la clase si es 'P'
               }

            fila.innerHTML = `
        <td>
            <button class="delete-button" onclick="LineasPedidoApp.eliminarLineaPedido(${linea.idNumeroLinea})">🗑️</button>
        </td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.idPedidoCompra || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.n_linea || ''}</td>
        <td contenteditable="false" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.n_operacion || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.proveedor || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.cliente || ''}</td>
        <td contenteditable="false" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.n_contenedor || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.producto || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.talla || ''}</td>
        <td contenteditable="true" class="editable peso-neto" oninput="LineasPedidoApp.calcularValorCompra(this)">${linea.p_neto ? Number(linea.p_neto).toFixed(4) : ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.unidad || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.bultos || ''}</td>
        <td contenteditable="true" class="editable precio" oninput="LineasPedidoApp.calcularValorCompra(this)">${linea.precio ? Number(linea.precio).toFixed(4) : ''}</td>
        <td contenteditable="false" class="editable valor-compra-total">${linea.valor_compra ? Number(linea.valor_compra).toFixed(4) : ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.moneda || ''}</td>
        <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)">${linea.paisOrigen || ''}</td>
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
                LineasPedidoApp.cargarLineasPedidoCompra();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                LineasPedidoApp.cargarLineasPedidoCompra();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                LineasPedidoApp.cargarLineasPedidoCompra();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                LineasPedidoApp.cargarLineasPedidoCompra();
            }
        },

        // Función para guardar cambios (crear y actualizar)
        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');

            // Primero, verificar si alguna fila modificada pertenece a un pedido terminado
                const filasTerminado = Array.from(filasModificadas).filter(fila => fila.getAttribute('data-status') === 'T');

                if (filasTerminado.length > 0) {
                    const idsPedidosTerminado = [...new Set(Array.from(filasTerminado).map(fila => fila.getAttribute('data-id-pedido-compra')))];
                    LineasPedidoApp.mostrarAlerta('error', 'Error', `El pedido(s): ${idsPedidosTerminado.join(', ')} está(n) terminado(s). No se pueden guardar cambios en estos pedidos.`);
                    return; // Detener la función para prevenir el guardado
                }

            // Función auxiliar para validar un campo numérico
            function validarCampoNumerico(fila, index, nombreCampo, esEntero = true) {
                const valor = fila.children[index].innerText.trim();
                if (valor !== '') {
                    // Convertimos según sea entero o decimal
                    const numero = esEntero ? parseLong(valor) : parseFloat(valor);
                    if (numero === null || isNaN(numero)) {
                        const idLineaPedido = fila.getAttribute('data-id-linea-pedido');
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de datos',
                            text: `La celda "${nombreCampo}" en la fila ${
                                idLineaPedido
                                    ? 'con ID ' + idLineaPedido
                                    : 'nueva'
                            } requiere un valor numérico.`,
                            toast: true,
                            position: 'top-end',
                            timer: 3000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                        return false; // Indica que falló la validación
                    }
                }
                return true; // Validación OK
            }

            for (const fila of filasModificadas) {
                const idLineaPedido = fila.getAttribute('data-id-linea-pedido');

                // Validar campos numéricos
                // idPedidoCompra (columna 1, entero)
                if (!validarCampoNumerico(fila, 1, 'ID Pedido Compra')) return;
                // n_linea (columna 2, entero)
                if (!validarCampoNumerico(fila, 2, 'Nº Línea')) return;
                // n_operacion (columna 3, entero)
                if (!validarCampoNumerico(fila, 3, 'Nº Operación')) return;
                // p_neto (columna 9, decimal)
                if (!validarCampoNumerico(fila, 9, 'Peso Neto', false)) return;
                // bultos (columna 11, entero)
                if (!validarCampoNumerico(fila, 11, 'Bultos')) return;
                // precio (columna 12, decimal)
                if (!validarCampoNumerico(fila, 12, 'Precio', false)) return;
            }

            // Si llegamos hasta aquí, todos los datos numéricos están OK.
            // Ahora procedemos a guardar (crear o actualizar)
            filasModificadas.forEach(fila => {
                const idLineaPedido = fila.getAttribute('data-id-linea-pedido');
                const idPedidoCompra = fila.children[1].innerText.trim();

                LineasPedidoApp.validarExistenciaPedidoCompra(idPedidoCompra)
                    .then(existe => {
                        if (!existe) {
                            LineasPedidoApp.mostrarAlerta('error', 'Error', 'ID de Pedido de Compra inválido.', 3000);
                            return;
                        }

                        const datos = {
                            idPedidoCompra: parseLong(idPedidoCompra),
                            n_linea: parseLong(fila.children[2].innerText.trim()),
                            n_operacion: parseLong(fila.children[3].innerText.trim()),
                            proveedor: fila.children[4].innerText.trim(),
                            cliente: fila.children[5].innerText.trim(),
                            n_contenedor: fila.children[6].innerText.trim(),
                            producto: fila.children[7].innerText.trim(),
                            talla: fila.children[8].innerText.trim(),
                            p_neto: parseFloat(fila.children[9].innerText.trim()) || 0,
                            unidad: fila.children[10].innerText.trim(),
                            bultos: parseLong(fila.children[11].innerText.trim()),
                            precio: parseFloat(fila.children[12].innerText.trim()) || 0,
                            valor_compra: parseFloat(fila.querySelector('.valor-compra-total').innerText.trim()) || 0,
                            moneda: fila.children[14].innerText.trim(),
                            paisOrigen: fila.children[15].innerText.trim()
                        };

                        if (!idLineaPedido) {
                            // Crear nueva línea
                            middleware.post(config.lineasPedidosCompraEndpoint, datos)
                                .then((nuevaLinea) => {
                                    LineasPedidoApp.mostrarAlerta('success', 'Éxito', 'Línea de pedido creada correctamente.', 500);
                                    LineasPedidoApp.cargarLineasPedidoCompra();
                                })
                                .catch(() => {
                                    LineasPedidoApp.mostrarAlerta('error', 'Error', 'No se pudo crear la línea de pedido.');
                                });
                        } else {
                            // Actualizar línea existente
                            const endpoint = config.lineasPedidosCompraIdEndpoint.replace('{id}', idLineaPedido);
                            middleware.put(endpoint, datos)
                                .then(() => {
                                    LineasPedidoApp.mostrarAlerta('success', 'Éxito', 'Cambios guardados correctamente.', 500);
                                    fila.classList.remove('modificado');
                                })
                                .catch(() => {
                                    LineasPedidoApp.mostrarAlerta('error', 'Error', 'No se pudo guardar los cambios.');
                                });
                        }
                    })
                    .catch(() => {
                        LineasPedidoApp.mostrarAlerta('error', 'Error', 'Error al validar el ID de Pedido de Compra.');
                    });
            });
        },


        // Función para validar la existencia de un Pedido de Compra
        validarExistenciaPedidoCompra: (idPedidoCompra) => {
            return middleware.get(`${config.pedidosCompraEndpoint}/${idPedidoCompra}/exists`)
                .then(data => data.existe)
                .catch(error => {
                console.error('Error en la validación del ID del Pedido de Compra:', error);
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
                    const url = config.lineasPedidosCompraIdEndpoint.replace('{id}', idLineaPedido);
                    middleware.delete(url)
                        .then(() => {
                        LineasPedidoApp.mostrarAlerta('success', 'Éxito', 'Línea de pedido eliminada correctamente.', 300);
                        LineasPedidoApp.cargarLineasPedidoCompra(); // Recargar datos
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
                <td contenteditable="false" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="false" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable peso-neto" oninput="LineasPedidoApp.calcularValorCompra(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable precio" oninput="LineasPedidoApp.calcularValorCompra(this)"></td>
                <td contenteditable="false" class="valor-compra-total"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="LineasPedidoApp.marcarModificado(this)"></td>
            `;

            tbody.insertBefore(nuevaFila, tbody.firstChild);
            nuevaFila.classList.add('modificado', 'new-row');
        },

        // Función para eliminar una fila antes de guardarla
        eliminarFila: (boton) => {
            const fila = boton.closest('tr');
            fila.remove();
        },

        // Función para calcular el valor de compra total
        // Función para calcular el valor de compra total
        calcularValorCompra: (elemento) => {
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

            const valorCompraTotal = (pesoNeto * precio).toFixed(4); // Cambiado a 2 decimales

            fila.querySelector('.valor-compra-total').innerText = valorCompraTotal;

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
            LineasPedidoApp.cargarLineasPedidoCompra();
        },

        // Funciones para mostrar y ocultar la búsqueda y filtros
        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
                searchInput.value = '';
                LineasPedidoApp.filtrarLineas();

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

            if (searchFields.length === 0) {
                LineasPedidoApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            currentPage = 1; // Reiniciar a la primera página
            LineasPedidoApp.cargarLineasPedidoCompra();
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
            LineasPedidoApp.cargarLineasPedidoCompra();
        });
    } else {
        // DOM ya está cargado
        LineasPedidoApp.initTableScroll();
        LineasPedidoApp.cargarLineasPedidoCompra();
    }
});
