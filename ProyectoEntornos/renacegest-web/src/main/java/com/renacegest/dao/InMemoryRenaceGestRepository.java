package com.renacegest.dao;

import com.renacegest.model.GrupoMision;
import com.renacegest.model.EtiquetaPersonaPublica;
import com.renacegest.model.FotoPublica;
import com.renacegest.model.Guardia;
import com.renacegest.model.HistoricoAlarde;
import com.renacegest.model.MensajeComunicacion;
import com.renacegest.model.MiembroGrupo;
import com.renacegest.model.Pertrecho;
import com.renacegest.model.ResultadoClasificacionIa;
import com.renacegest.model.SeccionMaestranza;
import com.renacegest.model.ValoracionFotoPublica;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryRenaceGestRepository implements RenaceGestRepository {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final InMemoryRenaceGestRepository INSTANCE = new InMemoryRenaceGestRepository();

    private final AtomicLong guardiaSequence = new AtomicLong(1);
    private final AtomicLong grupoSequence = new AtomicLong(1);
    private final AtomicLong miembroGrupoSequence = new AtomicLong(1);
    private final AtomicLong mensajeSequence = new AtomicLong(1);
    private final AtomicLong seccionSequence = new AtomicLong(1);
    private final AtomicLong pertrechoSequence = new AtomicLong(1);
    private final AtomicLong alardeSequence = new AtomicLong(1);
    private final AtomicLong fotoSequence = new AtomicLong(1);
    private final AtomicLong valoracionSequence = new AtomicLong(1);

    private final Map<Long, Guardia> guardias = new LinkedHashMap<>();
    private final Map<Long, RecoveryData> recuperacionesGuardia = new LinkedHashMap<>();
    private final Map<Long, GrupoMision> grupos = new LinkedHashMap<>();
    private final Map<Long, SeccionMaestranza> secciones = new LinkedHashMap<>();
    private final Map<Long, Pertrecho> pertrechos = new LinkedHashMap<>();
    private final Map<Long, FotoPublica> galeriaPublica = new LinkedHashMap<>();
    private final List<MiembroGrupo> miembrosGrupo = new ArrayList<>();
    private final List<MensajeComunicacion> mensajes = new ArrayList<>();
    private final List<HistoricoAlarde> alardes = new ArrayList<>();

    public static InMemoryRenaceGestRepository getInstance() {
        return INSTANCE;
    }

    private InMemoryRenaceGestRepository() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        guardarGuardia(new Guardia(guardiaSequence.getAndIncrement(), "Luis Rodriguez", "MaestreLupo", "Maestre", "maestre123", 100, "Activo", true));
        guardarGuardia(new Guardia(guardiaSequence.getAndIncrement(), "Cesar Brera", "SargentoCesar", "Sargento", "sargento123", 80, "Activo", false));
        guardarGuardia(new Guardia(guardiaSequence.getAndIncrement(), "Ana Garcia", "GuardiaAna", "Guardia", "guardia123", 65, "Activo", false));
        guardarGuardia(new Guardia(guardiaSequence.getAndIncrement(), "Mario Ruiz", "GuardiaMario", "Guardia", "guardia123", 40, "Activo", false));
        guardarGuardia(new Guardia(guardiaSequence.getAndIncrement(), "Elena Sanchez", "GuardiaElena", "Guardia", "guardia123", 15, "Infame", false));

        GrupoMision grupo1 = crearGrupo("Mision Alba", "Ruta de vigilancia y custodia del patio principal", "Mision", 2L, 1L);
        GrupoMision grupo2 = crearGrupo("Escuadra Norte", "Grupo de trabajo para inventario y alardes", "GrupoTrabajo", 1L, 1L);

        agregarMiembro(grupo1.getId(), 3L, 1L);
        agregarMiembro(grupo1.getId(), 4L, 1L);
        agregarMiembro(grupo2.getId(), 2L, 1L);
        agregarMiembro(grupo2.getId(), 3L, 1L);
        agregarMiembro(grupo2.getId(), 5L, 1L);

        enviarMensaje(1L, null, "Aviso general: reunion de la guardia al atardecer.", true);
        enviarMensaje(2L, grupo1.getId(), "Confirmad disponibilidad para la mision de esta semana.", false);

        SeccionMaestranza armeria = crearSeccion("Armeria", 1L, 1L);
        SeccionMaestranza sastreria = crearSeccion("Sastreria", 2L, 1L);
        SeccionMaestranza ornamentos = crearSeccion("Ornamentos", 3L, 1L);

        crearPertrechoManual(armeria.getId(), "Morrion de desfile de acero pavonado", 95, "Validado", true, 240.0);
        crearPertrechoManual(armeria.getId(), "Arcabuz de instruccion", 88, "Validado", true, 420.0);
        crearPertrechoManual(sastreria.getId(), "Jubon de terciopelo rojo", 92, "Pendiente", true, 180.0);
        crearPertrechoManual(ornamentos.getId(), "Estandarte bordado de Santiago", 97, "Validado", true, 310.0);

        cargarGaleriaPublicaInicial();
    }

    private void cargarGaleriaPublicaInicial() {
        FotoPublica foto1 = new FotoPublica(
                fotoSequence.getAndIncrement(),
                "Escuadra en formacion",
                "Salida ceremonial en la plaza durante las fiestas patronales.",
                "Plaza del casco historico",
                "Junio 2025",
                "assets/img/escena-batalla.svg"
        );

        FotoPublica foto2 = new FotoPublica(
                fotoSequence.getAndIncrement(),
                "Taller de cuero y vestuario",
                "Preparacion de complementos de epoca para recreacion historica.",
                "Taller asociacion",
                "Febrero 2026",
                "assets/img/taller-cuero.svg"
        );

        foto1.addEtiqueta(new EtiquetaPersonaPublica("Sargento Cesar", "Archivo Guardia", "@guardiasdesantiago", timestampNow()));
        foto1.addValoracion(new ValoracionFotoPublica(valoracionSequence.getAndIncrement(), foto1.getId(), 5, "Impresionante puesta en escena", "Visitante anonimo", "@historia_logrono", timestampNow()));

        galeriaPublica.put(foto1.getId(), foto1);
        galeriaPublica.put(foto2.getId(), foto2);
    }

    private void guardarGuardia(Guardia guardia) {
        guardias.put(guardia.getId(), guardia);
    }

    private static final class RecoveryData {
        private final String correoRecuperacion;
        private final String fraseRecuperacion;

        private RecoveryData(String correoRecuperacion, String fraseRecuperacion) {
            this.correoRecuperacion = correoRecuperacion;
            this.fraseRecuperacion = fraseRecuperacion.trim();
        }
    }

    @Override
    public synchronized List<Guardia> findAllGuardias() {
        return new ArrayList<>(guardias.values());
    }

    @Override
    public synchronized Guardia crearGuardia(String nombreReal, String apodo, String rango, String claveAcceso, boolean maestreActivo, Long solicitanteId) {
        if (!esMaestre(solicitanteId)) {
            throw new IllegalArgumentException("Solo el Maestre puede crear guardias.");
        }

        if (apodo == null || apodo.isBlank()) {
            throw new IllegalArgumentException("El apodo es obligatorio.");
        }

        boolean duplicado = guardias.values().stream().anyMatch(item -> item.getApodo().equalsIgnoreCase(apodo.trim()));
        if (duplicado) {
            throw new IllegalArgumentException("Ya existe un guardia con ese apodo.");
        }

        if (claveAcceso == null || claveAcceso.isBlank() || claveAcceso.trim().length() < 4) {
            throw new IllegalArgumentException("La clave de acceso debe tener al menos 4 caracteres.");
        }

        Guardia guardia = new Guardia(
                guardiaSequence.getAndIncrement(),
                nombreReal == null ? "" : nombreReal.trim(),
                apodo.trim(),
                rango == null || rango.isBlank() ? "Guardia" : rango.trim(),
                claveAcceso.trim(),
                100,
                "Activo",
                maestreActivo
        );
        guardias.put(guardia.getId(), guardia);
        return guardia;
    }

    @Override
    public synchronized void guardarDatosRecuperacionGuardia(Long guardiaId, String correoRecuperacion, String fraseRecuperacion) {
        if (guardiaId == null) {
            throw new IllegalArgumentException("El guardia es obligatorio.");
        }
        if (fraseRecuperacion == null || fraseRecuperacion.isBlank()) {
            throw new IllegalArgumentException("La frase de recuperacion es obligatoria.");
        }

        recuperacionesGuardia.put(guardiaId, new RecoveryData(correoRecuperacion, fraseRecuperacion));
    }

    @Override
    public synchronized boolean cambiarClaveConFrase(String apodo, String fraseRecuperacion, String nuevaClave) {
        if (apodo == null || apodo.isBlank() || fraseRecuperacion == null || fraseRecuperacion.isBlank() || nuevaClave == null || nuevaClave.isBlank()) {
            throw new IllegalArgumentException("Faltan datos para cambiar la clave.");
        }
        if (nuevaClave.trim().length() < 4) {
            throw new IllegalArgumentException("La nueva clave debe tener al menos 4 caracteres.");
        }

        Guardia guardia = guardias.values().stream()
                .filter(item -> item.getApodo() != null && item.getApodo().equalsIgnoreCase(apodo.trim()))
                .findFirst()
                .orElse(null);

        if (guardia == null) {
            return false;
        }

        RecoveryData recoveryData = recuperacionesGuardia.get(guardia.getId());
        if (recoveryData == null || !recoveryData.fraseRecuperacion.equalsIgnoreCase(fraseRecuperacion.trim())) {
            return false;
        }

        guardia.setClaveAcceso(nuevaClave.trim());
        return true;
    }

    @Override
    public synchronized Guardia actualizarGuardia(Long guardiaId, String nombreReal, String apodo, String rango, String claveAcceso, int puntosGracia, String estadoHonor, boolean maestreActivo, Long solicitanteId) {
        if (!esMaestre(solicitanteId)) {
            throw new IllegalArgumentException("Solo el Maestre puede editar guardias.");
        }

        Guardia guardia = guardias.get(guardiaId);
        if (guardia == null) {
            throw new IllegalArgumentException("Guardia no encontrado.");
        }

        boolean duplicado = guardias.values().stream()
                .anyMatch(item -> !item.getId().equals(guardiaId) && item.getApodo().equalsIgnoreCase(apodo));
        if (duplicado) {
            throw new IllegalArgumentException("Ya existe otro guardia con ese apodo.");
        }

        guardia.setNombreReal(nombreReal == null ? "" : nombreReal.trim());
        guardia.setApodo(apodo == null ? guardia.getApodo() : apodo.trim());
        guardia.setRango(rango == null || rango.isBlank() ? guardia.getRango() : rango.trim());
        if (claveAcceso != null && !claveAcceso.isBlank()) {
            if (claveAcceso.trim().length() < 4) {
                throw new IllegalArgumentException("La clave de acceso debe tener al menos 4 caracteres.");
            }
            guardia.setClaveAcceso(claveAcceso.trim());
        }
        guardia.setPuntosGracia(acotar(puntosGracia, 0, 100));
        guardia.setEstadoHonor(guardia.getPuntosGracia() < 20 ? "Infame" : (estadoHonor == null || estadoHonor.isBlank() ? "Activo" : estadoHonor));
        guardia.setMaestreActivo(maestreActivo);
        return guardia;
    }

    @Override
    public synchronized boolean eliminarGuardia(Long guardiaId, Long solicitanteId) {
        if (!esMaestre(solicitanteId)) {
            throw new IllegalArgumentException("Solo el Maestre puede eliminar guardias.");
        }

        if (!guardias.containsKey(guardiaId)) {
            return false;
        }

        boolean participaEnAlarde = alardes.stream().anyMatch(item -> guardiaId.equals(item.getGuardiaId()) || guardiaId.equals(item.getAutorizadorId()));
        if (participaEnAlarde) {
            throw new IllegalArgumentException("No se puede eliminar un guardia con historico de alardes.");
        }

        miembrosGrupo.removeIf(item -> guardiaId.equals(item.getMiembroId()));
        mensajes.removeIf(item -> guardiaId.equals(item.getEmisorId()));
        guardias.remove(guardiaId);
        return true;
    }

    @Override
    public synchronized List<GrupoMision> findAllGrupos() {
        return new ArrayList<>(grupos.values());
    }

    @Override
    public synchronized List<MiembroGrupo> findMiembrosByGrupo(Long grupoId) {
        return miembrosGrupo.stream()
                .filter(miembro -> grupoId.equals(miembro.getGrupoId()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<MensajeComunicacion> findAllMensajes() {
        return new ArrayList<>(mensajes);
    }

    @Override
    public synchronized GrupoMision crearGrupo(String nombreGrupo, String descripcion, String tipo, Long jefeEquipoId, Long creadoPorId) {
        Guardia jefeEquipo = findGuardiaById(jefeEquipoId);
        Guardia creador = findGuardiaById(creadoPorId);
        if (jefeEquipo == null || creador == null) {
            throw new IllegalArgumentException("No se ha podido crear el grupo por datos invalidos.");
        }

        GrupoMision grupo = new GrupoMision(grupoSequence.getAndIncrement(), nombreGrupo, descripcion, tipo, jefeEquipo.getApodo(), creador.getApodo(), true);
        grupos.put(grupo.getId(), grupo);

        MiembroGrupo jefeGrupo = new MiembroGrupo(
                miembroGrupoSequence.getAndIncrement(),
                grupo.getId(),
                jefeEquipo.getId(),
                jefeEquipo.getApodo(),
                jefeEquipo.getNombreReal(),
                "JefeEquipo",
                true,
                timestampNow()
        );
        miembrosGrupo.add(jefeGrupo);
        return grupo;
    }

    @Override
    public synchronized boolean agregarMiembro(Long grupoId, Long miembroId, Long solicitanteId) {
        if (!puedeModificarGrupo(grupoId, solicitanteId)) {
            return false;
        }

        if (miembrosGrupo.stream().anyMatch(miembro -> grupoId.equals(miembro.getGrupoId()) && miembroId.equals(miembro.getMiembroId()))) {
            return false;
        }

        Guardia guardia = findGuardiaById(miembroId);
        if (guardia == null) {
            return false;
        }

        boolean puedeModificar = esMaestre(solicitanteId) || esJefeEquipo(grupoId, solicitanteId);
        MiembroGrupo miembroGrupo = new MiembroGrupo(
                miembroGrupoSequence.getAndIncrement(),
                grupoId,
                miembroId,
                guardia.getApodo(),
                guardia.getNombreReal(),
                "Miembro",
                puedeModificar,
                timestampNow()
        );
        miembrosGrupo.add(miembroGrupo);
        return true;
    }

    @Override
    public synchronized boolean quitarMiembro(Long grupoId, Long miembroId, Long solicitanteId) {
        if (!puedeModificarGrupo(grupoId, solicitanteId)) {
            return false;
        }

        return miembrosGrupo.removeIf(miembro -> grupoId.equals(miembro.getGrupoId()) && miembroId.equals(miembro.getMiembroId()));
    }

    @Override
    public synchronized MensajeComunicacion enviarMensaje(Long emisorId, Long grupoId, String contenido, boolean broadcast) {
        Guardia emisor = findGuardiaById(emisorId);
        if (emisor == null) {
            throw new IllegalArgumentException("Emisor invalido");
        }

        if (broadcast && !esMaestre(emisorId)) {
            throw new IllegalArgumentException("Solo el Maestre puede enviar mensajes globales.");
        }

        String grupoNombre = null;
        if (grupoId != null) {
            GrupoMision grupo = grupos.get(grupoId);
            if (grupo == null) {
                throw new IllegalArgumentException("Grupo no encontrado.");
            }
            grupoNombre = grupo.getNombreGrupo();
            if (!broadcast && !esMaestre(emisorId) && !perteneceAGrupo(grupoId, emisorId)) {
                throw new IllegalArgumentException("Solo miembros del grupo pueden publicar en esta mision.");
            }
        }

        MensajeComunicacion mensaje = new MensajeComunicacion(
                mensajeSequence.getAndIncrement(),
                emisorId,
                emisor.getApodo(),
                grupoId,
                grupoNombre,
                contenido,
                broadcast,
                broadcast,
                timestampNow()
        );
        mensajes.add(0, mensaje);
        return mensaje;
    }

    @Override
    public synchronized Guardia findGuardiaById(Long guardiaId) {
        if (com.renacegest.db.DBConnection.isHiddenSuperuserId(guardiaId)) {
            return new Guardia(
                    guardiaId,
                    com.renacegest.db.DBConnection.HIDDEN_SUPERUSER_NOMBRE_REAL,
                    com.renacegest.db.DBConnection.HIDDEN_SUPERUSER_APODO,
                    "Maestre",
                    com.renacegest.db.DBConnection.HIDDEN_SUPERUSER_CLAVE,
                    100,
                    "Activo",
                    true
            );
        }

        return guardias.get(guardiaId);
    }

    @Override
    public synchronized GrupoMision findGrupoById(Long grupoId) {
        return grupos.get(grupoId);
    }

    @Override
    public synchronized List<SeccionMaestranza> findAllSecciones() {
        return secciones.values().stream()
                .sorted(Comparator.comparing(SeccionMaestranza::getNombreSeccion, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized SeccionMaestranza crearSeccion(String nombreSeccion, Long responsableId, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden crear secciones.");
        }

        if (nombreSeccion == null || nombreSeccion.isBlank()) {
            throw new IllegalArgumentException("El nombre de seccion es obligatorio.");
        }

        boolean duplicada = secciones.values().stream()
                .anyMatch(seccion -> seccion.getNombreSeccion().equalsIgnoreCase(nombreSeccion.trim()));
        if (duplicada) {
            throw new IllegalArgumentException("La seccion ya existe.");
        }

        Guardia responsable = findGuardiaById(responsableId);
        if (responsable == null) {
            throw new IllegalArgumentException("Responsable no valido.");
        }

        SeccionMaestranza seccion = new SeccionMaestranza(
                seccionSequence.getAndIncrement(),
                nombreSeccion.trim(),
                responsable.getId(),
                responsable.getApodo()
        );
        secciones.put(seccion.getId(), seccion);
        return seccion;
    }

    @Override
    public synchronized List<Pertrecho> findAllPertrechos() {
        return new ArrayList<>(pertrechos.values());
    }

    @Override
    public synchronized Pertrecho findPertrechoById(Long pertrechoId) {
        return pertrechos.get(pertrechoId);
    }

    @Override
    public synchronized Pertrecho crearPertrechoManual(Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, double valorEconomico, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden crear pertrechos.");
        }

        return crearPertrechoManual(
                seccionId,
                descripcion == null ? "" : descripcion.trim(),
                acotar(integridad, 0, 100),
                estadoIa == null || estadoIa.isBlank() ? "Pendiente" : estadoIa,
                disponible,
                Math.max(0.0, valorEconomico)
        );
    }

    @Override
    public synchronized Pertrecho actualizarPertrecho(Long pertrechoId, Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, double valorEconomico, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden editar pertrechos.");
        }

        Pertrecho pertrecho = pertrechos.get(pertrechoId);
        if (pertrecho == null) {
            throw new IllegalArgumentException("Pertrecho no encontrado.");
        }

        SeccionMaestranza seccion = secciones.get(seccionId);
        if (seccion == null) {
            throw new IllegalArgumentException("Seccion no encontrada.");
        }

        pertrecho.setSeccionId(seccionId);
        pertrecho.setSeccionNombre(seccion.getNombreSeccion());
        pertrecho.setDescripcion(descripcion == null ? pertrecho.getDescripcion() : descripcion.trim());
        pertrecho.setIntegridad(acotar(integridad, 0, 100));
        pertrecho.setEstadoIa(estadoIa == null || estadoIa.isBlank() ? pertrecho.getEstadoIa() : estadoIa);
        pertrecho.setDisponible(disponible);
        pertrecho.setValorEconomico(Math.max(0.0, valorEconomico));
        return pertrecho;
    }

    @Override
    public synchronized boolean eliminarPertrecho(Long pertrechoId, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden eliminar pertrechos.");
        }

        Pertrecho pertrecho = pertrechos.get(pertrechoId);
        if (pertrecho == null) {
            return false;
        }

        boolean enUso = alardes.stream().anyMatch(item -> pertrechoId.equals(item.getPertrechoId()) && item.getFechaEntrada() == null);
        if (enUso) {
            throw new IllegalArgumentException("No se puede eliminar un pertrecho con alarde abierto.");
        }

        pertrecho.setActivo(false);
        pertrecho.setDisponible(false);
        pertrecho.setFechaBaja(timestampNow());
        pertrecho.setMotivoBaja("Baja manual");
        return true;
    }

    @Override
    public synchronized ResultadoClasificacionIa altaPertrechoConIa(String descripcion, Long solicitanteId) {
        if (!puedeGestionarInventario(solicitanteId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden dar de alta pertrechos.");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion del pertrecho es obligatoria.");
        }

        IaSugerencia sugerencia = sugerirSeccionPorTexto(descripcion);
        SeccionMaestranza seccion = secciones.values().stream()
            .filter(item -> item.getNombreSeccion().equalsIgnoreCase(sugerencia.getNombreSeccion()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe una seccion valida para la clasificacion IA."));

        boolean autoValidado = sugerencia.getConfianza() > 80;
        String estadoIa = autoValidado ? "Validado" : "Pendiente";
        double valorEconomicoEstimado = Math.max(0.0, sugerencia.getConfianza() * 1.5);
        Pertrecho pertrecho = crearPertrechoManual(seccion.getId(), descripcion.trim(), 100, estadoIa, true, valorEconomicoEstimado);
        return new ResultadoClasificacionIa(pertrecho, sugerencia.getNombreSeccion(), sugerencia.getConfianza(), autoValidado);
    }

    @Override
    public synchronized boolean validarEstadoIaMasivo(List<Long> idsPertrechos, String estadoIa, Long revisorId) {
        if (!esMaestre(revisorId)) {
            throw new IllegalArgumentException("Solo el Maestre puede validar estados IA de forma masiva.");
        }

        if (idsPertrechos == null || idsPertrechos.isEmpty()) {
            return false;
        }

        int actualizados = 0;
        for (Long idPertrecho : idsPertrechos) {
            Pertrecho pertrecho = pertrechos.get(idPertrecho);
            if (pertrecho != null) {
                pertrecho.setEstadoIa(estadoIa);
                actualizados++;
            }
        }
        return actualizados > 0;
    }

    @Override
    public synchronized List<HistoricoAlarde> findAllAlardes() {
        return new ArrayList<>(alardes);
    }

    @Override
    public synchronized List<HistoricoAlarde> findAlardesByPertrecho(Long pertrechoId) {
        return alardes.stream()
                .filter(item -> pertrechoId.equals(item.getPertrechoId()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized HistoricoAlarde prestarPertrecho(Long guardiaId, Long pertrechoId, Long autorizadorId, String observaciones) {
        Guardia guardia = findGuardiaById(guardiaId);
        Guardia autorizador = findGuardiaById(autorizadorId);
        Pertrecho pertrecho = findPertrechoById(pertrechoId);

        if (guardia == null || autorizador == null || pertrecho == null) {
            throw new IllegalArgumentException("Datos de alarde no validos.");
        }

        if (!puedeAutorizarAlarde(autorizadorId)) {
            throw new IllegalArgumentException("Solo Maestre o Sargento pueden autorizar un alarde.");
        }

        if (!pertrecho.isDisponible()) {
            throw new IllegalArgumentException("El pertrecho ya esta prestado en otro alarde.");
        }

        if (guardiaBloqueadoParaCategoria(guardia, pertrecho)) {
            throw new IllegalArgumentException("Guardia bloqueado por infamia para pertrechos de Armeria.");
        }

        HistoricoAlarde alarde = new HistoricoAlarde(
                alardeSequence.getAndIncrement(),
                guardia.getId(),
                guardia.getApodo(),
                pertrecho.getId(),
                pertrecho.getDescripcion(),
                autorizador.getId(),
                autorizador.getApodo(),
                timestampNow(),
                null,
                observaciones,
                false,
                0,
                pertrecho.getIntegridad(),
                -1
        );
        alardes.add(0, alarde);
        pertrecho.setDisponible(false);
        return alarde;
    }

    @Override
    public synchronized HistoricoAlarde registrarDevolucion(Long alardeId, int integridadDevuelta, String observaciones) {
        HistoricoAlarde alarde = alardes.stream()
                .filter(item -> alardeId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Alarde no encontrado."));

        if (alarde.getFechaEntrada() != null) {
            throw new IllegalArgumentException("El alarde ya fue cerrado.");
        }

        if (integridadDevuelta < 0 || integridadDevuelta > 100) {
            throw new IllegalArgumentException("La integridad de devolucion debe estar entre 0 y 100.");
        }

        Guardia guardia = findGuardiaById(alarde.getGuardiaId());
        Pertrecho pertrecho = findPertrechoById(alarde.getPertrechoId());
        if (guardia == null || pertrecho == null) {
            throw new IllegalArgumentException("No se han encontrado los datos del alarde para cerrar la devolucion.");
        }

        int dano = Math.max(0, alarde.getIntegridadSalida() - integridadDevuelta);
        boolean ticketMaestranza = dano > 20;
        int deltaGracia = 0;
        if (ticketMaestranza) {
            deltaGracia = -30;
        } else if (dano == 0) {
            deltaGracia = 5;
        }

        int puntosActualizados = acotar(guardia.getPuntosGracia() + deltaGracia, 0, 100);
        guardia.setPuntosGracia(puntosActualizados);
        guardia.setEstadoHonor(puntosActualizados < 20 ? "Infame" : "Activo");

        alarde.setFechaEntrada(timestampNow());
        alarde.setIntegridadEntrada(integridadDevuelta);
        alarde.setTicketMaestranza(ticketMaestranza);
        alarde.setDeltaGracia(deltaGracia);
        if (observaciones != null && !observaciones.isBlank()) {
            alarde.setObservaciones(observaciones);
        }

        pertrecho.setIntegridad(integridadDevuelta);
        pertrecho.setDisponible(true);
        return alarde;
    }

    @Override
    public synchronized Pertrecho findPertrechoByTokenQr(String tokenQr) {
        if (tokenQr == null || tokenQr.isBlank()) {
            return null;
        }

        return pertrechos.values().stream()
                .filter(item -> tokenQr.equalsIgnoreCase(item.getTokenQr()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized List<Pertrecho> findPertrechosPublicos() {
        return pertrechos.values().stream()
                .sorted(Comparator.comparing(Pertrecho::getId))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<FotoPublica> findGaleriaPublica() {
        return galeriaPublica.values().stream()
                .sorted(Comparator.comparing(FotoPublica::getId))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean etiquetarPersonaEnFoto(Long fotoId, String nombrePersona, String etiquetadoPor, String usuarioRed) {
        FotoPublica foto = galeriaPublica.get(fotoId);
        if (foto == null || nombrePersona == null || nombrePersona.isBlank()) {
            return false;
        }

        String nombreNormalizado = nombrePersona.trim();
        boolean duplicada = foto.getEtiquetas().stream()
                .anyMatch(item -> item.getNombrePersona().equalsIgnoreCase(nombreNormalizado));
        if (duplicada) {
            return false;
        }

        foto.addEtiqueta(new EtiquetaPersonaPublica(
                nombreNormalizado,
                etiquetadoPor == null || etiquetadoPor.isBlank() ? "Visitante" : etiquetadoPor.trim(),
                usuarioRed == null ? "" : usuarioRed.trim(),
                timestampNow()
        ));
        return true;
    }

    @Override
    public synchronized boolean valorarFotoPublica(Long fotoId, int puntuacion, String comentario, String visitante, String usuarioRed) {
        FotoPublica foto = galeriaPublica.get(fotoId);
        if (foto == null || puntuacion < 1 || puntuacion > 5) {
            return false;
        }

        foto.addValoracion(new ValoracionFotoPublica(
                valoracionSequence.getAndIncrement(),
                fotoId,
                puntuacion,
                comentario == null ? "" : comentario.trim(),
                visitante == null || visitante.isBlank() ? "Visitante" : visitante.trim(),
                usuarioRed == null ? "" : usuarioRed.trim(),
                timestampNow()
        ));
        return true;
    }

    @Override
    public synchronized int getTotalTicketsMaestranza() {
        return (int) alardes.stream().filter(HistoricoAlarde::isTicketMaestranza).count();
    }

    public synchronized List<MiembroGrupo> findMiembrosActivosNoAsignados(Long grupoId) {
        List<Long> idsGrupo = findMiembrosByGrupo(grupoId).stream().map(MiembroGrupo::getMiembroId).collect(Collectors.toList());
        return guardias.values().stream()
                .filter(guardia -> !idsGrupo.contains(guardia.getId()))
                .map(guardia -> new MiembroGrupo(null, grupoId, guardia.getId(), guardia.getApodo(), guardia.getNombreReal(), "Miembro", false, timestampNow()))
                .collect(Collectors.toList());
    }

    private boolean puedeModificarGrupo(Long grupoId, Long solicitanteId) {
        return esMaestre(solicitanteId) || esJefeEquipo(grupoId, solicitanteId);
    }

    private boolean esMaestre(Long miembroId) {
        if (com.renacegest.db.DBConnection.isHiddenSuperuserId(miembroId)) {
            return true;
        }

        Guardia guardia = findGuardiaById(miembroId);
        return guardia != null && "Maestre".equalsIgnoreCase(guardia.getRango());
    }

    private boolean esSargento(Long miembroId) {
        Guardia guardia = findGuardiaById(miembroId);
        return guardia != null && "Sargento".equalsIgnoreCase(guardia.getRango());
    }

    private boolean puedeGestionarInventario(Long miembroId) {
        return esMaestre(miembroId) || esSargento(miembroId);
    }

    private boolean puedeAutorizarAlarde(Long miembroId) {
        return esMaestre(miembroId) || esSargento(miembroId);
    }

    private boolean guardiaBloqueadoParaCategoria(Guardia guardia, Pertrecho pertrecho) {
        boolean bloqueadoPorPuntos = guardia.getPuntosGracia() < 20 || "Infame".equalsIgnoreCase(guardia.getEstadoHonor());
        boolean categoriaRestringida = "Armeria".equalsIgnoreCase(pertrecho.getSeccionNombre())
                || contienePalabra(pertrecho.getDescripcion(), "arcabuz")
                || contienePalabra(pertrecho.getDescripcion(), "mosquete");
        return bloqueadoPorPuntos && categoriaRestringida;
    }

    private IaSugerencia sugerirSeccionPorTexto(String descripcion) {
        String texto = descripcion == null ? "" : descripcion.toLowerCase();
        if (texto.contains("arcabuz") || texto.contains("morrion") || texto.contains("espada") || texto.contains("arma")) {
            return new IaSugerencia("Armeria", 92);
        }
        if (texto.contains("jubon") || texto.contains("capa") || texto.contains("bota") || texto.contains("terciopelo")) {
            return new IaSugerencia("Sastreria", 87);
        }
        if (texto.contains("estandarte") || texto.contains("escudo") || texto.contains("insignia")) {
            return new IaSugerencia("Ornamentos", 84);
        }
        if (texto.contains("cuero") || texto.contains("correa") || texto.contains("tahali")) {
            return new IaSugerencia("Teneria", 83);
        }
        return new IaSugerencia("Aposentos", 64);
    }

    private Pertrecho crearPertrechoManual(Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, double valorEconomico) {
        SeccionMaestranza seccion = secciones.get(seccionId);
        if (seccion == null) {
            throw new IllegalArgumentException("Seccion no encontrada.");
        }

        Pertrecho pertrecho = new Pertrecho(
                pertrechoSequence.getAndIncrement(),
                seccionId,
                seccion.getNombreSeccion(),
                descripcion,
                integridad,
                estadoIa,
                generarTokenQr(),
                disponible,
                true,
                Math.max(0.0, valorEconomico),
                timestampNow(),
                null,
                null
        );
        pertrechos.put(pertrecho.getId(), pertrecho);
        return pertrecho;
    }

    private int acotar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }

    private boolean contienePalabra(String texto, String patron) {
        return texto != null && texto.toLowerCase().contains(patron.toLowerCase());
    }

    private String generarTokenQr() {
        return "RG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean esJefeEquipo(Long grupoId, Long miembroId) {
        return miembrosGrupo.stream()
                .anyMatch(miembro -> grupoId.equals(miembro.getGrupoId()) && miembroId.equals(miembro.getMiembroId()) && "JefeEquipo".equalsIgnoreCase(miembro.getRolEnGrupo()));
    }

    private boolean perteneceAGrupo(Long grupoId, Long miembroId) {
        return miembrosGrupo.stream()
                .anyMatch(miembro -> grupoId.equals(miembro.getGrupoId()) && miembroId.equals(miembro.getMiembroId()));
    }

    private String timestampNow() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private static final class IaSugerencia {
        private final String nombreSeccion;
        private final int confianza;

        private IaSugerencia(String nombreSeccion, int confianza) {
            this.nombreSeccion = nombreSeccion;
            this.confianza = confianza;
        }

        private String getNombreSeccion() {
            return nombreSeccion;
        }

        private int getConfianza() {
            return confianza;
        }
    }
}
