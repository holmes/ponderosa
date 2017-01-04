package com.holmes.ponderosa.data.api.auth;

import android.app.Application;
import android.security.keystore.KeyGenParameterSpec;
import android.support.annotation.NonNull;
import com.holmes.ponderosa.util.Preconditions;
import com.squareup.moshi.Moshi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import timber.log.Timber;

import static android.security.keystore.KeyProperties.BLOCK_MODE_CBC;
import static android.security.keystore.KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;
import static android.security.keystore.KeyProperties.PURPOSE_DECRYPT;
import static android.security.keystore.KeyProperties.PURPOSE_ENCRYPT;

/** Really hope this is done correctly. */
public class CredentialManager {
  private static final String ALIAS = "ponderosa";
  private final Application application;
  private final Moshi moshi;

  private String encryptedDataFilePath;
  private KeyStore keyStore;

  static class Credentials {
    final String username;
    final String password;

    private Credentials(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }

  public CredentialManager(Application application, Moshi moshi) {
    this.application = application;
    this.moshi = moshi;
  }

  public void initialize() {
    try {
      String filesDirectory = application.getFilesDir().getAbsolutePath();
      encryptedDataFilePath = filesDirectory + File.separator + "credentials.json";
      Timber.d("Credentials located at: %s", encryptedDataFilePath);

      keyStore = KeyStore.getInstance("AndroidKeyStore");
      keyStore.load(null);
      Timber.d("Loaded AndroidKeyStore");

      if (!keyStore.containsAlias(ALIAS)) {
        KeyGenParameterSpec spec =
            new KeyGenParameterSpec.Builder(ALIAS, PURPOSE_ENCRYPT | PURPOSE_DECRYPT).setKeySize(1024)
                .setBlockModes(BLOCK_MODE_CBC)
                .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                .build();

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        generator.initialize(spec);
        generator.generateKeyPair();
        Timber.d("Generated a new KeyPair");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void save(@NonNull String username, @NonNull String password) {
    Preconditions.checkNotNull(username, "username == null");
    Preconditions.checkNotNull(password, "password == null");
    String plainText = moshi.adapter(Credentials.class).toJson(new Credentials(username, password));

    try {
      PublicKey publicKey = getEntry().getCertificate().getPublicKey();
      Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
      inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
      CipherOutputStream cipherOutputStream =
          new CipherOutputStream(new FileOutputStream(encryptedDataFilePath), inCipher);
      cipherOutputStream.write(plainText.getBytes("UTF-8"));
      cipherOutputStream.close();
      Timber.d("Saved credentials for %s", username);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Credentials retrieve() {
    if (!new File(encryptedDataFilePath).exists()) {
      return new Credentials("guest", "guest");
    }

    try {
      PrivateKey privateKey = getEntry().getPrivateKey();
      Cipher outCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround");
      outCipher.init(Cipher.DECRYPT_MODE, privateKey);
      CipherInputStream cipherInputStream =
          new CipherInputStream(new FileInputStream(encryptedDataFilePath), outCipher);
      byte[] roundTrippedBytes = new byte[1000];

      int index = 0;
      int nextByte;
      while ((nextByte = cipherInputStream.read()) != -1) {
        roundTrippedBytes[index] = (byte) nextByte;
        index++;
      }

      String roundTrippedString = new String(roundTrippedBytes, 0, index, "UTF-8");
      return moshi.adapter(Credentials.class).fromJson(roundTrippedString);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private KeyStore.PrivateKeyEntry getEntry()
      throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
    return (KeyStore.PrivateKeyEntry) keyStore.getEntry(ALIAS, null);
  }
}
