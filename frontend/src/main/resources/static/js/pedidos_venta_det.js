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
            console.log('Configuración cargada:', config);
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
                        window.location.href = "/ventas";
                    }
                    // Si el usuario cancela, no hacer nada
                });
            } else {
                // Si no hay cambios sin guardar, navegar directamente
                window.location.href = "/ventas";
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
                        // Si el usuario confirma, navegar a Home
                        window.location.href = "/home";
                    }
                    // Si el usuario cancela, no hacer nada
                });
            } else {
                // Si no hay cambios sin guardar, navegar directamente
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
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.pesoNetoTotal ? Number(detalle.pesoNetoTotal).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.totalBultos || ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.precioTotal ? Number(detalle.precioTotal).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.promedio ? Number(detalle.promedio).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)">${detalle.valorVentaTotal ? Number(detalle.valorVentaTotal).toFixed(4) : ''}</td>
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

            if (filasModificadas.length === 0) {
                PedidoVentaDetApp.mostrarAlerta('info', 'Información', 'No hay cambios para guardar.');
                return;
            }

            let hayErrores = false;

            filasModificadas.forEach(fila => {
                const idPedidoVentaDet = fila.getAttribute('data-id-pedido-venta-det');
                const idPedidoVenta = fila.children[1].innerText.trim();
                const pesoNetoTotal = fila.children[2].innerText.trim();
                const totalBultos = fila.children[3].innerText.trim();
                const precioTotal = fila.children[4].innerText.trim();
                const promedio = fila.children[5].innerText.trim();
                const valorVentaTotal = fila.children[6].innerText.trim();
                const importador = fila.children[7].innerText.trim();

                // Validaciones
                if (!validarCampoNumerico(idPedidoVenta, 'ID Pedido Venta', true)) {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error de datos', 'El campo "ID Pedido Venta" debe ser un número entero.');
                    hayErrores = true;
                    return;
                }

                if (!validarCampoNumerico(pesoNetoTotal, 'Peso Neto Total', false)) {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error de datos', 'El campo "Peso Neto Total" debe ser un número decimal.');
                    hayErrores = true;
                    return;
                }

                if (!validarCampoNumerico(totalBultos, 'Total Bultos', true)) {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error de datos', 'El campo "Total Bultos" debe ser un número entero.');
                    hayErrores = true;
                    return;
                }

                if (!validarCampoNumerico(precioTotal, 'Precio Total', false)) {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error de datos', 'El campo "Precio Total" debe ser un número decimal.');
                    hayErrores = true;
                    return;
                }

                if (!validarCampoNumerico(promedio, 'Promedio', false)) {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error de datos', 'El campo "Promedio" debe ser un número decimal.');
                    hayErrores = true;
                    return;
                }

                if (!validarCampoNumerico(valorVentaTotal, 'Valor Venta Total', false)) {
                    PedidoVentaDetApp.mostrarAlerta('error', 'Error de datos', 'El campo "Valor Venta Total" debe ser un número decimal.');
                    hayErrores = true;
                    return;
                }

                // Si hay errores, detener el proceso
                if (hayErrores) {
                    return;
                }

                // Crear objeto de datos
                const datos = {
                    idPedidoVenta: parseLong(idPedidoVenta),
                    pesoNetoTotal: parseFloatSafe(pesoNetoTotal),
                    totalBultos: parseLong(totalBultos),
                    precioTotal: parseFloatSafe(precioTotal),
                    promedio: parseFloatSafe(promedio),
                    valorVentaTotal: parseFloatSafe(valorVentaTotal),
                    importador: importador
                };

                if (!idPedidoVentaDet) {
                    // Crear nueva línea
                    middleware.post(config.pedidosVentaDetEndpoint, datos)
                        .then(() => {
                            PedidoVentaDetApp.mostrarAlerta('success', 'Éxito', 'Detalle creado correctamente.', 2000);
                            PedidoVentaDetApp.cargarDetallesPedidoVenta(); // Recargar la tabla
                        })
                        .catch(error => {
                            if (error.message && error.message.includes('El detalle ya existe.')) {
                                PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'El detalle ya existe.');
                            } else {
                                PedidoVentaDetApp.mostrarAlerta('error', 'Error', 'No se pudo crear el detalle.');
                            }
                        });
                } else {
                    // Actualizar línea existente
                    const url = config.pedidosVentaDetIdEndpoint.replace('{id}', idPedidoVentaDet);
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
            });

            if (hayErrores) {
                // Si hubo errores, detener el guardado
                return;
            }
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
                    const url = config.pedidosVentaDetIdEndpoint.replace('{id}', idPedidoVentaDet);
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
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
                <td contenteditable="true" class="editable" oninput="PedidoVentaDetApp.marcarModificado(this)"></td>
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
            searchInput.value = '';
            search = '';
            searchFields = [];
            currentPage = 1;
            PedidoVentaDetApp.cargarDetallesPedidoVenta();
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

            if (searchFields.length === 0 && searchInput !== '') {
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

    // Función auxiliar para validar un campo numérico
    function validarCampoNumerico(valor, nombreCampo, esEntero = true) {
        if (valor === '') {
            // Campo vacío, podrías permitirlo si es opcional
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
