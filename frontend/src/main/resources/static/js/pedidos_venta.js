// pedidos_venta.js
import middleware from '/js/middleware.js'; // Asegúrate de que la ruta es correcta

// Declaración de variables de estado
let config = {};
let currentPage = 1; // Página inicial
let size = 10;        // Tamaño de página inicial
let sortBy = 'idPedidoVenta'; // Campo de ordenamiento inicial
let sortDir = 'asc';
let proveedor = '';
let cliente = '';

// Mapeo de índices de columnas a nombres de campos
const columnasAtributos = {
    2: 'idPedidoVenta',
    3: 'nOperacion',
    4: 'proforma',
    5: 'proveedor',
    6: 'incoterm',
    7: 'referenciaProveedor'
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
        // Inicializar variables de estado si están presentes en config
        currentPage = config.currentPage || 1;
        size = config.size || 10;
        sortBy = config.sortBy || 'idPedidoVenta';
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

    const PedidoVentaApp = {
        // Navegación
        goBack: () => {
            window.location.href = "/ventas";
        },
        goHome: () => {
            window.location.href= '/home';
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

        // Cargar y renderizar pedidos de venta
        cargarPedidosVenta: () => {
            const url = new URL(config.pedidosVentaEndpoint);
            url.searchParams.append('page', currentPage - 1); // Backend es zero-based
            url.searchParams.append('size', size);
            url.searchParams.append('proveedor', proveedor);
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
                PedidoVentaApp.renderTabla(data.content);
                PedidoVentaApp.actualizarPaginacion(data.number + 1, data.totalPages);
            })
                .catch(error => {
                PedidoVentaApp.mostrarAlerta('error', config.error, config.errorCargarPedidos);
                console.error('Error al cargar los pedidos de venta:', error);
            });
        },

        // Renderizar la tabla con los pedidos de venta
        renderTabla: (pedidos) => {
            console.log('Renderizando tabla con pedidos:', pedidos); // Depuración
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = ''; // Limpiar la tabla antes de renderizar

            pedidos.forEach(pedido => {
                PedidoVentaApp.renderFilaPedido(pedido);
            });
        },

        // Renderizar una fila de pedido de venta
        renderFilaPedido: (pedido) => {
            console.log('Renderizando fila para pedido:', pedido);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-pedido-venta', pedido.idPedidoVenta);

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoVentaApp.eliminarPedido(${pedido.idPedidoVenta})">🗑️</button>
                </td>
                <td>${pedido.idPedidoVenta}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaApp.marcarModificado(this)">${pedido.n_operacion || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaApp.marcarModificado(this)">${pedido.proforma || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaApp.marcarModificado(this)">${pedido.proveedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaApp.marcarModificado(this)">${pedido.incoterm || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaApp.marcarModificado(this)">${pedido.referenciaProveedor || ''}</td>
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
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="PedidoVentaApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="PedidoVentaApp.irAPaginaAnterior()">Anterior</a>`;
            }

            // Deshabilitar o habilitar "Siguiente Página" y "Última Página"
            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="PedidoVentaApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="PedidoVentaApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

        // Funciones de Navegación de Paginación
        irAPrimeraPagina: () => {
            if (currentPage > 1) {
                currentPage = 1;
                PedidoVentaApp.cargarPedidosVenta();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                PedidoVentaApp.cargarPedidosVenta();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                PedidoVentaApp.cargarPedidosVenta();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                PedidoVentaApp.cargarPedidosVenta();
            }
        },

        // Función para guardar cambios (crear y actualizar)
        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');

            console.log(`Filas modificadas encontradas: ${filasModificadas.length}`); // Depuración

            filasModificadas.forEach(fila => {
                const idPedidoVenta = fila.getAttribute('data-id-pedido-venta');
                const datos = {
                    n_operacion: fila.children[2].innerText.trim(),
                    proforma: fila.children[3].innerText.trim(),
                    proveedor: fila.children[4].innerText.trim(),
                    incoterm: fila.children[5].innerText.trim(),
                    referenciaProveedor: fila.children[6].innerText.trim()
                };

                console.log(`Guardando pedido: ${idPedidoVenta ? 'Actualización' : 'Creación'}`, datos); // Depuración

                if (!idPedidoVenta) {
                    // Crear nuevo pedido
                    middleware.post(config.pedidosVentaEndpoint, datos)
                        .then((nuevoPedido) => {
                        PedidoVentaApp.mostrarAlerta('success', config.creacionExitosa, config.pedidoCreadoExito);
                        PedidoVentaApp.cargarPedidosVenta(); // Recargar la tabla
                    })
                        .catch((error) => {
                        PedidoVentaApp.mostrarAlerta('error', 'Error', error.message || config.errorCrearPedido);
                        console.error('Error al crear pedido:', error);
                    });
                } else {
                    // Actualizar pedido existente
                    const url = config.pedidoVentaIdEndpoint.replace('{id}', idPedidoVenta);
                    middleware.put(url, datos)
                        .then(() => {
                        PedidoVentaApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito);
                        PedidoVentaApp.cargarPedidosVenta(); // Recargar la tabla
                    })
                        .catch((error) => {
                        PedidoVentaApp.mostrarAlerta('error', 'Error', error.message || config.errorGuardarCambios);
                        console.error('Error al actualizar pedido:', error);
                    });
                }
            });
        },

        // Función para eliminar un pedido
        eliminarPedido: (idPedidoVenta) => {
            console.log(`Intentando eliminar pedido: ${idPedidoVenta}`); // Depuración
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
                    const url = config.pedidoVentaIdEndpoint.replace('{id}', idPedidoVenta);
                    middleware.delete(url)
                        .then(() => {
                        PedidoVentaApp.mostrarAlerta('success', config.pedidoEliminadoExito, config.pedidoEliminadoExito);
                        PedidoVentaApp.cargarPedidosVenta(); // Recargar la tabla
                    })
                        .catch(() => {
                        PedidoVentaApp.mostrarAlerta('error', config.error, config.errorEliminarPedido);
                    });
                }
            });
        },

        // Función para crear una nueva fila editable
        crearPedidoVenta: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoVentaApp.eliminarFila(this)">🗑️</button>
                </td>
                <td></td>
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
            PedidoVentaApp.cargarPedidosVenta();
        },

        // Funciones para mostrar y ocultar la búsqueda y filtros
        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
                searchInput.value = '';
                config.search = '';
                config.searchFields = [];
                currentPage = 1;
                PedidoVentaApp.cargarPedidosVenta();

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
                PedidoVentaApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            // Actualizar variables de estado
            config.search = searchInput;
            config.searchFields = searchFields;

            // Resetear la paginación a la primera página
            currentPage = 1;

            // Cargar los pedidos con los nuevos parámetros de búsqueda
            PedidoVentaApp.cargarPedidosVenta();
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

    // Exponer globalmente para que las funciones sean accesibles desde el HTML
    window.PedidoVentaApp = PedidoVentaApp;

    // Verificar si el DOM ya está cargado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            PedidoVentaApp.initTableScroll();
            PedidoVentaApp.cargarPedidosVenta();
        });
    } else {
        // DOM ya está cargado
        PedidoVentaApp.initTableScroll();
        PedidoVentaApp.cargarPedidosVenta();
    }
});
