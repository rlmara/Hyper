package org.ltimindtree;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;

public class ContractHandle {

    private Contract contract;

    public Contract getContract() {
        return contract;
    }

    public ContractHandle(final Gateway gateway, final String channelName, final String chaincodeName) {
        // Get a network instance representing the channel where the smart contract is
        // deployed.
        var network = gateway.getNetwork(channelName);

        // Get the smart contract from the network.
        this.contract = network.getContract(chaincodeName);
    }
}
