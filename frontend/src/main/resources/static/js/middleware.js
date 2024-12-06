// middleware.js
const middleware = {
    get: async (url) => {
        console.log(`Realizando GET a: ${url}`); // Depuración
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Error en GET: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error en middleware.get:', error);
            throw error;
        }
    },
    // Función POST modificada
    post: async (url, data) => {
        console.log(`Realizando POST a: ${url} con datos:`, data); // Depuración
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                let errorMessage = `Error en POST: ${response.status}`;
                try {
                    const errorData = await response.json();
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    console.error('Error al parsear el cuerpo de la respuesta:', e);
                }
                throw new Error(errorMessage);
            }
            const responseData = await response.json();
            return responseData;
        } catch (error) {
            console.error('Error en middleware.post:', error);
            throw error;
        }
    },

    // Función PUT modificada
    put: async (url, data) => {
        console.log(`Realizando PUT a: ${url} con datos:`, data); // Depuración
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                let errorMessage = `Error en PUT: ${response.status}`;
                try {
                    const errorData = await response.json();
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    console.error('Error al parsear el cuerpo de la respuesta:', e);
                }
                throw new Error(errorMessage);
            }
            const responseData = await response.json();
            return responseData;
        } catch (error) {
            console.error('Error en middleware.put:', error);
            throw error;
        }
    },

    delete: async (url) => {
        console.log(`Realizando DELETE a: ${url}`); // Depuración
        try {
            const response = await fetch(url, {
                method: 'DELETE'
            });
            if (!response.ok) {
                throw new Error(`Error en DELETE: ${response.status}`);
            }
            return response;
        } catch (error) {
            console.error('Error en middleware.delete:', error);
            throw error;
        }
    }
};

export default middleware;
