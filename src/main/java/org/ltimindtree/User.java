package org.ltimindtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;

public class User {

    private Identity identity;
    private Signer signer;

    private String subjectDN;
    private String issuerDN;

    public Identity getIdentity() {
        return identity;
    }

    public Signer getSigner() {
        return signer;
    }

    public User(String mspId, Path certPath, Path keyPath) throws CertificateException, IOException, InvalidKeyException {
        this.identity = newIdentity(mspId, certPath);
        this.signer = newSigner(keyPath);
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public String getIssuerDN() {
        return issuerDN;
    }

    private Identity newIdentity(String mspId, Path certPath) throws IOException, CertificateException {
        BufferedReader certReader = Files.newBufferedReader(certPath);
        X509Certificate certificate = Identities.readX509Certificate(certReader);
        this.subjectDN = certificate.getSubjectDN().toString();
        this.issuerDN =certificate.getIssuerDN().toString();
        return new X509Identity(mspId, certificate);
    }

    private Signer newSigner(Path keyPath) throws IOException, InvalidKeyException {
        BufferedReader keyReader = Files.newBufferedReader(keyPath);
        PrivateKey privateKey = Identities.readPrivateKey(keyReader);

        return Signers.newPrivateKeySigner(privateKey);
    }

}
