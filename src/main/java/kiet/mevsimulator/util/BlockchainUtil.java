package kiet.mevsimulator.util;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;

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
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
    }

    public String createAccount() throws Exception {
        return Credentials.create(Keys.createEcKeyPair()).getAddress();
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public Credentials getCredentials() {
        return credentials;
    }
}