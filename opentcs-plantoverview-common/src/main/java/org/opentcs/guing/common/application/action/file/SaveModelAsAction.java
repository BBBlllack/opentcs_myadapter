// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.application.action.file;

import static org.opentcs.guing.common.util.I18nPlantOverview.MENU_PATH;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.opentcs.guing.common.application.GuiManager;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 */
public class SaveModelAsAction
    extends
      AbstractAction {

  /**
   * This action's ID.
   */
  public static final String ID = "file.saveModelAs";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);
  /**
   * The manager this instance is working with.
   */
  private final GuiManager guiManager;

  /**
   * Creates a new instance.
   *
   * @param manager The gui manager
   */
  @SuppressWarnings("this-escape")
  public SaveModelAsAction(final GuiManager manager) {
    this.guiManager = manager;

    putValue(NAME, BUNDLE.getString("saveModelAsAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("saveModelAsAction.shortDescription"));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift ctrl S"));
    putValue(MNEMONIC_KEY, Integer.valueOf('A'));

    ImageIcon icon = ImageDirectory.getImageIcon("/menu/document-save-as.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    guiManager.saveModelAs();
  }
}
