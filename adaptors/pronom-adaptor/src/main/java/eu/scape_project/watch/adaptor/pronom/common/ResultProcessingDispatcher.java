package eu.scape_project.watch.adaptor.pronom.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The dispatcher provides a method to decide whether or not the partial result
 * of the service should be processed or not. It acts as a simple cache that
 * stores the checksums of the passed results in a file and checks if the
 * checksum is already known or not.
 * 
 * @author Petar Petrov <me@petarpetrov.org>
 * 
 */
public class ResultProcessingDispatcher {

  private static final Logger LOG = LoggerFactory.getLogger(ResultProcessingDispatcher.class);

  /**
   * A list of the currently known hashes. Synchronized list.
   */
  private List<String> hashes;

  /**
   * The path to the file cache.
   */
  private String cacheFilePath;

  /**
   * Creates a dispatcher with a cache file pointing to the given path. The
   * constructor initializes the hash list and reads the old values.
   * 
   * @param cacheFilePath
   *          the cache file.
   */
  public ResultProcessingDispatcher(final String cacheFilePath) {
    this.cacheFilePath = cacheFilePath;
    this.hashes = Collections.synchronizedList(new ArrayList<String>());
    this.readHashes();
  }

  /**
   * Checks if the result is known based on the md5 hash and returns true if the
   * result shall be processed further and false if the result is already known
   * and shall be discarded.
   * 
   * @param result
   *          the partial result of to check.
   * @return true if it wasn't known, and false otherwise.
   */
  public boolean process(String result) {
    LOG.debug("Checking if partial result is known");

    final String checksum = this.getMD5Checksum(result);
    return !this.doesHashExist(checksum);
  }

  /**
   * Calculates the md5 checksum of the passed string and returns a string
   * representation.
   * 
   * @param text
   *          the text to digest.
   * @return the md5 checksum.
   */
  private String getMD5Checksum(final String text) {
    final MessageDigest digester = this.getDigester("MD5");
    final byte[] hash = digester.digest(text.getBytes());
    final BigInteger bigInt = new BigInteger(1, hash);

    return bigInt.toString(16);

  }

  /**
   * Gets a digester with the desired algorithm or null if the algorithm is
   * unknown.
   * 
   * @param alg
   *          the digest algorithm, e.g. MD5, SHA, etc.
   * @return the digester.
   */
  private MessageDigest getDigester(String alg) {
    try {
      return MessageDigest.getInstance(alg);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Checks if the hash is known and returns true if it is, or false otherwise.
   * In the case that the hash is unknown, it is added to the cache list and
   * appended to the cache file.
   * 
   * @param hash
   *          the hash to check.
   * @return if the hash was known.
   */
  private boolean doesHashExist(String hash) {
    boolean result = false;

    if (this.hashes.contains(hash)) {
      result = true;

    } else {
      this.appendHash(hash);
      this.hashes.add(hash);
    }

    return result;
  }

  /**
   * Appends the hash to the cache file.
   * 
   * @param hash
   *          the hash to append.
   */
  private void appendHash(final String hash) {
    FileWriter fw = null;
    try {
      fw = new FileWriter(this.cacheFilePath, true);
      fw.write(hash + "\n");

    } catch (final IOException e) {
      LOG.error("An error occurred, while appending the hash '{}' to the cache file: {}", hash, e.getMessage());
    } finally {

      if (fw != null) {
        try {
          fw.close();
        } catch (final IOException e) {
          LOG.error("An error occurred while closing the stream: {}", e.getMessage());
        }
      }
    }
  }

  /**
   * Reads the cache file line by line and appends all hashes to the hash list.
   * If the file is not existing than one is created and the method aborts the
   * reading.
   */
  private synchronized void readHashes() {
    FileReader reader = null;
    BufferedReader in = null;

    this.hashes.clear();
    final File file = new File(this.cacheFilePath);

    if (!file.exists()) {
      try {
        LOG.info("Cache file for PRONOM results does not exist, creating one at: {}", this.cacheFilePath);

        final File parent = file.getParentFile();
        if (!parent.exists()) {
          LOG.debug("Creating parent directories: {}", parent.getAbsolutePath());
          parent.mkdirs();
        }

        file.createNewFile();
        return;
        // no need to proceed.
      } catch (final IOException e) {
        LOG.error("Could not create a cache file: {}", e.getMessage());
      }
    }

    LOG.info("Reading file cache located at: {}", this.cacheFilePath);
    try {
      reader = new FileReader(file);
      in = new BufferedReader(reader);
      String hash;

      while ((hash = in.readLine()) != null) {
        this.hashes.add(hash);
      }

    } catch (final IOException e) {
      LOG.error("An error occurred, while reading the cache file: {}", e.getMessage());

    } finally {
      try {
        if (in != null) {
          in.close();
        }

        if (reader != null) {
          reader.close();
        }
      } catch (final IOException e) {
        LOG.error("An error occurred, while closing the streams: {}", e.getMessage());
      }
    }
  }

}
