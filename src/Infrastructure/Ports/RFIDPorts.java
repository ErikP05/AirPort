package Infrastructure.Ports;

public interface RFIDPorts {
void connect();
String readUID();
void disconnect();
}