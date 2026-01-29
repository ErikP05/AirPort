package DataAccess.DTOs;

public class PaisDestinoDTO {
    private Integer IdPaisDestino;
    private String Nombre;
    private String Estado;
    private String FechaCreacion;
    private String FechaModifica;

    public PaisDestinoDTO() {
    }

    public PaisDestinoDTO(String nombre) {
        IdPaisDestino = 0;
        Nombre = nombre;
    }

    public PaisDestinoDTO(Integer idPaisDestino, String nombre, String estado, String fechaCreacion,
            String fechaModifica) {
        IdPaisDestino = idPaisDestino;
        Nombre = nombre;
        Estado = estado;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }

    public Integer getIdPaisDestino() {
        return IdPaisDestino;
    }

    public void setIdPaisDestino(Integer idPaisDestino) {
        IdPaisDestino = idPaisDestino;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
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
                + "\n IdPaisDestino : " + getIdPaisDestino()
                + "\n Nombre        : " + getNombre()
                + "\n Estado        : " + getEstado()
                + "\n FechaCreacion : " + getFechaCreacion()
                + "\n FechaModifica : " + getFechaModifica();
    }
}
