package util;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Component
public class BlockchainUtil {

    private final Web3j web3j;
    private final Credentials credentials;

    public BlockchainUtil() {
        this.web3j = Web3j.build(new HttpService("http://localhost:8545"));
        // Use the first account from Ganache
        this.credentials = Credentials.create("0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d");
    }

    public CompletableFuture<TransactionReceipt> sendTransaction(String to, BigDecimal value) throws Exception {
        return Transfer.sendFunds(web3j, credentials, to, value, Convert.Unit.ETHER).sendAsync();
    }

    public BigInteger getBalance(String address) throws Exception {
        return web3j.ethGetBalance(address, web3j.ethBlockNumber().send().getBlockNumber()).send().getBalance();
    }

    public String createAccount() throws Exception {
        // In Ganache, accounts are pre-created, but for simulation, we can use existing ones or generate new
        // For simplicity, return a dummy address or use web3j to create
        // Web3j doesn't have direct account creation, but we can use the pre-funded accounts
        // For now, return one of the Ganache accounts
        return "0x90F8bf6A479f320ead074411a4B0e7944Ea8c9C1"; // Second account
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public Credentials getCredentials() {
        return credentials;
    }
}