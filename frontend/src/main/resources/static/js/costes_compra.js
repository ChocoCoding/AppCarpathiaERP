import middleware from '/js/middleware.js';

let config = {};
let currentPage = 1; // Página inicial
let size = 10;       // Tamaño de página inicial
let sortBy = 'pedidoCompra.idPedidoCompra'; // Campo de ordenamiento inicial
let sortDir = 'desc';
let search = '';
let searchFields = [];

// Mapeo de índices de columnas a nombres de campos
const columnasAtributos = {
    1: 'pedidoCompra.idPedidoCompra',
    2: 'nOperacion',
    3: 'nContenedor',
    4: 'arancel',
    5: 'sanidad',
    6: 'plastico',
    7: 'carga',
    8: 'inland',
    9: 'muellaje',
    10: 'pif',
    11: 'despacho',
    12: 'conexiones',
    13: 'iva',
    14: 'dec_iva',
    15: 'tasa_sanitaria',
    16: 'suma_costes',
    17: 'gasto_total',
    18: 'status'
};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8702/api/config')
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
            sortBy = config.sortBy || 'pedidoCompra.idPedidoCompra';
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

    const CosteCompraApp = {
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

        // Cargar y renderizar costes de compra
        cargarCostesCompra: () => {
            const url = new URL(config.costesPedidosCompraEndpoint);
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
                    CosteCompraApp.renderTabla(data.content);
                    CosteCompraApp.actualizarPaginacion(data.number + 1, data.totalPages);
                })
                .catch(error => {
                    CosteCompraApp.mostrarAlerta('error', 'Error', 'Error al cargar los costes de compra.');
                    console.error('Error al cargar los costes de compra:', error);
                });
        },

        // Renderizar la tabla con los costes de compra
        renderTabla: (costes) => {
            console.log('Renderizando tabla con costes de compra:', costes); // Depuración
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = ''; // Limpiar la tabla antes de renderizar

            costes.forEach(coste => {
                CosteCompraApp.renderFilaCoste(coste);
            });
        },

        // Renderizar una fila de coste de compra
        renderFilaCoste: (coste) => {
            console.log('Renderizando fila para coste de compra:', coste);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-coste-compra', coste.idCosteCompra);
            fila.setAttribute('data-id-pedido-compra', coste.idPedidoCompra);
            fila.setAttribute('data-status', coste.status);
                                        // Añadir clase basada en el estado
                                       if (coste.status && coste.status.toUpperCase() === 'T') {
                                           fila.classList.add('status-terminado');
                                       } else {
                                           fila.classList.remove('status-terminado'); // Asegurar que no tenga la clase si es 'P'
                                       }

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="CosteCompraApp.eliminarCosteCompra(${coste.idCosteCompra})">🗑️</button>
                </td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.idPedidoCompra || ''}</td>
                <td contenteditable="false" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.n_operacion || ''}</td>
                <td contenteditable="false" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.n_contenedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.arancel ? Number(coste.arancel).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.sanidad ? Number(coste.sanidad).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.plastico ? Number(coste.plastico).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.carga ? Number(coste.carga).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.inland ? Number(coste.inland).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.muellaje ? Number(coste.muellaje).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.pif ? Number(coste.pif).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.despacho ? Number(coste.despacho).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.conexiones ? Number(coste.conexiones).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.iva ? Number(coste.iva).toFixed(4) : ''}</td>
                <td contenteditable="true" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.dec_iva || ''}</td>
                <td contenteditable="false" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.tasa_sanitaria ? Number(coste.tasa_sanitaria).toFixed(4) : ''}</td>
                <td contenteditable="false" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.suma_costes ? Number(coste.suma_costes).toFixed(4) : ''}</td>
                <td contenteditable="false" class="editable" oninput="CosteCompraApp.marcarModificado(this)">${coste.gasto_total ? Number(coste.gasto_total).toFixed(4) : ''}</td>
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
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="CosteCompraApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="CosteCompraApp.irAPaginaAnterior()">Anterior</a>`;
            }

            // Deshabilitar o habilitar "Siguiente Página" y "Última Página"
            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="CosteCompraApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="CosteCompraApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

        // Funciones de Navegación de Paginación
        irAPrimeraPagina: () => {
            if (currentPage > 1) {
                currentPage = 1;
                CosteCompraApp.cargarCostesCompra();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                CosteCompraApp.cargarCostesCompra();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                CosteCompraApp.cargarCostesCompra();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                CosteCompraApp.cargarCostesCompra();
            }
        },

        // Función para guardar cambios (crear y actualizar)
        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');
            const formatoFecha = /^\d{2}\/\d{2}\/\d{4}$/;

            const filasTerminado = Array.from(filasModificadas).filter(fila => fila.getAttribute('data-status') === 'T');

                          if (filasTerminado.length > 0) {
                             const idsPedidosTerminado = [...new Set(Array.from(filasTerminado).map(fila => fila.getAttribute('data-id-pedido-compra')))];
                             CosteCompraApp.mostrarAlerta('error', 'Error', `El pedido: ${idsPedidosTerminado.join(', ')} está terminado.No se pueden guardar los cambios`);
                              return;
                          }

            // Función auxiliar para validar un campo numérico
            function validarCampoNumerico(fila, index, nombreCampo, esEntero = true) {
                const valor = fila.children[index].innerText.trim();
                if (valor !== '') {
                    const numero = esEntero ? parseInt(valor, 10) : parseFloat(valor);
                    if (isNaN(numero)) {
                        const idCosteCompra = fila.getAttribute('data-id-coste-compra');
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de datos',
                            text: `La celda "${nombreCampo}" en la fila ${
                                idCosteCompra
                                    ? 'con ID ' + idCosteCompra
                                    : 'nueva'
                            } requiere un valor numérico.`,
                            toast: true,
                            position: 'top-end',
                            timer: 3000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                        return false;
                    }
                }
                return true;
            }

            // Validar todos los campos numéricos de todas las filas modificadas
            for (const fila of filasModificadas) {
                // idPedidoCompra (col 1, entero)
                if (!validarCampoNumerico(fila, 1, 'ID Pedido Compra')) return;
                // n_operacion (col 2, entero)
                if (!validarCampoNumerico(fila, 2, 'Nº Operación')) return;
                // arancel (col 4, decimal)
                if (!validarCampoNumerico(fila, 4, 'Arancel', false)) return;
                // sanidad (col 5, decimal)
                if (!validarCampoNumerico(fila, 5, 'Sanidad', false)) return;
                // plastico (col 6, decimal)
                if (!validarCampoNumerico(fila, 6, 'Plástico', false)) return;
                // carga (col 7, decimal)
                if (!validarCampoNumerico(fila, 7, 'Carga', false)) return;
                // inland (col 8, decimal)
                if (!validarCampoNumerico(fila, 8, 'Inland', false)) return;
                // muellaje (col 9, decimal)
                if (!validarCampoNumerico(fila, 9, 'Muellaje', false)) return;
                // pif (col 10, decimal)
                if (!validarCampoNumerico(fila, 10, 'PIF', false)) return;
                // despacho (col 11, decimal)
                if (!validarCampoNumerico(fila, 11, 'Despacho', false)) return;
                // conexiones (col 12, decimal)
                if (!validarCampoNumerico(fila, 12, 'Conexiones', false)) return;
                // iva (col 13, decimal)
                if (!validarCampoNumerico(fila, 13, 'IVA', false)) return;
                // tasa_sanitaria (col 15, decimal)
                if (!validarCampoNumerico(fila, 15, 'Tasa Sanitaria', false)) return;
                // suma_costes (col 16, decimal)
                if (!validarCampoNumerico(fila, 16, 'Suma Costes', false)) return;
                // gasto_total (col 17, decimal)
                if (!validarCampoNumerico(fila, 17, 'Gasto Total', false)) return;
            }

            // Si todas las validaciones pasan, proceder con el guardado
            filasModificadas.forEach(fila => {
                const idCosteCompra = fila.getAttribute('data-id-coste-compra');
                const idPedidoCompra = fila.children[1].innerText.trim();
                // No se utilizan fechas en CostesDTO, por lo que omitimos la validación de fechas

                CosteCompraApp.validarExistenciaPedidoCompra(idPedidoCompra)
                    .then(existe => {
                        if (!existe) {
                            CosteCompraApp.mostrarAlerta('error', 'Error', 'ID de Pedido de Compra inválido.', 3000);
                            return;
                        }

                        const datos = {
                            idPedidoCompra,
                            n_operacion: fila.children[2].innerText.trim(),
                            n_contenedor: fila.children[3].innerText.trim(),
                            arancel: fila.children[4].innerText.trim(),
                            sanidad: fila.children[5].innerText.trim(),
                            plastico: fila.children[6].innerText.trim(),
                            carga: fila.children[7].innerText.trim(),
                            inland: fila.children[8].innerText.trim(),
                            muellaje: fila.children[9].innerText.trim(),
                            pif: fila.children[10].innerText.trim(),
                            despacho: fila.children[11].innerText.trim(),
                            conexiones: fila.children[12].innerText.trim(),
                            iva: fila.children[13].innerText.trim(),
                            dec_iva: fila.children[14].innerText.trim(),
                            tasa_sanitaria: fila.children[15].innerText.trim(),
                            suma_costes: fila.children[16].innerText.trim(),
                            gasto_total: fila.children[17].innerText.trim()
                        };

                        if (!idCosteCompra) {
                            // Crear nuevo registro
                            middleware.post(config.costesPedidosCompraEndpoint, datos)
                                .then(() => {
                                    CosteCompraApp.mostrarAlerta('success', 'Éxito', 'Coste creado correctamente.', 2000);
                                    CosteCompraApp.cargarCostesCompra(); // Recargar la tabla
                                })
                                .catch(error => {
                                    if (error.message && error.message.includes('El coste ya existe.')) {
                                        CosteCompraApp.mostrarAlerta('error', 'Error', 'El coste ya existe.');
                                    } else {
                                        CosteCompraApp.mostrarAlerta('error', 'Error', 'No se pudo crear el coste.');
                                    }
                                });
                        } else {
                            // Actualizar registro existente
                            const url = config.costesPedidosCompraIdEndpoint.replace('{id}', idCosteCompra);
                            middleware.put(url, datos)
                                .then(() => {
                                    CosteCompraApp.mostrarAlerta('success', 'Éxito', 'Cambios guardados correctamente.', 2000);
                                    fila.classList.remove('modificado');
                                    CosteCompraApp.cargarCostesCompra(); // Recargar la tabla
                                })
                                .catch(error => {
                                    CosteCompraApp.mostrarAlerta('error', 'Error', 'No se pudo guardar los cambios.');
                                });
                        }
                    })
                    .catch(error => {
                        CosteCompraApp.mostrarAlerta('error', 'Error', 'Error al validar el ID de Pedido de Compra.');
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

        // Función para eliminar un coste de compra
        eliminarCosteCompra: (idCosteCompra) => {
            if (!idCosteCompra) {
                CosteCompraApp.mostrarAlerta('error', 'Error', 'ID de Coste inválido.');
                return;
            }

            Swal.fire({
                title: '¿Estás seguro?',
                text: "¿Deseas eliminar este coste?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.costesPedidosCompraIdEndpoint.replace('{id}', idCosteCompra);
                    middleware.delete(url)
                        .then(() => {
                            CosteCompraApp.mostrarAlerta('success', 'Éxito', 'Coste eliminado correctamente.', 2000);
                            CosteCompraApp.cargarCostesCompra(); // Recargar la tabla
                        })
                        .catch(error => {
                            CosteCompraApp.mostrarAlerta('error', 'Error', 'No se pudo eliminar el coste.');
                        });
                }
            });
        },

        // Función para crear una nueva fila editable
        crearCosteCompra: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="CosteCompraApp.eliminarFila(this)">🗑️</button>
                </td>
                <td contenteditable="true" class="editable"></td> <!-- ID Pedido Compra -->
                <td contenteditable="false" class="editable"></td> <!-- Nº Operación -->
                <td contenteditable="false" class="editable"></td> <!-- Nº Contenedor -->
                <td contenteditable="true" class="editable"></td> <!-- Arancel -->
                <td contenteditable="true" class="editable"></td> <!-- Sanidad -->
                <td contenteditable="true" class="editable"></td> <!-- Plástico -->
                <td contenteditable="true" class="editable"></td> <!-- Carga -->
                <td contenteditable="true" class="editable"></td> <!-- Inland -->
                <td contenteditable="true" class="editable"></td> <!-- Muellaje -->
                <td contenteditable="true" class="editable"></td> <!-- PIF -->
                <td contenteditable="true" class="editable"></td> <!-- Despacho -->
                <td contenteditable="true" class="editable"></td> <!-- Conexiones -->
                <td contenteditable="true" class="editable"></td> <!-- IVA -->
                <td contenteditable="true" class="editable"></td> <!-- Dec.Iva -->
                <td contenteditable="true" class="editable"></td> <!-- Tasa Sanitaria -->
                <td contenteditable="false" class="editable"></td> <!-- Suma Costes -->
                <td contenteditable="false" class="editable"></td> <!-- Gasto Total -->
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
            CosteCompraApp.cargarCostesCompra();
        },

        // Funciones para mostrar y ocultar la búsqueda y filtros
        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
            searchInput.value = '';
            search = '';
            searchFields = [];
            currentPage = 1;
            CosteCompraApp.cargarCostesCompra();
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        // Función para filtrar costes (servidor)
        filtrarCostes: () => {
            const searchInput = document.getElementById('search-input').value.trim();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked'))
                .map(input => parseInt(input.value));
            searchFields = columnasSeleccionadas.map(index => columnasAtributos[index]).filter(field => field); // Obtener nombres de campos

            if (searchFields.length === 0) {
                CosteCompraApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            // Actualizar variables de estado
            search = searchInput;

            // Resetear la paginación a la primera página
            currentPage = 1;

            // Cargar los costes con los nuevos parámetros de búsqueda
            CosteCompraApp.cargarCostesCompra();
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
    window.CosteCompraApp = CosteCompraApp;

    // Verificar si el DOM ya está cargado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            CosteCompraApp.initTableScroll();
            CosteCompraApp.cargarCostesCompra();
        });
    } else {
        // DOM ya está cargado
        CosteCompraApp.initTableScroll();
        CosteCompraApp.cargarCostesCompra();
    }
});
