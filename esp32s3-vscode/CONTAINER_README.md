**Guía Rápida — Devcontainer ESP‑IDF (v5.5.1)**

Este documento explica cómo usar el devcontainer configurado en este repositorio para desarrollar con ESP‑IDF v5.5.1.

**Requisitos Previos:**
- **Docker:** instalado y el daemon en ejecución accesible desde WSL/ VS Code.
- **VS Code** con la extensión **Remote - Containers** (o Dev Containers).

**Abrir y reconstruir el Devcontainer**
- **Desde VS Code (recomendado):** abre la carpeta del repo en WSL y ejecuta el comando "Dev Containers: Rebuild and Reopen in Container" (Ctrl+Shift+P).
- **Alternativa CLI (si prefieres Docker manual):**
  - desde WSL en la raíz del repo:
    docker build -f .devcontainer/Dockerfile --build-arg DOCKER_TAG=v5.5.1 -t simpletelemetry-devcontainer:v5.5.1 .devcontainer
    docker run --rm -it --privileged -v "${PWD}:/workspaces/esp32s3-vscode" -w /workspaces/esp32s3-vscode simpletelemetry-devcontainer:v5.5.1 bash

**Qué hace el contenedor al arrancar**
- El contenedor usa la imagen base "espressif/idf:v5.5.1" y exporta el entorno de ESP‑IDF en "/opt/esp/idf".
- Hemos añadido un postStartCommand en ".devcontainer/devcontainer.json" que ejecuta: 
  . /opt/esp/idf/export.sh && idf.py --version && python3 -V

**Comandos útiles dentro del contenedor (terminal integrada)**
- Activar entorno (si hace falta):
  . /opt/esp/idf/export.sh
- Ver versiones:
  idf.py --version
  python3 -V
- Compilar el proyecto:
  idf.py build
- Configurar opciones (menuconfig):
  idf.py menuconfig
- Flashear a la placa (ejemplo, puerto detectado o configurado en settings):
  idf.py -p /dev/ttyUSB0 flash
- Monitor serial:
  idf.py -p /dev/ttyUSB0 monitor
- Limpiar la build:
  idf.py fullclean

**Ajustes en VS Code**
- Las configuraciones del contenedor (rutas a "/opt/esp/idf" y "/opt/esp") están en ".devcontainer/devcontainer.json" → customizations.vscode.settings.
- La workspace settings dentro del contenedor se aplicarán automáticamente: consulta ".vscode/settings.json" (hemos removido rutas absolutas del host para evitar conflictos).

**Serial / permisos / dispositivos USB**
- Si necesitas acceso al puerto serie desde dentro del contenedor, hay dos opciones:
  - Ejecutar el contenedor con "--privileged" (ya está configurado en "devcontainer.json") y mapear dispositivos si es necesario.
  - Mapear un dispositivo específico con "runArgs" en "devcontainer.json", por ejemplo:
    "runArgs": ["--device=/dev/ttyUSB0:/dev/ttyUSB0","--privileged"]
- En Linux, asegúrate que el usuario dentro del contenedor tenga acceso a "/dev/ttyUSB0" (grupo dialout) o usa "--privileged".

**clangd / LSP**
- Hemos configurado "clangd.path" y "clangd.arguments" en "devcontainer.json" para que el LSP use el toolchain incluido en la imagen ("/opt/esp/tools/..."). Si por alguna razón las rutas cambian dentro de la imagen, ajusta esas entradas.

**Depuración y OpenOCD**
- La imagen incluye "openocd-esp32" y toolchains. Si vas a depurar con un probe, configura la opción correcta en "launch.json" y asegúrate de mapear el dispositivo USB del probe en "devcontainer.json" si es necesario.

**Consejos rápidos**
- Siempre reconstruye el contenedor si cambias ".devcontainer/Dockerfile" o ".devcontainer/devcontainer.json" (Rebuild and Reopen).
- Usa la terminal integrada en VS Code (abierta dentro del contenedor) para ejecutar `idf.py` y comandos de build.
- Si el monitor no se conecta: revisa que el puerto serie esté mapeado y que el usuario del contenedor tenga permisos.

**Si algo falla**
- Revisa la salida del postStartCommand en el panel de contenedores — mostrará `idf.py --version` y `python3 -V` al arrancar.
- Para problemas de Docker/WSL: ejecuta `docker info` y confirma que Docker Desktop o el daemon está funcionando y accesible desde WSL.

**Soporte**
Si quieres, puedo:
- Añadir montado de un puerto serie específico en "devcontainer.json".
- Generar un README adicional dentro de ".devcontainer" para documentar más pasos.
- Ajustar "clangd" si la imagen usa rutas diferentes tras rebuild.

---
Archivo relacionado: ".devcontainer/devcontainer.json" (configuración del contenedor), ".devcontainer/Dockerfile" (imagen base), ".vscode/settings.json" (workspace settings ligeros).

*** Fin del documento ***
