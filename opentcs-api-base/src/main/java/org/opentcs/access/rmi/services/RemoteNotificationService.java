// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.access.rmi.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Predicate;
import org.opentcs.access.rmi.ClientID;
import org.opentcs.components.kernel.services.NotificationService;
import org.opentcs.data.notification.UserNotification;

/**
 * Declares the methods provided by the {@link NotificationService} via RMI.
 *
 * <p>
 * The majority of the methods declared here have signatures analogous to their counterparts in
 * {@link NotificationService}, with an additional {@link ClientID} parameter which serves the
 * purpose of identifying the calling client and determining its permissions.
 * </p>
 * <p>
 * To avoid redundancy, the semantics of methods that only pass through their arguments are not
 * explicitly documented here again. See the corresponding API documentation in
 * {@link NotificationService} for these, instead.
 * </p>
 */
public interface RemoteNotificationService
    extends
      Remote {

  // CHECKSTYLE:OFF
  List<UserNotification> fetchUserNotifications(
      ClientID clientId,
      Predicate<UserNotification> predicate
  )
      throws RemoteException;

  void publishUserNotification(ClientID clientId, UserNotification notification)
      throws RemoteException;
  // CHECKSTYLE:ON
}
