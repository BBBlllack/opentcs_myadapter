// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.properties;

import jakarta.inject.Inject;
import org.opentcs.guing.common.event.OperationModeChangeEvent;
import org.opentcs.guing.common.event.SystemModelTransitionEvent;
import org.opentcs.thirdparty.guing.common.jhotdraw.application.action.edit.UndoRedoManager;
import org.opentcs.util.event.EventHandler;

/**
 * An AttributesComponent intended to be shown permanently to display the
 * properties of the currently selected driving course components.
 */
public class SelectionPropertiesComponent
    extends
      AttributesComponent
    implements
      EventHandler {

  /**
   * Creates a new instance.
   *
   * @param undoManager Manages undo/redo actions.
   */
  @Inject
  public SelectionPropertiesComponent(UndoRedoManager undoManager) {
    super(undoManager);
  }

  @Override
  public void onEvent(Object event) {
    if (event instanceof SystemModelTransitionEvent) {
      handleSystemModelTransition((SystemModelTransitionEvent) event);
    }
    if (event instanceof OperationModeChangeEvent) {
      handleOperationModeChange((OperationModeChangeEvent) event);
    }
  }

  private void handleSystemModelTransition(SystemModelTransitionEvent evt) {
    switch (evt.getStage()) {
      case UNLOADING:
        reset();
        break;
      default:
        // Do nada.
    }
  }

  private void handleOperationModeChange(OperationModeChangeEvent evt) {
    reset();
  }
}
