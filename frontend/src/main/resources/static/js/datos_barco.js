import middleware from '/js/middleware.js';

let config = {};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8702/api/config')
        .then(response => response.json())
        .then(data => {
            config = data;
            return config;
        })
        .catch(error => {
            console.error('Error al cargar la configuración:', error);
        });
}

cargarConfiguraciones().then(() => {
    const DatosBarcoApp = {
        goBack: () => {
            window.history.back();
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

        guardarCambios: () => {
            const filasModificadas = document.querySelectorAll('tbody tr.modificado');
            const formatoFecha = /^\d{2}\/\d{2}\/\d{4}$/;

            filasModificadas.forEach(fila => {
                const idDatosBarco = fila.getAttribute('data-id-datos-barco');
                const idPedidoCompra = fila.children[1].innerText.trim();
                const fechaPagoFlete = fila.children[11].innerText.trim();
                const fechaEmbarque = fila.children[8].innerText.trim();
                const fechaLlegada = fila.children[9].innerText.trim();

                // Validación de fechas
                if ((fechaPagoFlete && !formatoFecha.test(fechaPagoFlete)) ||
                    (fechaEmbarque && !formatoFecha.test(fechaEmbarque)) ||
                    (fechaLlegada && !formatoFecha.test(fechaLlegada))) {
                    DatosBarcoApp.mostrarAlerta('error', config.errorFormatoFechaIncorrecto, config.errorFechaFormato, 3000);
                    return;
                }

                DatosBarcoApp.validarExistenciaPedidoCompra(idPedidoCompra)
                    .then(existe => {
                        if (!existe) {
                            DatosBarcoApp.mostrarAlerta('error', config.error, config.errorIdPedidoInvalido, 3000);
                            return;
                        }

                        const datos = {
                            idPedidoCompra,
                            n_operacion: fila.children[2].innerText.trim(),
                            nombreBarco: fila.children[3].innerText.trim(),
                            viaje: fila.children[4].innerText.trim(),
                            naviera: fila.children[5].innerText.trim(),
                            puertoEmbarque: fila.children[6].innerText.trim(),
                            puertoLlegada: fila.children[7].innerText.trim(),
                            fecha_embarque: fechaEmbarque,
                            fecha_llegada: fechaLlegada,
                            flete: fila.children[10].innerText.trim(),
                            fecha_pago_flete: fechaPagoFlete,
                            facturaFlete: fila.children[12].innerText.trim()
                        };

                        if (!idDatosBarco) {
                            middleware.post(config.datosBarcoEndpoint, datos)
                                .then(() => {
                                    DatosBarcoApp.mostrarAlerta('success', config.creacionExitosa, config.datosBarcoCreadoExito, 2000);
                                    location.reload();
                                })
                                .catch(error => {
                                    DatosBarcoApp.mostrarAlerta('error', config.error, config.errorCrearDatosBarco);
                                });
                        } else {
                            const url = config.datosBarcoIdEndpoint.replace('{id}', idDatosBarco);
                            middleware.put(url, datos)
                                .then(() => {
                                    DatosBarcoApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito, 2000);
                                    fila.classList.remove('modificado');
                                    location.reload();
                                })
                                .catch(error => {
                                    DatosBarcoApp.mostrarAlerta('error', config.error, config.errorGuardarCambios);
                                });
                        }
                    })
                    .catch(error => {
                        DatosBarcoApp.mostrarAlerta('error', config.error, config.errorIdPedidoInvalido);
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
                DatosBarcoApp.mostrarAlerta('error', config.error, config.errorIdInvalido);
                return;
            }

            Swal.fire({
                title: config.confirmarEliminarDatosBarco,
                text: config.confirmarEliminarDatosBarcoTexto,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: config.confirmarEliminar,
                cancelButtonText: config.cancelar
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.datosBarcoIdEndpoint.replace('{id}', idDatosBarco);
                    middleware.delete(url)
                        .then(() => {
                            DatosBarcoApp.mostrarAlerta('success', config.eliminado, config.datosBarcoEliminadoExito, 2000);
                            location.reload();
                        })
                        .catch(error => {
                            DatosBarcoApp.mostrarAlerta('error', config.error, config.errorEliminarDatosBarco);
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
                <td contenteditable="true" class="editable"></td>
            `;

            tbody.insertBefore(nuevaFila, tbody.firstChild);
            nuevaFila.classList.add('modificado', 'new-row');
        },

        eliminarFila: (boton) => {
            const fila = boton.closest('tr');
            fila.remove();
        },

        sortTable: (columnIndex) => {
            const table = document.querySelector("tbody");
            const rows = Array.from(table.querySelectorAll("tr"));
            const isAscending = table.getAttribute("data-sort-direction") === "asc";
            const newDirection = isAscending ? "desc" : "asc";

            rows.sort((a, b) => {
                const aText = a.children[columnIndex].innerText.trim();
                const bText = b.children[columnIndex].innerText.trim();

                return isAscending
                    ? aText.localeCompare(bText, undefined, { numeric: true })
                    : bText.localeCompare(aText, undefined, { numeric: true });
            });

            rows.forEach(row => table.appendChild(row));

            table.setAttribute("data-sort-direction", newDirection);
            document.querySelectorAll(".sortable").forEach(th => th.classList.remove("asc", "desc"));
            document.querySelector(`.sortable:nth-child(${columnIndex + 1})`).classList.add(newDirection);
        },

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
                DatosBarcoApp.filtrarDetalles();
            }
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        filtrarDetalles: () => {
            const searchInput = document.getElementById('search-input').value.toLowerCase();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked')).map(input => parseInt(input.value));
            const filas = document.querySelectorAll('tbody tr');

            filas.forEach(fila => {
                const columnas = fila.querySelectorAll('td');
                let match = false;

                columnas.forEach((columna, index) => {
                    const columnaTexto = columna.innerText.toLowerCase().trim();
                    if ((columnasSeleccionadas.length === 0 || columnasSeleccionadas.includes(index + 1)) && columnaTexto.includes(searchInput)) {
                        match = true;
                    }
                });

                fila.style.display = match ? '' : 'none';
            });
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

    // Inicializar la tabla con scroll
    document.addEventListener('DOMContentLoaded', DatosBarcoApp.initTableScroll());

    // Exponer globalmente
    window.DatosBarcoApp = DatosBarcoApp;
});
