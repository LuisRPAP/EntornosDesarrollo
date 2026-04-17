package com.renacegest.dao;

import com.renacegest.model.GrupoMision;
import com.renacegest.model.FotoPublica;
import com.renacegest.model.Guardia;
import com.renacegest.model.HistoricoAlarde;
import com.renacegest.model.MensajeComunicacion;
import com.renacegest.model.MiembroGrupo;
import com.renacegest.model.Pertrecho;
import com.renacegest.model.ResultadoClasificacionIa;
import com.renacegest.model.SeccionMaestranza;

import java.util.List;

public interface RenaceGestRepository {
    List<Guardia> findAllGuardias();

    Guardia crearGuardia(String nombreReal, String apodo, String rango, String claveAcceso, boolean maestreActivo, Long solicitanteId);

    void guardarDatosRecuperacionGuardia(Long guardiaId, String correoRecuperacion, String fraseRecuperacion);

    boolean cambiarClaveConFrase(String apodo, String fraseRecuperacion, String nuevaClave);

    Guardia actualizarGuardia(Long guardiaId, String nombreReal, String apodo, String rango, String claveAcceso, int puntosGracia, String estadoHonor, boolean maestreActivo, Long solicitanteId);

    boolean eliminarGuardia(Long guardiaId, Long solicitanteId);

    List<GrupoMision> findAllGrupos();

    List<MiembroGrupo> findMiembrosByGrupo(Long grupoId);

    List<MensajeComunicacion> findAllMensajes();

    GrupoMision crearGrupo(String nombreGrupo, String descripcion, String tipo, Long jefeEquipoId, Long creadoPorId);

    boolean agregarMiembro(Long grupoId, Long miembroId, Long solicitanteId);

    boolean quitarMiembro(Long grupoId, Long miembroId, Long solicitanteId);

    MensajeComunicacion enviarMensaje(Long emisorId, Long grupoId, String contenido, boolean broadcast);

    Guardia findGuardiaById(Long guardiaId);

    GrupoMision findGrupoById(Long grupoId);

    List<SeccionMaestranza> findAllSecciones();

    SeccionMaestranza crearSeccion(String nombreSeccion, Long responsableId, Long solicitanteId);

    List<Pertrecho> findAllPertrechos();

    Pertrecho findPertrechoById(Long pertrechoId);

    Pertrecho crearPertrechoManual(Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, double valorEconomico, Long solicitanteId);

    Pertrecho actualizarPertrecho(Long pertrechoId, Long seccionId, String descripcion, int integridad, String estadoIa, boolean disponible, double valorEconomico, Long solicitanteId);

    boolean eliminarPertrecho(Long pertrechoId, Long solicitanteId);

    ResultadoClasificacionIa altaPertrechoConIa(String descripcion, Long solicitanteId);

    boolean validarEstadoIaMasivo(List<Long> idsPertrechos, String estadoIa, Long revisorId);

    List<HistoricoAlarde> findAllAlardes();

    List<HistoricoAlarde> findAlardesByPertrecho(Long pertrechoId);

    HistoricoAlarde prestarPertrecho(Long guardiaId, Long pertrechoId, Long autorizadorId, String observaciones);

    HistoricoAlarde registrarDevolucion(Long alardeId, int integridadDevuelta, String observaciones);

    Pertrecho findPertrechoByTokenQr(String tokenQr);

    List<Pertrecho> findPertrechosPublicos();

    List<FotoPublica> findGaleriaPublica();

    boolean etiquetarPersonaEnFoto(Long fotoId, String nombrePersona, String etiquetadoPor, String usuarioRed);

    boolean valorarFotoPublica(Long fotoId, int puntuacion, String comentario, String visitante, String usuarioRed);

    int getTotalTicketsMaestranza();
}
