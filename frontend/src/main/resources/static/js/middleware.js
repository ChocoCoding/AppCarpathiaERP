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
                throw new Error(`Error en POST: ${response.status}`);
            }
            const responseData = await response.json();
            return responseData;
        } catch (error) {
            console.error('Error en middleware.post:', error);
            throw error;
        }
    },
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
                throw new Error(`Error en PUT: ${response.status}`);
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
