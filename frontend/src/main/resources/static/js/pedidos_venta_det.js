// pedidos_venta_det.js
import middleware from '/js/middleware.js';

let config = {};
let currentPage = 1; // Página inicial
let size = 10;       // Tamaño de página inicial
let sortBy = 'pedidoVenta.idPedidoVenta'; // Campo de ordenamiento inicial
let sortDir = 'asc';
let search = '';
let searchFields = [];

// Mapeo de índices de columnas a nombres de campos
const columnasAtributos = {
    2: 'pedidoVenta.idPedidoVenta',
    3: 'pesoNetoTotal',
    4: 'totalBultos',
    5: 'precioTotal',
    6: 'promedio',
    7: 'valorVentaTotal',
    8: 'importador'
};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8703/api/ventas/config')
        .then(response => {
        if (!response.ok) {
            throw new Error('Error al cargar la configuración del backend');
        }
        return response.json();
    })
        .then(data => {
        config = data;
        console.log('Configuracion cargada ',config)
        // Inicializar variables de estado si están presentes en config
        currentPage = config.currentPage || 1;
        size = config.size || 10;
        sortBy = config.sortBy || 'pedidoVenta.idPedidoVenta';
        sortDir = config.sortDir || 'asc';
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

    const PedidoVentaDetApp = {
        // Navegación
        goBack: () => {
            window.history.back();
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

        // Cargar y renderizar detalles de pedido de venta
        cargarDetallesPedidoVenta: () => {
            const url = new URL(config.pedidosVentaDetEndpoint);
            url.searchParams.append('page', currentPage - 1); // Backend es zero-based
            url.searchParams.append('size', size);
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
                PedidoVentaDetApp.renderTabla(data.content);
                PedidoVentaDetApp.actualizarPaginacion(data.number + 1, data.totalPages);
            })
                .catch(error => {
                PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'Error al cargar los detalles de pedido de venta.');
                console.error('Error al cargar los detalles de pedido de venta:', error);
            });
        },

        // Renderizar la tabla con los detalles de pedido de venta
        renderTabla: (detalles) => {
            console.log('Renderizando tabla con detalles:', detalles); // Depuración
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = ''; // Limpiar la tabla antes de renderizar

            detalles.forEach(detalle => {
                PedidoVentaDetApp.renderFilaDetalle(detalle);
            });
        },

        // Renderizar una fila de detalle de pedido de venta
        renderFilaDetalle: (detalle) => {
            console.log('Renderizando fila para detalle:', detalle);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-pedido-venta-det', detalle.idPedidoVentaDet);

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoVentaDetApp.eliminarPedidoVentaDet(${detalle.idPedidoVentaDet})">🗑️</button>
                </td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.idPedidoVenta || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.pesoNetoTotal || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.totalBultos || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.precioTotal || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.promedio || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.valorVentaTotal || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.importador || ''}</td>
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
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="PedidoVentaDetApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="PedidoVentaDetApp.irAPaginaAnterior()">Anterior</a>`;
            }

            // Deshabilitar o habilitar "Siguiente Página" y "Última Página"
            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="PedidoVentaDetApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="PedidoVentaDetApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

        // Funciones de Navegación de Paginación
        irAPrimeraPagina: () => {
            if (currentPage > 1) {
                currentPage = 1;
                PedidoVentaDetApp.cargarDetallesPedidoVenta();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                PedidoVentaDetApp.cargarDetallesPedidoVenta();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                PedidoVentaDetApp.cargarDetallesPedidoVenta();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                PedidoVentaDetApp.cargarDetallesPedidoVenta();
            }
        },

        // Función para guardar cambios (crear y actualizar)
        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');
            const formatoFecha = /^\d{2}\/\d{2}\/\d{4}$/;

            filasModificadas.forEach(fila => {
                const idPedidoVentaDet = fila.getAttribute('data-id-pedido-venta-det');
                const idPedidoVenta = fila.children[1].innerText.trim();



                PedidoVentaDetApp.validarExistenciaPedidoVenta(idPedidoVenta)
                    .then(existe => {
                    if (!existe) {
                        PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'ID de Pedido de Venta inválido.', 3000);
                        return;
                    }

                    const datos = {
                        idPedidoVenta: idPedidoVenta,
                        pesoNetoTotal: parseFloatSafe(fila.children[2].innerText.trim()),
                        totalBultos: parseLong(fila.children[3].innerText.trim()),
                        precioTotal: parseFloatSafe(fila.children[4].innerText.trim()),
                        promedio: parseFloatSafe(fila.children[5].innerText.trim()),
                        valorVentaTotal: parseFloatSafe(fila.children[6].innerText.trim()),
                        importador: fila.children[7].innerText.trim()
                    };

                    if (!idPedidoVentaDet) {
                        middleware.post(config.pedidosVentaDetEndpoint, datos)
                            .then(() => {
                            PedidoVentaDetApp.mostrarAlerta('success', 'Éxito', 'Detalle creado correctamente.', 2000);
                            PedidoVentaDetApp.cargarDetallesPedidoVenta(); // Recargar la tabla
                        })
                            .catch(error => {
                            if (error.message.includes('El detalle ya existe.')) {
                                PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'El detalle ya existe.');
                            } else {
                                PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'No se pudo crear el detalle.');
                            }
                        });
                    } else {
                        const url = config.pedidoVentaDetIdEndpoint.replace('{id}', idPedidoVentaDet);
                        middleware.put(url, datos)
                            .then(() => {
                            PedidoVentaDetApp.mostrarAlerta('success', 'Éxito', 'Cambios guardados correctamente.', 2000);
                            fila.classList.remove('modificado');
                            PedidoVentaDetApp.cargarDetallesPedidoVenta(); // Recargar la tabla
                        })
                            .catch(error => {
                            PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'No se pudo guardar los cambios.');
                        });
                    }
                })
                    .catch(error => {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'Error al validar el ID de Pedido de Venta.');
                });
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

        // Función para eliminar un detalle de pedido de venta
        eliminarPedidoVentaDet: (idPedidoVentaDet) => {
            if (!idPedidoVentaDet) {
                PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'ID de Detalle inválido.');
                return;
            }

            Swal.fire({
                title: '¿Estás seguro?',
                text: "¿Deseas eliminar este detalle?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.pedidoVentaDetIdEndpoint.replace('{id}', idPedidoVentaDet);
                    middleware.delete(url)
                        .then(() => {
                        PedidoVentaDetApp.mostrarAlerta('success', 'Éxito', 'Detalle eliminado correctamente.', 2000);
                        PedidoVentaDetApp.cargarDetallesPedidoVenta(); // Recargar la tabla
                    })
                        .catch(error => {
                        PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'No se pudo eliminar el detalle.');
                    });
                }
            });
        },

        // Función para crear una nueva fila editable
        crearPedidoVentaDet: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoVentaDetApp.eliminarFila(this)">🗑️</button>
                </td>
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
        },

        // Función para eliminar una fila antes de guardarla
        eliminarFila: (boton) => {
            const fila = boton.closest('tr');
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

            console.log(`Ordenando por: ${sortBy} (${sortDir})`);
            PedidoVentaDetApp.cargarDetallesPedidoVenta();
        },

        // Funciones para mostrar y ocultar la búsqueda y filtros
        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
            if (searchInput.style.display === 'none' || searchInput.style.display === '') {
                searchInput.style.display = 'block';
                searchInput.classList.add('expanded');
                searchInput.focus();
            } else {
                searchInput.style.display = 'none';
                searchInput.classList.remove('expanded');
                searchInput.value = '';
                search = '';
                searchFields = [];
                currentPage = 1;
                PedidoVentaDetApp.cargarDetallesPedidoVenta();
            }
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        // Función para filtrar detalles (servidor)
        filtrarDetalles: () => {
            const searchInput = document.getElementById('search-input').value.trim();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked'))
                .map(input => parseInt(input.value));
            searchFields = columnasSeleccionadas.map(index => columnasAtributos[index]).filter(field => field); // Obtener nombres de campos

            if (searchFields.length === 0) {
                PedidoVentaDetApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            // Actualizar variables de estado
            search = searchInput;

            // Resetear la paginación a la primera página
            currentPage = 1;

            // Cargar los detalles con los nuevos parámetros de búsqueda
            PedidoVentaDetApp.cargarDetallesPedidoVenta();
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
    window.PedidoVentaDetApp = PedidoVentaDetApp;

    // Verificar si el DOM ya está cargado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            PedidoVentaDetApp.initTableScroll();
            PedidoVentaDetApp.cargarDetallesPedidoVenta();
        });
    } else {
        // DOM ya está cargado
        PedidoVentaDetApp.initTableScroll();
        PedidoVentaDetApp.cargarDetallesPedidoVenta();
    }
});
