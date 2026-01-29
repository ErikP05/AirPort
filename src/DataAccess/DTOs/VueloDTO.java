package DataAccess.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;


public class VueloDTO {
    private Integer IdVuelo;
    private Integer IdPaisOrigen;
    private Integer IdPaisDestino;
    private Integer IdAvion;
    private LocalDate FechaVuelo;
    private LocalTime HoraVuelo;
    private String Estado;
    private String FechaCreacion;
    private String FechaModifica;

    public VueloDTO() {
    }


    public VueloDTO(Integer idVuelo, Integer idPaisOrigen, Integer idPaisDestino, Integer idAvion, LocalDate fechaVuelo) {
        IdVuelo = idVuelo;
        IdPaisOrigen = idPaisOrigen;
        IdPaisDestino = idPaisDestino;
        IdAvion = idAvion;
        FechaVuelo = fechaVuelo;
    }


    public VueloDTO(Integer idVuelo, Integer idPaisOrigen, Integer idPaisDestino, Integer idAvion, LocalDate fechaVuelo, LocalTime horaVuelo) {
        IdVuelo = idVuelo;
        IdPaisOrigen = idPaisOrigen;
        IdPaisDestino = idPaisDestino;
        IdAvion = idAvion;
        FechaVuelo = fechaVuelo;
        HoraVuelo = horaVuelo;
    }


    public VueloDTO(Integer idVuelo, Integer idPaisOrigen, Integer idPaisDestino, Integer idAvion, LocalDate fechaVuelo, LocalTime horaVuelo, String estado,
            String fechaCreacion, String fechaModifica) {
        IdVuelo = idVuelo;
        IdPaisOrigen = idPaisOrigen;
        IdPaisDestino = idPaisDestino;
        IdAvion = idAvion;
        FechaVuelo = fechaVuelo;
        HoraVuelo = horaVuelo;
        Estado = estado;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }
    public LocalDate getFechaVuelo() {
        return FechaVuelo;
    }

    public void setFechaVuelo(LocalDate fechaVuelo) {
        FechaVuelo = fechaVuelo;
    }

    public LocalTime getHoraVuelo() {
        return HoraVuelo;
    }

    public void setHoraVuelo(LocalTime horaVuelo) {
        HoraVuelo = horaVuelo;
    }

    public Integer getIdVuelo() {
        return IdVuelo;
    }

    public void setIdVuelo(Integer idVuelo) {
        IdVuelo = idVuelo;
    }

    public Integer getIdPaisOrigen() {
        return IdPaisOrigen;
    }

    public void setIdPaisOrigen(Integer idPaisOrigen) {
        IdPaisOrigen = idPaisOrigen;
    }

    public Integer getIdPaisDestino() {
        return IdPaisDestino;
    }

    public void setIdPaisDestino(Integer idPaisDestino) {
        IdPaisDestino = idPaisDestino;
    }

    public Integer getIdAvion() {
        return IdAvion;
    }

    public void setIdAvion(Integer idAvion) {
        IdAvion = idAvion;
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
                + "\n IdVuelo     : " + getIdVuelo()
                + "\n IdPaisOrigen         : " + getIdPaisOrigen()
                + "\n IdPaisDestino       : " + getIdPaisDestino()
                + "\n IdAvion         : " + getIdAvion()
                + "\n FechaVuelo     : " + getFechaVuelo()
                + "\n HoraVuelo     : " + getHoraVuelo()
                + "\n Estado         : " + getEstado()
                + "\n FechaCreacion  : " + getFechaCreacion()
                + "\n FechaModifica  : " + getFechaModifica();
    }
}
