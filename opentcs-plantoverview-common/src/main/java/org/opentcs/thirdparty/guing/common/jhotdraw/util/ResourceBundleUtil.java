// SPDX-FileCopyrightText: The original authors of JHotDraw and all its contributors
// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.thirdparty.guing.common.jhotdraw.util;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience wrapper for accessing resources stored in a ResourceBundle.
 *
 * @author Werner Randelshofer, Hausmatt 10, CH-6405 Immensee, Switzerland
 */
public class ResourceBundleUtil
    implements
      Serializable {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ResourceBundleUtil.class);
  /**
   * The global verbose property.
   */
  private static final boolean IS_VERBOSE = true;
  /**
   * The wrapped resource bundle.
   */
  private final ResourceBundle resource;
  /**
   * The base name of the resource bundle.
   */
  private final String baseName;

  /**
   * Creates a new ResouceBundleUtil which wraps the provided resource bundle.
   *
   * @param baseName
   * @param locale
   */
  public ResourceBundleUtil(String baseName, Locale locale) {
    this.baseName = requireNonNull(baseName, "baseName");
    requireNonNull(locale, "locale");
    this.resource = ResourceBundle.getBundle(baseName, locale);
  }

  /**
   * Get a String from the ResourceBundle. <br>Convenience method to save
   * casting.
   *
   * @param key The key of the property.
   * @return The value of the property. Returns the key if the property is
   * missing.
   */
  public String getString(String key) {
    try {
      return resource.getString(key);
    }
    catch (MissingResourceException e) {
      if (IS_VERBOSE) {
        LOG.warn("baseName: {}, '{}' not found.", baseName, key, e);
      }

      return key;
    }
  }

  /**
   * Returns a formatted string using javax.text.MessageFormat.
   *
   * @param key
   * @param arguments
   * @return formatted String
   */
  public String getFormatted(String key, Object... arguments) {
    return MessageFormat.format(getString(key), arguments);
  }

  public static ResourceBundleUtil getBundle(String baseName)
      throws MissingResourceException {
    return new ResourceBundleUtil(baseName, Locale.getDefault());
  }

  @Override
  public String toString() {
    return super.toString() + "[" + baseName + ", " + resource + "]";
  }
}
