package BusinessLogic;

import java.util.List;

import BusinessLogic.Entities.Pasajeroimp;
import BusinessLogic.Interfaces.IPasajeroService;
import BusinessLogic.Entities.CheckInServiceimp;
import BusinessLogic.Interfaces.ICheckInService;
import DataAccess.DAOs.AsientoDAO;
import DataAccess.DAOs.RFIDDAO;
import DataAccess.DAOs.ReservaDAO;
import DataAccess.DAOs.VueloDAO;
import DataAccess.DTOs.ReservaDTO;
import DataAccess.Interfaces.IDAO;
import Infrastructure.AppException;

public class FactoryBL<T> {
    private final IDAO<T> oDAO;

    public FactoryBL(Class<? extends IDAO<T>> classDAO) {
        if (classDAO != null) {
            try {
                this.oDAO = classDAO.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                AppException er = new AppException("Error al instanciar classDAO<T>", e, getClass(), "FactoryBL(...)");
                throw new RuntimeException(er);
            }
        } else {
            this.oDAO = null;
        }
    }

    public ICheckInService createCheckInService() throws AppException {
        ReservaDAO reservaDAO = new ReservaDAO();
        VueloDAO vueloDAO = new VueloDAO();
        RFIDDAO rfidDAO = new RFIDDAO();
        AsientoDAO asientoDAO = new AsientoDAO();
        return new CheckInServiceimp(rfidDAO, reservaDAO, vueloDAO, asientoDAO);
    }

    public IPasajeroService createPasajeroService() throws AppException {
        return new Pasajeroimp();
    }

    public List<T> getAll() throws AppException {
        if (oDAO == null) {
            throw new AppException("DAO no inicializado");
        }
        return oDAO.readAll();
    }

    public T getBy(Integer id) throws AppException {
        if (oDAO == null) {
            throw new AppException("DAO no inicializado");
        }
        return oDAO.readBy(id);
    }

    public boolean add(T oT) throws AppException {
        if (oDAO == null) {
            throw new AppException("DAO no inicializado");
        }
        return oDAO.create(oT);
    }

    public boolean upd(T oT) throws AppException {
        if (oDAO == null) {
            throw new AppException("DAO no inicializado");
        }
        return oDAO.update(oT);
    }

    public boolean del(Integer id) throws AppException {
        if (oDAO == null) {
            throw new AppException("DAO no inicializado");
        }
        return oDAO.delete(id);
    }

    public Integer getMaxReg(String cellName) throws AppException {
        return oDAO.getMaxReg(cellName);
    }

    public Integer getMinReg(String cellName) throws AppException {
        return oDAO.getMinReg(cellName);
    }

    public Integer getCountReg() throws Exception {
        return oDAO.getCountReg();
    }

    public boolean create(T entity) throws AppException {
        if (oDAO == null) {
            throw new AppException("DAO no inicializado");
        }
        return oDAO.create(entity);
    }
}
