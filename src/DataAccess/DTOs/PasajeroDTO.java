package DataAccess.DTOs;

public class PasajeroDTO {

    private Integer IdPasajero;
    private Integer IdSexo;
    private String Nombre;
    private String Apellido;
    private String Cedula;
    private String UidRfid;
    private String Email;
    private String FechaNacimiento;
    private String Estado;
    private String FechaCreacion;
    private String FechaModifica;

    public PasajeroDTO() {
    }

    public PasajeroDTO(Integer idSexo, String nombre, String apellido, String cedula,
            String uidRfid,
            String email, String fechaNacimiento) {
        IdPasajero = 0;
        IdSexo = idSexo;
        Nombre = nombre;
        Apellido = apellido;
        Cedula = cedula;
        UidRfid = uidRfid;
        Email = email;
        FechaNacimiento = fechaNacimiento;
    }

    public PasajeroDTO(Integer idPasajero, Integer idSexo, String nombre, String apellido, String cedula,
            String uidRfid,
            String email, String fechaNacimiento, String estado, String fechaCreacion,
            String fechaModifica) {
        IdPasajero = idPasajero;
        IdSexo = idSexo;
        Nombre = nombre;
        Apellido = apellido;
        Cedula = cedula;
        UidRfid = uidRfid;
        Email = email;
        FechaNacimiento = fechaNacimiento;
        Estado = estado;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }

    public Integer getIdPasajero() {
        return IdPasajero;
    }

    public void setIdPasajero(Integer idPasajero) {
        IdPasajero = idPasajero;
    }

    public Integer getIdSexo() {
        return IdSexo;
    }

    public void setIdSexo(Integer idSexo) {
        IdSexo = idSexo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public String getUidRfid() {
        return UidRfid;
    }

    public void setUidRfid(String uidRfid) {
        UidRfid = uidRfid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFechaNacimiento() {
        return FechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        FechaNacimiento = fechaNacimiento;
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
                + "\n IdPasajero     : " + getIdPasajero()
                + "\n IdSexo         : " + getIdSexo()
                + "\n Nombre         : " + getNombre()
                + "\n Apellido       : " + getApellido()
                + "\n Cedula         : " + getCedula()
                + "\n UidRfid        : " + getUidRfid()
                + "\n Email          : " + getEmail()
                + "\n FechaNacimiento: " + getFechaNacimiento()
                + "\n Estado         : " + getEstado()
                + "\n FechaCreacion  : " + getFechaCreacion()
                + "\n FechaModifica  : " + getFechaModifica();
    }
}
