package net.wimpi.modbustcp.interfaces;


public interface IServerManager<E extends IIOCoreOptions> {

    void listen();

    void listen(E options);

    boolean isLive();

    IClientPool<String, IClient> getClientPool();
}
