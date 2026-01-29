package BusinessLogic.Validacion;

import BusinessLogic.Interfaces.IValidacion;

public class ValidacionService implements IValidacion {
	@Override
	public boolean validarEmail(String email) {
		// git ade validación de email
		return email != null && email.contains("@") && email.contains(".");
	}

	@Override
	public boolean validarCedula(String cedula) {
		// Lógica simple de validación de cédula (solo ejemplo)
		return cedula != null && cedula.length() >= 6;
	}
}
