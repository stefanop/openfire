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
package org.jivesoftware.messenger.disco;

/**
 * Represent a DiscoItem provided by the server. Therefore, the DiscoServerItems are responsible
 * for providing the DiscoInfoProvider and DiscoItemsProvider that will provide the information and
 * items related to this item.<p>
 * <p/>
 * When the server starts up, IQDiscoItemsHandler will request to all the services that implement
 * the ServerItemsProvider interface for their DiscoServerItems. Each DiscoServerItem will provide
 * its DiscoInfoProvider which will automatically be included in IQDiscoInfoHandler as the provider
 * for this item's JID. Moreover, each DiscoServerItem will also provide its DiscoItemsProvider
 * which will automatically be included in IQDiscoItemsHandler. Special attention must be paid to
 * the JID since all the items with the same host will share the same DiscoInfoProvider or
 * DiscoItemsProvider.
 *
 * @author Gaston Dombiak
 */
public interface DiscoServerItem extends DiscoItem {

    /**
     * Returns the DiscoInfoProvider responsible for providing the information related to this item.
     * The DiscoInfoProvider will be automatically included in IQDiscoInfoHandler as the provider
     * for this item's JID.
     *
     * @return the DiscoInfoProvider responsible for providing the information related to this item.
     */
    public abstract DiscoInfoProvider getDiscoInfoProvider();

    /**
     * Returns the DiscoItemsProvider responsible for providing the items related to this item.
     * The DiscoItemsProvider will be automatically included in IQDiscoItemsHandler as the provider
     * for this item's JID.
     *
     * @return the DiscoItemsProvider responsible for providing the items related to this item.
     */
    public abstract DiscoItemsProvider getDiscoItemsProvider();
}
