package org.ltimindtree;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identities;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class GatewayBuilder {

    private Gateway gateway;

    public ManagedChannel getChannel() {
        return channel;
    }

    private ManagedChannel channel;

    public Gateway getGateway() {
        return gateway;
    }

    public GatewayBuilder(String peerEndpoint, User user, Path tlsCertPath, String overrideAuth) throws CertificateException, IOException {
        this.gateway = connect(peerEndpoint, user, tlsCertPath, overrideAuth);
    }

    private Gateway connect(String peerEndpoint, User user, Path tlsCertPath, String overrideAuth) throws CertificateException, IOException {
        // The gRPC client connection should be shared by all Gateway connections to
        // this endpoint.
        this.channel = newGrpcConnection(peerEndpoint, tlsCertPath, overrideAuth);

        Gateway.Builder builder = org.hyperledger.fabric.client.Gateway.newInstance().identity(user.getIdentity()).signer(user.getSigner()).connection(channel)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        Gateway gateway = builder.connect();
        return gateway;
    }

    private ManagedChannel newGrpcConnection(String peerEndpoint, Path tlsCertPath, String overrideAuth) throws IOException, CertificateException {
        BufferedReader tlsCertReader = Files.newBufferedReader(tlsCertPath);
        X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

        return NettyChannelBuilder.forTarget(peerEndpoint)
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build()).overrideAuthority(overrideAuth)
                .build();
    }

}
