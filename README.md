# Policlínico Autovías Seguras - Sistema Web

Sitio web corporativo y sistema de gestión de citas médicas para Policlínico Autovías Seguras.  
**Desarrollado por:** Juan Elias Arango Salvador

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java**: 21.0.7 LTS (Oracle OpenJDK)
- **Spring Boot**: 3.4.1
- **Maven**: 3.9.9
- **Thymeleaf**: Motor de templates HTML del lado del servidor
- **Spring Web**: Para controladores REST y MVC
- **Spring Data JPA**: Gestión de persistencia
- **Spring Validation**: Validación de formularios
- **Spring Mail**: Envío de correos electrónicos
- **Lombok**: Reducción de código boilerplate

### Integraciones
- **Google Sheets API**: v4-rev20230815-2.0.0 - Respaldo de datos en la nube
- **H2 Database**: Base de datos en memoria para desarrollo

### Frontend
- **HTML5 + CSS3**: Diseño responsive custom
- **JavaScript Vanilla**: Funcionalidades interactivas
- **Thymeleaf**: Templates del lado del servidor
- **Font Awesome**: 6.5.1 - Iconografía
- **Google Fonts**: Poppins

### Infraestructura
- **Azure App Service**: Hosting y despliegue continuo
- **GitHub Actions**: CI/CD automatizado
- **Git**: Control de versiones

---

## 📁 Estructura del Proyecto

```
Policlinico-Autovias-Seguras/
├── .github/
│   └── workflows/
│       └── main_policlinicoautoviasseguras.yml   # CI/CD con Azure
├── src/
│   ├── main/
│   │   ├── java/com/policlinico/autovias/
│   │   │   ├── controller/      # Controladores MVC
│   │   │   ├── model/           # Entidades y DTOs
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── repository/      # Acceso a datos
│   │   │   └── config/          # Configuraciones Spring
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/         # Estilos personalizados
│   │       │   └── js/          # Scripts JavaScript
│   │       ├── templates/       # Vistas Thymeleaf
│   │       │   ├── fragments/   # Componentes reutilizables (navbar, footer)
│   │       │   ├── admin/       # Panel administrativo
│   │       │   ├── email/       # Templates de correos
│   │       │   └── error/       # Páginas de error
│   │       ├── application.properties  # Configuración app
│   │       └── credentials/     # Credenciales Google API (no versionado)
│   └── test/                    # Tests unitarios
├── target/                      # Compilados (no versionado)
├── pom.xml                      # Dependencias Maven
└── README.md                    # Este archivo
```

---

## 🚀 Instalación y Ejecución Local

### Prerrequisitos
- **Java JDK 21** instalado
- **Maven 3.9+** instalado
- **Git** para clonar el repositorio

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JEAS-14/Policlinico-Autovias-Seguras.git
   cd Policlinico-Autovias-Seguras
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**
   ```bash
   # En Windows
   run.cmd
   
   # O en Linux/Mac
   mvn clean spring-boot:run -DskipTests
   ```

4. **Acceder al sitio**
   - Abre tu navegador en: `http://localhost:8080`

### Comandos Útiles

| Comando | Descripción |
|---------|-------------|
| `run.cmd` | **[Windows]** Iniciar servidor con Java 21 |
| `mvn clean compile` | Recompilar cambios (hot reload manual) |
| `mvn clean package` | Generar archivo JAR en `target/` |
| `mvn test` | Ejecutar tests unitarios |
| `mvn clean install` | Instalación completa con dependencias |

**NOTA:** El comando `mvn spring-boot:run` ya **NO funciona** en este proyecto por incompatibilidad de Java 25. **Usa `run.cmd` en su lugar.**

### ⚠️ Nota Importante: Java 21 Requerido
Este proyecto **solo funciona con Java 21 LTS**. Si tu VS Code tiene Java 25 configurado, usa:
- **Windows**: `run.cmd` (incluido en el repositorio)
- **Linux/Mac**: Verifica que tu `JAVA_HOME` apunte a Java 21

---

## 🌐 Despliegue

### Proceso Automático (CI/CD)

1. **Hacer cambios en el código**
2. **Commit y push a la rama `main`**
   ```bash
   git add .
   git commit -m "Descripción del cambio"
   git push origin main
   ```
3. **GitHub Actions se activa automáticamente**
   - Compila el proyecto con Maven
   - Ejecuta tests
   - Genera el archivo JAR
   - Despliega en Azure App Service

4. **Verificar despliegue**
   - Ve a: https://github.com/JEAS-14/Policlinico-Autovias-Seguras/actions
   - Espera el ✅ verde
   - Accede a: https://policlinicoautoviasseguras.azurewebsites.net

---

## 🔧 Arquitectura

**Patrón MTC (Model-Template-Controller)**

Similar a MVC pero adaptado para Thymeleaf:

- **Model**: Entidades JPA y DTOs (`@Entity`, `@Data`)
- **Template**: Vistas HTML con Thymeleaf (`*.html`)
- **Controller**: Controladores Spring (`@Controller`, `@GetMapping`, `@PostMapping`)

**Flujo de una solicitud:**
```
Usuario → Controller → Service → Repository → Database
                ↓
            Template ← Model
                ↓
            Response HTML
```

---

## 📋 Funcionalidades Principales

✅ **Landing page profesional** con información de servicios  
✅ **Sistema de reserva de citas médicas** con validación  
✅ **Integración con Google Sheets** para respaldo de datos  
✅ **Envío de correos** de confirmación automáticos  
✅ **Panel administrativo** para gestión de citas  
✅ **Diseño 100% responsive** (móvil, tablet, desktop)  
✅ **Páginas de servicios**: Brevetes, Escuela de Conductores, SUCAMEC, Ocupacional  
✅ **Libro de reclamaciones digital**  
✅ **Blog corporativo** con artículos de salud  

---

## 🐛 Troubleshooting

### Problema: "Application failed to start"
**Solución:** Verifica que uses Java 21 y Spring Boot 3.4.1
```bash
java -version  # Debe mostrar 21.x.x
mvn -version   # Verifica Maven 3.9+
```

### Problema: Azure deployment falla
**Causa común:** Spring Boot version incorrecta en `pom.xml`  
**Solución:** Verifica que `<version>3.4.1</version>` en el `<parent>`

### Problema: Cambios CSS/JS no se reflejan en Azure
**Solución:** Limpia caché del navegador
- **Chrome/Edge**: `Ctrl + Shift + R`
- **Firefox**: `Ctrl + F5`

### Problema: Google Sheets no guarda datos
**Solución:** Verifica que `credentials/policlinico-backup-*.json` exista y sea válido

---

## 📞 Contacto

**Policlínico Autovías Seguras**  
📱 WhatsApp: +51 913 889 497  
📧 Email: info@policlinicoautoviasseguras.com  
🌐 Web: https://policlinicoautoviasseguras.azurewebsites.net

---

## 📝 Licencia

© 2026 Policlínico Autovías Seguras. Todos los derechos reservados.  
Desarrollado por Juan Elias Arango Salvador.
