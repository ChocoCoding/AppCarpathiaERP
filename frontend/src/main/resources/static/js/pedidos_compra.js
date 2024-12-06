// pedidos_compra.js
import middleware from '/js/middleware.js'; // Asegúrate de que la ruta es correcta

// Declaración de variables de estado
let config = {};
let currentPage = 1; // Página inicial
let size = 10;        // Tamaño de página inicial
let sortBy = 'idPedidoCompra'; // Campo de ordenamiento inicial
let sortDir = 'asc';
let proveedor = '';
let cliente = '';

// Mapeo de índices de columnas a nombres de campos (actualizado)
const columnasAtributos = {
    2: 'idPedidoCompra',
    3: 'nOperacion',
    4: 'nContenedor',
    5: 'proforma',
    6: 'proveedor',
    7: 'cliente',
    8: 'incoterm',
    9: 'referenciaProveedor'
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
            // Inicializar variables de estado si están presentes en config
            currentPage = config.currentPage || 1;
            size = config.size || 10;
            sortBy = config.sortBy || 'idPedidoCompra';
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

    const PedidoCompraApp = {
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
            // Similar implementación para otras funciones de navegación si es necesario
            // Puedes aplicar la misma lógica si deseas verificar cambios antes de navegar a Home
            window.location.href = "/home";
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

        // Cargar y renderizar pedidos de compra
        cargarPedidosCompra: () => {
            const url = new URL(config.pedidosCompraEndpoint);
            url.searchParams.append('page', currentPage - 1); // Backend es zero-based
            url.searchParams.append('size', size);
            url.searchParams.append('proveedor', proveedor);
            url.searchParams.append('cliente', cliente);
            url.searchParams.append('sortBy', sortBy);
            url.searchParams.append('sortDir', sortDir);

            // Incluir parámetros de búsqueda si existen
            if (config.search && config.searchFields && config.searchFields.length > 0) {
                url.searchParams.append('search', config.search);
                config.searchFields.forEach(field => url.searchParams.append('searchFields', field));
            }

            console.log('Solicitando URL:', url.toString());

            middleware.get(url.toString())
                .then(data => {
                    console.log('Datos recibidos:', data);
                    // Usar la nueva función para procesar y renderizar
                    PedidoCompraApp.processAndRenderPedidos(data.content);
                    PedidoCompraApp.actualizarPaginacion(data.number + 1, data.totalPages);
                })
                .catch(error => {
                    PedidoCompraApp.mostrarAlerta('error', config.error, config.errorCargarPedidos);
                    console.error('Error al cargar los pedidos de compra:', error);
                });
        },

        // Renderizar la tabla con los pedidos de compra
        renderTabla: (pedidos) => {
            console.log('Renderizando tabla con pedidos:', pedidos); // Depuración
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = ''; // Limpiar la tabla antes de renderizar

            pedidos.forEach(pedido => {
                PedidoCompraApp.renderFilaPedido(pedido);
            });
        },

        // Renderizar una fila de pedido de compra
        renderFilaPedido: (pedido) => {
            console.log('Renderizando fila para pedido:', pedido);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-pedido-compra', pedido.idPedidoCompra);

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoCompraApp.eliminarPedido(${pedido.idPedidoCompra})">🗑️</button>
                </td>
                <td>${pedido.idPedidoCompra}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.n_operacion || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.n_contenedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.proforma || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.proveedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.cliente || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.incoterm || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoCompraApp.marcarModificado(this)">${pedido.referenciaProveedor || ''}</td>
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
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAPaginaAnterior()">Anterior</a>`;
            }

            // Deshabilitar o habilitar "Siguiente Página" y "Última Página"
            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

        // Funciones de Navegación de Paginación
        irAPrimeraPagina: () => {
            if (currentPage > 1) {
                currentPage = 1;
                PedidoCompraApp.cargarPedidosCompra();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                PedidoCompraApp.cargarPedidosCompra();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                PedidoCompraApp.cargarPedidosCompra();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                PedidoCompraApp.cargarPedidosCompra();
            }
        },

        // Función para guardar cambios (crear y actualizar)
        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');

            console.log(`Filas modificadas encontradas: ${filasModificadas.length}`); // Depuración

            // Antes de enviar cambios, validamos que los campos numéricos sean valores numéricos
            for (const fila of filasModificadas) {
                const idPedidoCompra = fila.getAttribute('data-id-pedido-compra');
                const valorOperacion = fila.children[2].innerText.trim(); // valor de n_operacion

                // Verificar si n_operacion es numérico si no está vacío
                if (valorOperacion !== '' && parseLong(valorOperacion) === null) {
                    // Mostrar alerta de error
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de datos',
                        text: `La celda "Nº Operación" ${
                            idPedidoCompra
                                ? 'en la fila con ID ' + idPedidoCompra
                                : 'en la nueva fila creada'
                        } requiere un valor numérico.`,
                        toast: true,
                        position: 'top-end',
                        timer: 3000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                    return; // Detener el guardado, el usuario debe corregir el error
                }
            }

            // Si pasa la validación, proceder con el guardado:
            filasModificadas.forEach(fila => {
                const idPedidoCompra = fila.getAttribute('data-id-pedido-compra');
                const datos = {
                    n_operacion: parseLong(fila.children[2].innerText.trim()),
                    n_contenedor: fila.children[3].innerText.trim(),
                    proforma: fila.children[4].innerText.trim(),
                    proveedor: fila.children[5].innerText.trim(),
                    cliente: fila.children[6].innerText.trim(),
                    incoterm: fila.children[7].innerText.trim(),
                    referenciaProveedor: fila.children[8].innerText.trim()
                };

                console.log(`Guardando pedido: ${idPedidoCompra ? 'Actualización' : 'Creación'}`, datos); // Depuración

                if (!idPedidoCompra) {
                    // Crear nuevo pedido
                    middleware.post(config.pedidosCompraEndpoint, datos)
                        .then((nuevoPedido) => {
                            PedidoCompraApp.mostrarAlerta('success', config.creacionExitosa, config.pedidoCreadoExito);
                            PedidoCompraApp.cargarPedidosCompra(); // Recargar la tabla
                        })
                        .catch((error) => {
                            PedidoCompraApp.mostrarAlerta('error', 'Error', error.message || config.errorCrearPedido);
                            console.error('Error al crear pedido:', error);
                        });
                } else {
                    // Actualizar pedido existente
                    const url = config.pedidoCompraIdEndpoint.replace('{id}', idPedidoCompra);
                    middleware.put(url, datos)
                        .then(() => {
                            PedidoCompraApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito);
                            PedidoCompraApp.cargarPedidosCompra(); // Recargar la tabla
                        })
                        .catch((error) => {
                            PedidoCompraApp.mostrarAlerta('error', 'Error', error.message || config.errorGuardarCambios);
                            console.error('Error al actualizar pedido:', error);
                        });
                }
            });
        },

        // Función para eliminar un pedido
        eliminarPedido: (idPedidoCompra) => {
            console.log(`Intentando eliminar pedido: ${idPedidoCompra}`); // Depuración
            Swal.fire({
                title: config.eliminarPedidoConfirmacion,
                text: config.eliminarPedidoConfirmacion,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.pedidoCompraIdEndpoint.replace('{id}', idPedidoCompra);
                    middleware.delete(url)
                        .then(() => {
                            PedidoCompraApp.mostrarAlerta('success', config.pedidoEliminadoExito, config.pedidoEliminadoExito);
                            PedidoCompraApp.cargarPedidosCompra(); // Recargar la tabla
                        })
                        .catch(() => {
                            PedidoCompraApp.mostrarAlerta('error', config.error, config.errorEliminarPedido);
                        });
                }
            });
        },

        // Función para crear una nueva fila editable
        crearPedidoCompra: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoCompraApp.eliminarFila(this)">🗑️</button>
                </td>
                <td></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
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

        // Función para ordenar la tabla
        sortTable: (campo) => {
            if (sortBy === campo) {
                sortDir = sortDir === 'asc' ? 'desc' : 'asc';
            } else {
                sortBy = campo;
                sortDir = 'asc';
            }

            console.log(`Ordenando por: ${sortBy} (${sortDir})`); // Depuración
            PedidoCompraApp.cargarPedidosCompra();
        },

        // Funciones para mostrar y ocultar la búsqueda y filtros
        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
            searchInput.value = '';
            config.search = '';
            config.searchFields = [];
            currentPage = 1;
            PedidoCompraApp.cargarPedidosCompra();
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        // Función para filtrar pedidos (servidor)
        filtrarPedidos: () => {
            const searchInput = document.getElementById('search-input').value.trim();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked')).map(input => parseInt(input.value));
            const searchFields = columnasSeleccionadas.map(index => columnasAtributos[index]).filter(field => field); // Obtener nombres de campos

            if (searchFields.length === 0) {
                PedidoCompraApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            // Actualizar variables de estado
            config.search = searchInput;
            config.searchFields = searchFields;

            // Resetear la paginación a la primera página
            currentPage = 1;

            // Cargar los pedidos con los nuevos parámetros de búsqueda
            PedidoCompraApp.cargarPedidosCompra();
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
        },

        // Nueva función para procesar y renderizar pedidos de compra según la lógica de búsqueda avanzada
        processAndRenderPedidos: (pedidos) => {
            const searchQuery = config.search ? config.search.trim() : '';
            const searchFields = config.searchFields || [];

            if (searchQuery && searchFields.length > 0) {
                // Buscar coincidencias exactas en cualquiera de los campos filtrados
                const exactMatches = pedidos.filter(pedido => {
                    return searchFields.some(field => {
                        const valorCampo = pedido[field];
                        // Manejar valores numéricos y de texto
                        if (typeof valorCampo === 'number') {
                            return valorCampo === Number(searchQuery);
                        } else if (typeof valorCampo === 'string') {
                            return valorCampo.toLowerCase() === searchQuery.toLowerCase();
                        }
                        return false;
                    });
                });

                if (exactMatches.length > 0) {
                    // Si hay coincidencias exactas, mostrar solo la primera
                    PedidoCompraApp.renderTabla([exactMatches[0]]);
                    PedidoCompraApp.mostrarAlerta('info', 'Información', 'Se encontró una coincidencia exacta. Mostrando solo el primer resultado.');
                } else {
                    // Si no hay coincidencias exactas, mostrar todas las coincidencias parciales
                    const partialMatches = pedidos.filter(pedido => {
                        return searchFields.some(field => {
                            const valorCampo = pedido[field];
                            if (valorCampo === null || valorCampo === undefined) return false;
                            // Convertir ambos valores a string para la comparación parcial
                            return valorCampo.toString().toLowerCase().includes(searchQuery.toLowerCase());
                        });
                    });
                    PedidoCompraApp.renderTabla(partialMatches);
                    PedidoCompraApp.mostrarAlerta('info', 'Información', `Mostrando ${partialMatches.length} resultados que contienen "${searchQuery}".`);
                }
            } else {
                // Si no hay búsqueda, mostrar todos los pedidos
                PedidoCompraApp.renderTabla(pedidos);
            }
        },
    };

    // Función para convertir cadenas a números de manera segura
    function parseLong(value) {
        const parsed = parseInt(value, 10);
        return isNaN(parsed) ? null : parsed;
    }

    // Exponer globalmente para que las funciones sean accesibles desde el HTML
    window.PedidoCompraApp = PedidoCompraApp;

    // Verificar si el DOM ya está cargado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            PedidoCompraApp.initTableScroll();
            PedidoCompraApp.cargarPedidosCompra();
        });
    } else {
        // DOM ya está cargado
        PedidoCompraApp.initTableScroll();
        PedidoCompraApp.cargarPedidosCompra();
    }
});
