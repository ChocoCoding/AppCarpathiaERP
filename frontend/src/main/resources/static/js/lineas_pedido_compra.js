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
    const LineasPedidoApp = {
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

            filasModificadas.forEach(fila => {
                const idLineaPedido = fila.getAttribute('data-id-linea-pedido');
                const idPedidoCompra = fila.children[1].innerText.trim();

                LineasPedidoApp.validarExistenciaPedidoCompra(idPedidoCompra)
                    .then(existe => {
                        if (!existe) {
                            LineasPedidoApp.mostrarAlerta('error', config.error, config.errorIdPedidoInvalido, 3000);
                            return;
                        }

                        const datos = {
                            idPedidoCompra: idPedidoCompra,
                            n_linea: fila.children[2].innerText.trim(),
                            n_operacion: fila.children[3].innerText.trim(),
                            proveedor: fila.children[4].innerText.trim(),
                            cliente: fila.children[5].innerText.trim(),
                            n_contenedor: fila.children[6].innerText.trim(),
                            producto: fila.children[7].innerText.trim(),
                            talla: fila.children[8].innerText.trim(),
                            p_neto: fila.children[9].innerText.trim(),
                            unidad: fila.children[10].innerText.trim(),
                            bultos: fila.children[11].innerText.trim(),
                            precio: fila.children[12].innerText.trim(),
                            valor_compra: fila.querySelector('.valor-compra-total').innerText.trim(),
                            moneda: fila.children[14].innerText.trim(),
                            paisOrigen: fila.children[15].innerText.trim()
                        };

                        if (!idLineaPedido) {
                            // Crear nueva línea
                            middleware.post(config.lineasPedidosCompraEndpoint, datos)
                                .then(() => {
                                    LineasPedidoApp.mostrarAlerta('success', config.creacionExitosa, config.lineaCreadaExito, 500);
                                    setTimeout(() => location.reload(), 500);
                                })
                                .catch(() => {
                                    LineasPedidoApp.mostrarAlerta('error', config.error, config.errorCrearLinea);
                                });
                        } else {
                            // Actualizar línea existente
                            const endpoint = config.lineasPedidosCompraIdEndpoint.replace('{id}', idLineaPedido);
                            middleware.put(endpoint, datos)
                                .then(() => {
                                    LineasPedidoApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito, 500);
                                    fila.classList.remove('modificado');
                                })
                                .catch(() => {
                                    LineasPedidoApp.mostrarAlerta('error', config.error, config.errorGuardarCambios);
                                });
                        }
                    })
                    .catch(() => {
                        LineasPedidoApp.mostrarAlerta('error', config.error, config.errorIdPedidoInvalido);
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

        eliminarLineaPedido: (idLineaPedido) => {
            if (!idLineaPedido) {
                LineasPedidoApp.mostrarAlerta('error', config.error, config.errorIdInvalido);
                return;
            }

            Swal.fire({
                title: config.confirmarEliminarLinea,
                text: config.confirmarEliminarLineaTexto,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: config.confirmarEliminar,
                cancelButtonText: config.cancelar
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.lineasPedidosCompraIdEndpoint.replace('{id}', idLineaPedido);
                    middleware.delete(url)
                        .then(() => {
                            LineasPedidoApp.mostrarAlerta('success', config.eliminado, config.lineaEliminadaExito, 300);
                            setTimeout(() => location.reload(), 300);
                        })
                        .catch(() => {
                            LineasPedidoApp.mostrarAlerta('error', config.error, config.errorEliminarLinea);
                        });
                }
            });
        },

        crearLineaPedido: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="LineasPedidoApp.eliminarFila(this)">🗑️</button>
                </td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable peso-neto" oninput="LineasPedidoApp.calcularValorCompra(this)"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable"></td>
                <td contenteditable="true" class="editable precio" oninput="LineasPedidoApp.calcularValorCompra(this)"></td>
                <td contenteditable="true" class="editable valor-compra-total"></td>
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

            const valorCompraTotal = (pesoNeto * precio).toFixed(6);

            fila.querySelector('.valor-compra-total').innerText = valorCompraTotal;

            fila.classList.add('modificado');
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
                    ? aText.localeCompare(bText, undefined, {numeric: true})
                    : bText.localeCompare(aText, undefined, {numeric: true});
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
                LineasPedidoApp.filtrarLineas();
            }
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

        filtrarLineas: () => {
            const searchInput = document.getElementById('search-input').value.toLowerCase();
            const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked')).map(input => parseInt(input.value));
            const filas = document.querySelectorAll('tbody tr');

            filas.forEach(fila => {
                const columnas = fila.querySelectorAll('td');
                let match = false;

                columnas.forEach((columna, index) => {
                    if ((columnasSeleccionadas.length === 0 || columnasSeleccionadas.includes(index + 1)) &&
                        columna.innerText.toLowerCase().includes(searchInput) && index !== 0) {
                        match = true;
                    }
                });

                if (match) {
                    fila.style.display = '';
                } else {
                    fila.style.display = 'none';
                }
            });
        },

        setupTableScroll: () => {
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

    // Exponer globalmente
    window.LineasPedidoApp = LineasPedidoApp;

    // Ejecutar configuración de desplazamiento (scroll) en el contenedor
    document.addEventListener('DOMContentLoaded', function () {
        LineasPedidoApp.setupTableScroll();
    });
});
