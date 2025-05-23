// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.components.drawing;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javax.swing.JToggleButton;
import org.jhotdraw.gui.JPopupButton;
import org.opentcs.guing.base.model.elements.VehicleModel;
import org.opentcs.guing.common.application.StatusPanel;
import org.opentcs.guing.common.components.drawing.DrawingOptions;
import org.opentcs.guing.common.components.drawing.DrawingViewPlacardPanel;
import org.opentcs.guing.common.components.drawing.DrawingViewScrollPane;
import org.opentcs.guing.common.components.drawing.OpenTCSDrawingEditor;
import org.opentcs.guing.common.components.drawing.OpenTCSDrawingView;
import org.opentcs.guing.common.model.SystemModel;
import org.opentcs.guing.common.persistence.ModelManager;

/**
 * A factory for drawing views.
 */
public class DrawingViewFactory {

  /**
   * A provider for drawing views.
   */
  private final Provider<OpenTCSDrawingView> drawingViewProvider;
  /**
   * The drawing editor.
   */
  private final OpenTCSDrawingEditor drawingEditor;
  /**
   * The status panel to display the current mouse position in.
   */
  private final StatusPanel statusPanel;
  /**
   * The manager keeping/providing the currently loaded model.
   */
  private final ModelManager modelManager;
  /**
   * The drawing options.
   */
  private final DrawingOptions drawingOptions;

  @Inject
  public DrawingViewFactory(
      Provider<OpenTCSDrawingView> drawingViewProvider,
      OpenTCSDrawingEditor drawingEditor,
      StatusPanel statusPanel,
      ModelManager modelManager,
      DrawingOptions drawingOptions
  ) {
    this.drawingViewProvider = requireNonNull(drawingViewProvider, "drawingViewProvider");
    this.drawingEditor = requireNonNull(drawingEditor, "drawingEditor");
    this.statusPanel = requireNonNull(statusPanel, "statusPanel");
    this.modelManager = requireNonNull(modelManager, "modelManager");
    this.drawingOptions = requireNonNull(drawingOptions, "drawingOptions");
  }

  /**
   * Creates and returns a new drawing view along with its placard panel, both
   * wrapped in a scroll pane.
   *
   * @param systemModel The system model.
   * @param selectionToolButton The selection tool button in the tool bar.
   * @param dragToolButton The drag tool button in the tool bar.
   * @param linkCreationToolButton The link creation tool button in the tool bar.
   * @param pathCreationToolButton The path creation tool button in the tool bar.
   * @return A new drawing view, wrapped in a scroll pane.
   */
  public DrawingViewScrollPane createDrawingView(
      SystemModel systemModel,
      JToggleButton selectionToolButton,
      JToggleButton dragToolButton,
      JToggleButton linkCreationToolButton,
      JPopupButton pathCreationToolButton
  ) {
    requireNonNull(systemModel, "systemModel");
    requireNonNull(selectionToolButton, "selectionToolButton");
    requireNonNull(dragToolButton, "dragToolButton");

    OpenTCSDrawingView drawingView = drawingViewProvider.get();
    drawingEditor.add(drawingView);
    drawingEditor.setActiveView(drawingView);
    for (VehicleModel vehicle : systemModel.getVehicleModels()) {
      drawingView.displayDriveOrders(vehicle, vehicle.getDisplayDriveOrders());
    }
    drawingView.setBlocks(systemModel.getMainFolder(SystemModel.FolderKey.BLOCKS));

    DrawingViewPlacardPanel placardPanel = new DrawingViewPlacardPanel(drawingView, drawingOptions);

    DrawingViewScrollPane scrollPane = new DrawingViewScrollPane(drawingView, placardPanel);
    scrollPane.originChanged(systemModel.getDrawingMethod().getOrigin());

    // --- Listens to draggings in the drawing ---
    ViewDragScrollListener dragScrollListener
        = new ViewDragScrollListener(
            scrollPane,
            placardPanel.getZoomComboBox(),
            selectionToolButton,
            dragToolButton,
            linkCreationToolButton,
            pathCreationToolButton,
            statusPanel,
            modelManager
        );
    drawingView.addMouseListener(dragScrollListener);
    drawingView.addMouseMotionListener(dragScrollListener);
    drawingView.getComponent().addMouseWheelListener(dragScrollListener);

    return scrollPane;
  }
}
