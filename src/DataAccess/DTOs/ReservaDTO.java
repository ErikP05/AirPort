package DataAccess.DTOs;

public class ReservaDTO {

    private Integer IdReserva;
    private Integer IdVuelo;
    private Integer IdPasajero;
    private String EstadoCheckin;
    private String Asiento;
    private String Estado;
    private String FechaCreacion;
    private String FechaModifica;

    public ReservaDTO() {
    }

    public ReservaDTO(Integer idReserva, Integer idVuelo, Integer idPasajero, String estadoCheckin, String asiento) {
        IdReserva = idReserva;
        IdVuelo = idVuelo;
        IdPasajero = idPasajero;
        EstadoCheckin = estadoCheckin;
        Asiento = asiento;
    }

    public ReservaDTO(Integer idReserva, Integer idVuelo, Integer idPasajero, String estadoCheckin, String asiento,
            String estado, String fechaCreacion, String fechaModifica) {
        IdReserva = idReserva;
        IdVuelo = idVuelo;
        IdPasajero = idPasajero;
        EstadoCheckin = estadoCheckin;
        Asiento = asiento;
        Estado = estado;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }

    public Integer getIdReserva() {
        return IdReserva;
    }

    public void setIdReserva(Integer idReserva) {
        IdReserva = idReserva;
    }

    public Integer getIdVuelo() {
        return IdVuelo;
    }

    public void setIdVuelo(Integer idVuelo) {
        IdVuelo = idVuelo;
    }

    public Integer getIdPasajero() {
        return IdPasajero;
    }

    public void setIdPasajero(Integer idPasajero) {
        IdPasajero = idPasajero;
    }

    public String getEstadoCheckin() {
        return EstadoCheckin;
    }

    public void setEstadoCheckin(String estadoCheckin) {
        EstadoCheckin = estadoCheckin;
    }

    public String getAsiento() {
        return Asiento;
    }

    public void setAsiento(String asiento) {
        Asiento = asiento;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getFechaCreacion() {
        return FechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        FechaCreacion = fechaCreacion;
    }

    public String getFechaModifica() {
        return FechaModifica;
    }

    public void setFechaModifica(String fechaModifica) {
        FechaModifica = fechaModifica;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + "\n IdReserva      : " + getIdReserva()
                + "\n IdVuelo        : " + getIdVuelo()
                + "\n IdPasajero     : " + getIdPasajero()
                + "\n EstadoCheckin  : " + getEstadoCheckin()
                + "\n Asiento        : " + getAsiento()
                + "\n Estado         : " + getEstado()
                + "\n FechaCreacion  : " + getFechaCreacion()
                + "\n FechaModifica  : " + getFechaModifica();
    }
}
