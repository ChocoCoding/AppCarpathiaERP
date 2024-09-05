import middleware from '/js/middleware.js';

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

    guardarCambios: () => {
        const filasModificadas = document.querySelectorAll('tbody tr.modificado');

        filasModificadas.forEach(fila => {
            const idLineaPedido = fila.getAttribute('data-id-linea-pedido');
            const idPedidoCompra = fila.children[1].innerText.trim();

            LineasPedidoApp.validarExistenciaPedidoCompra(idPedidoCompra)
                .then(existe => {
                    if (!existe) {
                        Swal.fire({
                            icon: 'error',
                            title: 'ID no válido',
                            text: 'El ID del Pedido de Compra no existe. Por favor, verifíquelo.',
                            toast: true,
                            position: 'top-end',
                            timer: 3000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
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
                        middleware.post('/lineas_pedidos_compra', datos)
                            .then(() => {
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Creación exitosa',
                                    text: 'Línea creada con éxito.',
                                    toast: true,
                                    position: 'top-end',
                                    timer: 500,
                                    timerProgressBar: true,
                                    showConfirmButton: false
                                }).then(() => {
                                    location.reload();
                                });
                            })
                            .catch(error => {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Error',
                                    text: 'Error al crear la línea.',
                                    toast: true,
                                    position: 'top-end',
                                    timer: 2000,
                                    timerProgressBar: true,
                                    showConfirmButton: false
                                });
                            });
                    } else {
                        // Actualizar línea existente
                        middleware.put(`/lineas_pedidos_compra/${idLineaPedido}`, datos)
                            .then(() => {
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Guardado exitoso',
                                    text: 'Cambios guardados con éxito.',
                                    toast: true,
                                    position: 'top-end',
                                    timer: 500,
                                    timerProgressBar: true,
                                    showConfirmButton: false
                                });
                                fila.classList.remove('modificado');
                            })
                            .catch(error => {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Error',
                                    text: 'Error al guardar los cambios.',
                                    toast: true,
                                    position: 'top-end',
                                    timer: 2000,
                                    timerProgressBar: true,
                                    showConfirmButton: false
                                });
                            });
                    }
                })
                .catch(error => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al validar el ID del Pedido de Compra.',
                        toast: true,
                        position: 'top-end',
                        timer: 2000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                });
        });
    },

    // Funcionalidad para el desplazamiento (scroll) con cursor "grab"
    setupTableScroll: () => {
        const tableContainer = document.querySelector('.table-container');
        let isDown = false;
        let startX;
        let scrollLeft;

        tableContainer.addEventListener('mousedown', (e) => {
            isDown = true;
            tableContainer.classList.add('active'); // Añadir clase active cuando presionas el mouse
            startX = e.pageX - tableContainer.offsetLeft;
            scrollLeft = tableContainer.scrollLeft;
        });

        tableContainer.addEventListener('mouseleave', () => {
            isDown = false;
            tableContainer.classList.remove('active'); // Quitar clase active cuando el mouse sale del contenedor
        });

        tableContainer.addEventListener('mouseup', () => {
            isDown = false;
            tableContainer.classList.remove('active'); // Quitar clase active cuando sueltas el mouse
        });

        tableContainer.addEventListener('mousemove', (e) => {
            if (!isDown) return;
            e.preventDefault();
            const x = e.pageX - tableContainer.offsetLeft;
            const walk = (x - startX) * 2; // Controla la velocidad de desplazamiento
            tableContainer.scrollLeft = scrollLeft - walk;
        });
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

    validarExistenciaPedidoCompra: (idPedidoCompra) => {
        return middleware.get(`/pedidos_compra/${idPedidoCompra}/exists`)
            .then(data => data.existe)
            .catch(error => {
                console.error('Error en la validación del ID del Pedido de Compra:', error);
                return false;
            });
    },

    eliminarLineaPedido: (idLineaPedido) => {
        if (!idLineaPedido) {
            Swal.fire({
                icon: 'error',
                title: 'ID no válido',
                text: 'No se puede eliminar esta línea porque el ID es nulo.',
                toast: true,
                position: 'top-end',
                timer: 2000,
                timerProgressBar: true,
                showConfirmButton: false
            });
            return;
        }

        Swal.fire({
            title: '¿Estás seguro?',
            text: "¿Estás seguro de que deseas eliminar esta línea? ¡Esta acción no se puede deshacer!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                middleware.delete(`/lineas_pedidos_compra/${idLineaPedido}`)
                    .then(() => {
                        Swal.fire({
                            icon: 'success',
                            title: 'Eliminado',
                            text: 'Línea eliminada con éxito.',
                            toast: true,
                            position: 'top-end',
                            timer: 300,
                            timerProgressBar: true,
                            showConfirmButton: false
                        }).then(() => {
                            location.reload(); // Recargar la página después de eliminar
                        });
                    })
                    .catch(error => {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Error al eliminar la línea.',
                            toast: true,
                            position: 'top-end',
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
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
    }
};

// Exponer globalmente
window.LineasPedidoApp = LineasPedidoApp;

// Ejecutar configuración de desplazamiento (scroll) en el contenedor
document.addEventListener('DOMContentLoaded', function () {
    LineasPedidoApp.setupTableScroll();
});
