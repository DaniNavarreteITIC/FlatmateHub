# FlatmateHub — Menú y Lista

Aplicación Android desarrollada en Kotlin para la organización y gestión de compañeros de piso. En esta práctica se ha trabajado la pantalla inicial con logotipo y animación, el menú de navegación entre vistas, las listas dinámicas mediante `RecyclerView` y la documentación del código.

## Pantalla inicial y logotipo
* La aplicación cuenta con una pantalla de entrada (*splash screen*) personalizada con el logotipo de **FlatmateHub** y su animación antes de pasar a la pantalla principal.
* Desde ahí se accede al flujo principal de la aplicación y a las distintas secciones mediante la navegación configurada.

## Menú de navegación
* Se ha programado el menú de navegación para moverse de forma funcional entre las pantallas disponibles de la aplicación.
* Cada opción del menú conecta con su pantalla correspondiente para que el usuario pueda desplazarse con claridad por el proyecto.

## Listas dinámicas (`RecyclerView`, `Adapter` y `Holder`)
* Se utiliza `RecyclerView` con elementos de prueba para mostrar listados dinámicos y permitir la interacción sobre los ítems.
* **Clase `Adapter`:** Gestiona la colección de datos de prueba, crea y enlaza las vistas de cada elemento y coordina los cambios de la lista.
* **Clase `Holder`:** Mantiene las referencias a los componentes visuales de cada fila y asigna los datos correspondientes de cada ítem.
* **Eventos (añadir, modificar y eliminar):** Se han programado las acciones de añadir, modificar y eliminar elementos, mostrando su ejecución mediante `Log` o `Toast` para verificar el comportamiento.

## Usabilidad y accesibilidad
* Se han tenido en cuenta criterios de usabilidad y accesibilidad en la interfaz (como textos claros, etiquetas descriptivas en elementos visuales o botones y un diseño cómodo de usar).
* Estas decisiones ayudan a que la aplicación sea más comprensible y accesible durante su uso.

## Documentación del código
* Las partes más relevantes del código en Kotlin —especialmente las clases `Adapter` y `Holder`, así como la programación del menú— se han documentado con comentarios **KDoc** explicados con palabras propias.
* La documentación HTML se ha generado con **Dokka** ejecutando la tarea `dokkaGenerateHtml` y se ha copiado en `docs/dokka/index.html` para poder abrirla y consultarla directamente en el navegador.
