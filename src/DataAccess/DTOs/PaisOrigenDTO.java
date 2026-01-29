package DataAccess.DTOs;

public class PaisOrigenDTO {
    private Integer IdPaisOrigen;
    private String Nombre;
    private String Estado;
    private String FechaCreacion;
    private String FechaModifica;

    public PaisOrigenDTO() {
    }

    public PaisOrigenDTO(String nombre) {
        IdPaisOrigen = 0;
        Nombre = nombre;
    }

    public PaisOrigenDTO(Integer idPaisOrigen, String nombre, String estado, String fechaCreacion,
            String fechaModifica) {
        IdPaisOrigen = idPaisOrigen;
        Nombre = nombre;
        Estado = estado;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }

    public Integer getIdPaisOrigen() {
        return IdPaisOrigen;
    }

    public void setIdPaisOrigen(Integer idPaisOrigen) {
        IdPaisOrigen = idPaisOrigen;
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
                + "\n IdPaisOrigen  : " + getIdPaisOrigen()
                + "\n Nombre        : " + getNombre()
                + "\n Estado        : " + getEstado()
                + "\n FechaCreacion : " + getFechaCreacion()
                + "\n FechaModifica : " + getFechaModifica();
    }
}
