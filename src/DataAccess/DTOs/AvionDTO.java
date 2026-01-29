package DataAccess.DTOs;

public class AvionDTO {
    private Integer IdAvion;
    private String Serie;
    private Integer CantidadAsientos;
    private String Estado;
    private String FechaCreacion;
    private String FechaModifica;

    public AvionDTO() {
    }

    public AvionDTO(String serie, Integer cantidadAsientos, String estado) {
        IdAvion = 0;
        Serie = serie;
        CantidadAsientos = cantidadAsientos;
        Estado = estado;
    }

    public AvionDTO(Integer idAvion, String serie, Integer cantidadAsientos, String estado,
            String fechaCreacion, String fechaModifica) {
        IdAvion = idAvion;
        Serie = serie;
        CantidadAsientos = cantidadAsientos;
        Estado = estado;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }

    public Integer getIdAvion() {
        return IdAvion;
    }

    public void setIdAvion(Integer idAvion) {
        IdAvion = idAvion;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        Serie = serie;
    }

    public Integer getCantidadAsientos() {
        return CantidadAsientos;
    }

    public void setCantidadAsientos(Integer cantidadAsientos) {
        CantidadAsientos = cantidadAsientos;
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
                + "\n IdAvion         : " + getIdAvion()
                + "\n Serie           : " + getSerie()
                + "\n CantidadAsientos: " + getCantidadAsientos()
                + "\n Estado          : " + getEstado()
                + "\n FechaCreacion   : " + getFechaCreacion()
                + "\n FechaModifica   : " + getFechaModifica()
                + "\n --------------------------- ";
    }
}
