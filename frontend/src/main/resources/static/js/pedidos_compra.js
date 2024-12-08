import middleware from '/js/middleware.js'; // Asegúrate de que la ruta es correcta

// Declaración de variables de estado
let config = {};
let currentPage = 1;
let size = 10;
let sortBy = 'idPedidoCompra';
let sortDir = 'desc';
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
    9: 'referenciaProveedor',
    10: 'status'
};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8702/api/config')  // Ajusta la URL si es necesario
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
                        window.location.href = "/compras";
                    }
                });
            } else {
                window.location.href = "/compras";
            }
        },

        goHome: () => {
            window.location.href = "/home";
        },

        logout: () => {
            window.location.href = "/logout";
        },

        marcarModificado: (elemento) => {
            const fila = elemento.closest('tr');
            fila.classList.add('modificado');
        },

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
            url.searchParams.append('page', currentPage - 1);
            url.searchParams.append('size', size);
            url.searchParams.append('proveedor', proveedor);
            url.searchParams.append('cliente', cliente);
            url.searchParams.append('sortBy', sortBy);
            url.searchParams.append('sortDir', sortDir);

            if (config.search && config.searchFields && config.searchFields.length > 0) {
                url.searchParams.append('search', config.search);
                config.searchFields.forEach(field => url.searchParams.append('searchFields', field));
            }

            console.log('Solicitando URL:', url.toString());

            middleware.get(url.toString())
                .then(data => {
                    console.log('Datos recibidos:', data);
                    PedidoCompraApp.renderTabla(data.content);
                    PedidoCompraApp.actualizarPaginacion(data.number + 1, data.totalPages);
                })
                .catch(error => {
                    PedidoCompraApp.mostrarAlerta('error', config.error, config.errorCargarPedidos);
                    console.error('Error al cargar los pedidos de compra:', error);
                });
        },

        renderTabla: (pedidos) => {
            console.log('Renderizando tabla con pedidos:', pedidos);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = '';

            pedidos.forEach(pedido => {
                PedidoCompraApp.renderFilaPedido(pedido);
            });
        },

        toggleStatus: (elemento) => {
                const fila = elemento.closest('tr');
                if (!fila) return;

                const idPedidoCompra = fila.getAttribute('data-id-pedido-compra');
                const estadoActual = fila.getAttribute('data-status') || 'P';
                const nuevoEstado = estadoActual === 'P' ? 'T' : 'P';

                const cuerpo = { status: nuevoEstado };

                const url = `${config.pedidosCompraEndpoint}/${idPedidoCompra}/status`;

                middleware.patch(url, cuerpo)
                    .then((pedidoActualizado) => {
                        PedidoCompraApp.mostrarAlerta('success', 'Éxito', `Estado cambiado a ${nuevoEstado === 'P' ? 'Pendiente' : 'Terminado'}.`);
                        // Actualizar la fila en el front
                        fila.setAttribute('data-status', nuevoEstado);

                        // Actualizar el icono de estado
                        const iconoEstado = fila.querySelector('.icono-estado');
                        if (iconoEstado) {
                            if (nuevoEstado === 'T') {
                                iconoEstado.classList.remove('bi-x-circle', 'pendiente');
                                iconoEstado.classList.add('bi-check-circle-fill', 'terminado');
                                iconoEstado.title = 'Estado: Terminado';
                            } else {
                                iconoEstado.classList.remove('bi-check-circle-fill', 'terminado');
                                iconoEstado.classList.add('bi-x-circle', 'pendiente');
                                iconoEstado.title = 'Estado: Pendiente';
                            }
                        }

                        // Aplicar o remover la clase de fondo azul
                        if (nuevoEstado === 'T') {
                            fila.classList.add('status-terminado');
                        } else {
                            fila.classList.remove('status-terminado');
                        }
                    })
                    .catch((error) => {
                        console.error('Error al cambiar el estado:', error);
                        PedidoCompraApp.mostrarAlerta('error', 'Error', 'No se pudo actualizar el estado del pedido.');
                    });
            },

        renderFilaPedido: (pedido) => {
            console.log('Renderizando fila para pedido:', pedido);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-pedido-compra', pedido.idPedidoCompra);
            fila.setAttribute('data-status', pedido.status);

            // Si el status es 'T', agregar la clase para el fondo azul
            if (pedido.status === 'T') {
                fila.classList.add('status-terminado');
            }

            // Icono estado: Usamos el icono de Bootstrap Icons "bi-check-circle-fill" para 'T' y "bi-x-circle" para 'P'
            const iconClass = pedido.status === 'T' ? 'terminado' : 'pendiente';
            const iconHTML = pedido.status === 'T'
                ? `<i class="bi bi-check-circle-fill icono-estado ${iconClass}" title="Estado: Terminado" onclick="PedidoCompraApp.toggleStatus(this)"></i>`
                : `<i class="bi bi-x-circle icono-estado ${iconClass}" title="Estado: Pendiente" onclick="PedidoCompraApp.toggleStatus(this)"></i>`;

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoCompraApp.eliminarPedido(${pedido.idPedidoCompra})">🗑️</button>
                    ${iconHTML}
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

        cambiarEstado: (idPedidoCompra) => {
            const fila = document.querySelector(`tr[data-id-pedido-compra="${idPedidoCompra}"]`);
            if (!fila) return;

            const estadoActual = fila.getAttribute('data-status') || 'P';
            const nuevoEstado = estadoActual === 'P' ? 'T' : 'P';

            // Preparar el cuerpo de la petición
            const cuerpo = { status: nuevoEstado };

            // Construir la URL correcta: /api/compras/pedidos_compra/{id}/status
            const url = `${config.pedidosCompraEndpoint}/${idPedidoCompra}/status`;

            // Llamar al endpoint PATCH para cambiar el estado
            middleware.patch(url, cuerpo)
                .then((pedidoActualizado) => {
                    PedidoCompraApp.mostrarAlerta('success', 'Éxito', `Estado cambiado a ${nuevoEstado === 'P' ? 'Pendiente' : 'Terminado'}.`);
                    // Actualizar la fila en el front
                    fila.setAttribute('data-status', nuevoEstado);

                    // Actualizar el icono de estado
                    const iconoEstado = fila.querySelector('.icono-estado');
                    if (iconoEstado) {
                        if (nuevoEstado === 'T') {
                            iconoEstado.classList.remove('bi-x-circle', 'pendiente');
                            iconoEstado.classList.add('bi-check-circle-fill', 'terminado');
                            iconoEstado.title = 'Estado: Terminado';
                        } else {
                            iconoEstado.classList.remove('bi-check-circle-fill', 'terminado');
                            iconoEstado.classList.add('bi-x-circle', 'pendiente');
                            iconoEstado.title = 'Estado: Pendiente';
                        }
                    }

                    // Aplicar o remover la clase de fondo azul
                    if (nuevoEstado === 'T') {
                        fila.classList.add('status-terminado');
                    } else {
                        fila.classList.remove('status-terminado');
                    }
                })
                .catch((error) => {
                    console.error('Error al cambiar el estado:', error);
                    PedidoCompraApp.mostrarAlerta('error', 'Error', 'No se pudo actualizar el estado del pedido.');
                });
        },

        actualizarPaginacion: (currentPageFromBackend, totalPagesFromBackend) => {
            currentPage = currentPageFromBackend;
            config.totalPages = totalPagesFromBackend;

            console.log(`Página actual: ${currentPage} / Total páginas: ${config.totalPages}`);

            const primeraPaginaSpan = document.getElementById('primera-pagina-span');
            const anteriorPaginaSpan = document.getElementById('anterior-span');
            const siguientePaginaSpan = document.getElementById('siguiente-span');
            const ultimaPaginaSpan = document.getElementById('ultima-pagina-span');

            if (!primeraPaginaSpan || !anteriorPaginaSpan || !siguientePaginaSpan || !ultimaPaginaSpan) {
                console.error('No se encontraron los elementos de paginación en el DOM.');
                return;
            }

            if (currentPage <= 1) {
                primeraPaginaSpan.innerHTML = `<span class="disabled">Primera Página</span>`;
                anteriorPaginaSpan.innerHTML = `<span class="disabled">Anterior</span>`;
            } else {
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAPaginaAnterior()">Anterior</a>`;
            }

            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="PedidoCompraApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

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

        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');

            console.log(`Filas modificadas encontradas: ${filasModificadas.length}`);
            const filasTerminado = Array.from(filasModificadas).filter(fila => fila.getAttribute('data-status') === 'T');

            if (filasTerminado.length > 0) {
            const idsPedidosTerminado = [...new Set(Array.from(filasTerminado).map(fila => fila.getAttribute('data-id-pedido-compra')))];
            PedidoCompraApp.mostrarAlerta('error', 'Error', `El pedido: ${idsPedidosTerminado.join(', ')} está terminado.No se pueden guardar los cambios`);
            return;
             }


            for (const fila of filasModificadas) {
                const idPedidoCompra = fila.getAttribute('data-id-pedido-compra');
                const valorOperacion = fila.children[2].innerText.trim();

                if (valorOperacion !== '' && parseLong(valorOperacion) === null) {
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
                    return;
                }
            }

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

                console.log(`Guardando pedido: ${idPedidoCompra ? 'Actualización' : 'Creación'}`, datos);

                if (!idPedidoCompra) {
                    // Crear nuevo pedido
                    middleware.post(config.pedidosCompraEndpoint, datos)
                        .then((nuevoPedido) => {
                            PedidoCompraApp.mostrarAlerta('success', config.creacionExitosa, config.pedidoCreadoExito);
                            PedidoCompraApp.cargarPedidosCompra();
                        })
                        .catch((error) => {
                            PedidoCompraApp.mostrarAlerta('error', 'Error', error.message || config.errorCrearPedido);
                            console.error('Error al crear pedido:', error);
                        });
                } else {
                    const url = config.pedidoCompraIdEndpoint.replace('{id}', idPedidoCompra);
                    middleware.put(url, datos)
                        .then(() => {
                            PedidoCompraApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito);
                            PedidoCompraApp.cargarPedidosCompra();
                        })
                        .catch((error) => {
                            PedidoCompraApp.mostrarAlerta('error', 'Error', error.message || config.errorGuardarCambios);
                            console.error('Error al actualizar pedido:', error);
                        });
                }
            });
        },

        eliminarPedido: (idPedidoCompra) => {
            console.log(`Intentando eliminar pedido: ${idPedidoCompra}`);
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
                            PedidoCompraApp.cargarPedidosCompra();
                        })
                        .catch(() => {
                            PedidoCompraApp.mostrarAlerta('error', config.error, config.errorEliminarPedido);
                        });
                }
            });
        },

        crearPedidoCompra: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="PedidoCompraApp.eliminarFila(this)">🗑️</button>
                    <i class="bi bi-x-circle icono-estado pendiente" title="Estado: Pendiente" onclick="PedidoCompraApp.toggleStatus(this)"></i>
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
            console.log('Fila nueva creada y agregada al DOM');
        },

        eliminarFila: (boton) => {
            const fila = boton.closest('tr');
            console.log('Eliminando fila:', fila);
            fila.remove();
        },

        sortTable: (campo) => {
            if (sortBy === campo) {
                sortDir = sortDir === 'asc' ? 'desc' : 'asc';
            } else {
                sortBy = campo;
                sortDir = 'asc';
            }

            console.log(`Ordenando por: ${sortBy} (${sortDir})`);
            PedidoCompraApp.cargarPedidosCompra();
        },

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

        filtrarPedidos: () => {
            const searchInput = document.getElementById('search-input').value.trim();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked')).map(input => parseInt(input.value));
            const searchFields = columnasSeleccionadas.map(index => columnasAtributos[index]).filter(field => field);

            if (searchFields.length === 0) {
                PedidoCompraApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            config.search = searchInput;
            config.searchFields = searchFields;

            currentPage = 1;

            PedidoCompraApp.cargarPedidosCompra();
        },

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
    };

    function parseLong(value) {
        const parsed = parseInt(value, 10);
        return isNaN(parsed) ? null : parsed;
    }

    window.PedidoCompraApp = PedidoCompraApp;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            PedidoCompraApp.initTableScroll();
            PedidoCompraApp.cargarPedidosCompra();
        });
    } else {
        PedidoCompraApp.initTableScroll();
        PedidoCompraApp.cargarPedidosCompra();
    }
});
