/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-2003 CoolServlets, Inc. All rights reserved.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */
package org.jivesoftware.net.spi;

import org.jivesoftware.net.ConnectionManager;
import org.jivesoftware.net.Connection;
import org.jivesoftware.net.ConnectionMonitor;

import java.util.Iterator;

import org.jivesoftware.messenger.container.BasicModule;
import org.jivesoftware.util.ConcurrentHashSet;
import org.jivesoftware.util.BasicResultFilter;

public class ConnectionManagerImpl extends BasicModule implements ConnectionManager {

    private ConcurrentHashSet connections = new ConcurrentHashSet();
    private ConnectionMonitor connectedMonitor = new TransientConnectionMonitor();
    private ConnectionMonitor configMonitor = new TransientConnectionMonitor();
    private ConnectionMonitor disconnectedMonitor = new TransientConnectionMonitor();

    public ConnectionManagerImpl() {
        super("Connection Manager");
    }

    public int getConnectionCount() {
        return connections.size();
    }

    public Iterator getConnections() {
        return connections.iterator();
    }

    public Iterator getConnections(BasicResultFilter filter) {
        return filter.filter(connections.iterator());
    }

    public void addConnection(Connection conn) {
        connections.add(conn);
        conn.setConnectionManager(this);
        connectedMonitor.addSample(conn);
    }

    public void removeConnection(Connection conn) {
        connections.remove(conn);
        disconnectedMonitor.addSample(conn);
    }

    public ConnectionMonitor getConnectedMonitor() {
        return connectedMonitor;
    }

    public ConnectionMonitor getConfigMonitor() {
        return configMonitor;
    }

    public ConnectionMonitor getDisconnectedMonitor() {
        return disconnectedMonitor;
    }
}