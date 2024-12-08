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
    2: 'pedidoCompra.idPedidoCompra',
    3: 'nOperacion',
    4: 'nContenedor',
    5: 'nombreBarco',
    6: 'viaje',
    7: 'naviera',
    8: 'puertoEmbarque',
    9: 'puertoLlegada',
    10: 'fecha_embarque',
    11: 'fecha_llegada',
    12: 'flete',
    13: 'fecha_pago_flete',
    14: 'facturaFlete',
    15: 'status'
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

    const DatosBarcoApp = {
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

        // Cargar y renderizar datos del barco
        cargarDatosBarco: () => {
            const url = new URL(config.datosBarcoEndpoint);
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
                    DatosBarcoApp.renderTabla(data.content);
                    DatosBarcoApp.actualizarPaginacion(data.number + 1, data.totalPages);
                })
                .catch(error => {
                    DatosBarcoApp.mostrarAlerta('error', 'Error', 'Error al cargar los datos del barco.');
                    console.error('Error al cargar los datos del barco:', error);
                });
        },

        // Renderizar la tabla con los datos del barco
        renderTabla: (datosBarco) => {
            console.log('Renderizando tabla con datos del barco:', datosBarco);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            tbody.innerHTML = '';

            datosBarco.forEach(dato => {
                DatosBarcoApp.renderFilaDato(dato);
            });
        },

        // Renderizar una fila de datos del barco
        renderFilaDato: (dato) => {
            console.log('Renderizando fila para dato del barco:', dato);
            const tbody = document.querySelector('tbody');
            if (!tbody) {
                console.error('No se encontró el elemento <tbody> en el DOM.');
                return;
            }
            const fila = document.createElement('tr');
            fila.setAttribute('data-id-datos-barco', dato.idDatosBarco);
            fila.setAttribute('data-id-pedido-compra', dato.idPedidoCompra);
            fila.setAttribute('data-status', dato.status);
                            // Añadir clase basada en el estado
                           if (dato.status && dato.status.toUpperCase() === 'T') {
                               fila.classList.add('status-terminado');
                           } else {
                               fila.classList.remove('status-terminado'); // Asegurar que no tenga la clase si es 'P'
                           }

            fila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="DatosBarcoApp.eliminarDatosBarco(${dato.idDatosBarco})">🗑️</button>
                </td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.idPedidoCompra || ''}</td>
                <td contenteditable="false" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.nOperacion || ''}</td>
                <td contenteditable="false" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.nContenedor || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.nombreBarco || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.viaje || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.naviera || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.puertoEmbarque || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.puertoLlegada || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.fechaEmbarqueFormatted || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.fechaLlegadaFormatted || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.flete || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.fechaPagoFleteBarcoFormatted || ''}</td>
                <td contenteditable="true" class="editable" oninput="DatosBarcoApp.marcarModificado(this)">${dato.facturaFlete || ''}</td>
            `;

            tbody.appendChild(fila);
        },

        // Actualizar los controles de paginación
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
                primeraPaginaSpan.innerHTML = `<a id="primera-pagina" href="javascript:void(0);" onclick="DatosBarcoApp.irAPrimeraPagina()">Primera Página</a>`;
                anteriorPaginaSpan.innerHTML = `<a id="anterior-pagina" href="javascript:void(0);" onclick="DatosBarcoApp.irAPaginaAnterior()">Anterior</a>`;
            }

            if (currentPage >= config.totalPages) {
                siguientePaginaSpan.innerHTML = `<span class="disabled">Siguiente</span>`;
                ultimaPaginaSpan.innerHTML = `<span class="disabled">Última Página</span>`;
            } else {
                siguientePaginaSpan.innerHTML = `<a id="siguiente-pagina" href="javascript:void(0);" onclick="DatosBarcoApp.irAPaginaSiguiente()">Siguiente</a>`;
                ultimaPaginaSpan.innerHTML = `<a id="ultima-pagina" href="javascript:void(0);" onclick="DatosBarcoApp.irAUltimaPagina()">Última Página</a>`;
            }
        },

        irAPrimeraPagina: () => {
            if (currentPage > 1) {
                currentPage = 1;
                DatosBarcoApp.cargarDatosBarco();
            }
        },

        irAPaginaAnterior: () => {
            if (currentPage > 1) {
                currentPage -= 1;
                DatosBarcoApp.cargarDatosBarco();
            }
        },

        irAPaginaSiguiente: () => {
            if (currentPage < config.totalPages) {
                currentPage += 1;
                DatosBarcoApp.cargarDatosBarco();
            }
        },

        irAUltimaPagina: () => {
            if (currentPage < config.totalPages) {
                currentPage = config.totalPages;
                DatosBarcoApp.cargarDatosBarco();
            }
        },

        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');

            const filasTerminado = Array.from(filasModificadas).filter(fila => fila.getAttribute('data-status') === 'T');

              if (filasTerminado.length > 0) {
                 const idsPedidosTerminado = [...new Set(Array.from(filasTerminado).map(fila => fila.getAttribute('data-id-pedido-compra')))];
                 DatosBarcoApp.mostrarAlerta('error', 'Error', `El pedido: ${idsPedidosTerminado.join(', ')} está terminado.No se pueden guardar los cambios`);
                  return;
              }

            const formatoFecha = /^\d{2}\/\d{2}\/\d{4}$/;

            // Función auxiliar para validar un campo numérico
            function validarCampoNumerico(fila, index, nombreCampo, esEntero = true) {
                const valor = fila.children[index].innerText.trim();
                if (valor !== '') {
                    const numero = esEntero ? parseInt(valor, 10) : parseFloat(valor);
                    if (isNaN(numero)) {
                        const idDatosBarco = fila.getAttribute('data-id-datos-barco');
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de datos',
                            text: `La celda "${nombreCampo}" en la fila ${
                                idDatosBarco
                                    ? 'con ID ' + idDatosBarco
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

            // Validar campos numéricos antes de guardar
            for (const fila of filasModificadas) {
                // idPedidoCompra (col 1, entero)
                if (!validarCampoNumerico(fila, 1, 'ID Pedido Compra')) return;
                // n_operacion (col 2, entero)
                if (!validarCampoNumerico(fila, 2, 'Nº Operación')) return;
                // flete (col 11, decimal)
                if (!validarCampoNumerico(fila, 11, 'Flete', false)) return;
            }

            // Si se supera la validación, proceder con el guardado
            filasModificadas.forEach(fila => {
                const idDatosBarco = fila.getAttribute('data-id-datos-barco');
                const idPedidoCompra = fila.children[1].innerText.trim();
                const fechaPagoFlete = fila.children[12].innerText.trim();
                const fechaEmbarque = fila.children[9].innerText.trim();
                const fechaLlegada = fila.children[10].innerText.trim();

                // Validación de fechas
                if ((fechaPagoFlete && !formatoFecha.test(fechaPagoFlete)) ||
                    (fechaEmbarque && !formatoFecha.test(fechaEmbarque)) ||
                    (fechaLlegada && !formatoFecha.test(fechaLlegada))) {
                    DatosBarcoApp.mostrarAlerta('error', 'Error', 'Formato de fecha incorrecto. Debe ser dd/MM/yyyy', 3000);
                    return;
                }

                DatosBarcoApp.validarExistenciaPedidoCompra(idPedidoCompra)
                    .then(existe => {
                        if (!existe) {
                            DatosBarcoApp.mostrarAlerta('error', 'Error', 'ID de Pedido de Compra inválido.', 3000);
                            return;
                        }

                        const datos = {
                            idPedidoCompra,
                            nOperacion: fila.children[2].innerText.trim(),
                            nContenedor: fila.children[3].innerText.trim(),
                            nombreBarco: fila.children[4].innerText.trim(),
                            viaje: fila.children[5].innerText.trim(),
                            naviera: fila.children[6].innerText.trim(),
                            puertoEmbarque: fila.children[7].innerText.trim(),
                            puertoLlegada: fila.children[8].innerText.trim(),
                            fecha_embarque: fechaEmbarque,
                            fecha_llegada: fechaLlegada,
                            flete: fila.children[11].innerText.trim(),
                            fecha_pago_flete: fechaPagoFlete,
                            facturaFlete: fila.children[13].innerText.trim()
                        };

                        if (!idDatosBarco) {
                            // Crear nuevo registro
                            middleware.post(config.datosBarcoEndpoint, datos)
                                .then(() => {
                                    DatosBarcoApp.mostrarAlerta('success', 'Éxito', 'Datos del barco creados correctamente.', 2000);
                                    DatosBarcoApp.cargarDatosBarco();
                                })
                                .catch(error => {
                                    DatosBarcoApp.mostrarAlerta('error', 'Error', 'No se pudo crear los datos del barco.');
                                });
                        } else {
                            // Actualizar registro existente
                            const url = config.datosBarcoIdEndpoint.replace('{id}', idDatosBarco);
                            middleware.put(url, datos)
                                .then(() => {
                                    DatosBarcoApp.mostrarAlerta('success', 'Éxito', 'Cambios guardados correctamente.', 2000);
                                    fila.classList.remove('modificado');
                                    DatosBarcoApp.cargarDatosBarco();
                                })
                                .catch(error => {
                                    DatosBarcoApp.mostrarAlerta('error', 'Error', 'No se pudo guardar los cambios.');
                                });
                        }
                    })
                    .catch(error => {
                        DatosBarcoApp.mostrarAlerta('error', 'Error', 'Error al validar el ID de Pedido de Compra.');
                    });
            });
        },

        validarExistenciaPedidoCompra: (idPedidoCompra) => {
            return middleware.get(`${config.pedidosCompraEndpoint}/${idPedidoCompra}/exists`)
                .then(data => data.existe)
                .catch(error => {
                    console.error('Error en la validación del ID del Pedido de Compra:', error);
                    return false;
                });
        },

        eliminarDatosBarco: (idDatosBarco) => {
            if (!idDatosBarco) {
                DatosBarcoApp.mostrarAlerta('error', 'Error', 'ID de Datos del Barco inválido.');
                return;
            }

            Swal.fire({
                title: '¿Estás seguro?',
                text: "¿Deseas eliminar estos datos del barco?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.datosBarcoIdEndpoint.replace('{id}', idDatosBarco);
                    middleware.delete(url)
                        .then(() => {
                            DatosBarcoApp.mostrarAlerta('success', 'Éxito', 'Datos del barco eliminados correctamente.', 2000);
                            DatosBarcoApp.cargarDatosBarco();
                        })
                        .catch(error => {
                            DatosBarcoApp.mostrarAlerta('error', 'Error', 'No se pudo eliminar los datos del barco.');
                        });
                }
            });
        },

        crearDatosBarco: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="DatosBarcoApp.eliminarFila(this)">🗑️</button>
                </td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="false" class="editable"></td>
                <td contenteditable="false" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
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

        eliminarFila: (boton) => {
            const fila = boton.closest('tr');
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
            DatosBarcoApp.cargarDatosBarco();
        },

        toggleSearch: () => {
            const searchInput = document.getElementById('search-input');
            searchInput.value = '';
            search = '';
            searchFields = [];
            currentPage = 1;
            DatosBarcoApp.cargarDatosBarco();
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        filtrarDetalles: () => {
            const searchInput = document.getElementById('search-input').value.trim();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked'))
                .map(input => parseInt(input.value));
            searchFields = columnasSeleccionadas.map(index => columnasAtributos[index]).filter(field => field);

            if (searchFields.length === 0) {
                DatosBarcoApp.mostrarAlerta('warning', 'Advertencia', 'Debes seleccionar al menos una columna para la búsqueda.');
                return;
            }

            search = searchInput;
            currentPage = 1;
            DatosBarcoApp.cargarDatosBarco();
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
        }
    };

    window.DatosBarcoApp = DatosBarcoApp;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            DatosBarcoApp.initTableScroll();
            DatosBarcoApp.cargarDatosBarco();
        });
    } else {
        DatosBarcoApp.initTableScroll();
        DatosBarcoApp.cargarDatosBarco();
    }
});
