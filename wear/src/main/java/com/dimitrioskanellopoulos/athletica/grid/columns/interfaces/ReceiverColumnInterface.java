package com.dimitrioskanellopoulos.athletica.grid.columns.interfaces;

public interface ReceiverColumnInterface {
    void registerReceivers();

    void unRegisterReceivers();

    Boolean hasRegisteredReceivers();
}
