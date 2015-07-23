/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     dmetzler
 */
package org.nuxeo.ecm.multi.tenant;

import java.io.Serializable;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.blob.binary.BinaryBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

/**
 * @since 5.8
 */
@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@RepositoryConfig(cleanup = Granularity.METHOD, init = MultiTenantRepositoryInit.class)
@LocalDeploy({ "org.nuxeo.ecm.platform.test:test-usermanagerimpl/userservice-config.xml",
        "org.nuxeo.ecm.multi.tenant.blob:multi-tenant-test-contrib.xml",
        "org.nuxeo.ecm.multi.tenant.blob:multi-tenant-enabled-default-test-contrib.xml" })
@Deploy({ "org.nuxeo.ecm.multi.tenant", "org.nuxeo.ecm.multi.tenant.blob" })
public class TestTenantBlobDispatcher {

    @Inject
    protected MultiTenantService mts;

    @Inject
    protected CoreSession session;

    @Test
    public void shouldHaveBlobProviderPerTenant() throws Exception {

        DocumentModel f0 = session.createDocumentModel("/domain0", "file0", "File");
        Blob blob0 = new StringBlob("Yo");
        f0.setPropertyValue("file:content", (Serializable) blob0);
        f0 = session.createDocument(f0);

        DocumentModel f1 = session.createDocumentModel("/domain1", "file1", "File");
        Blob blob1 = new StringBlob("Yo");
        f1.setPropertyValue("file:content", (Serializable) blob1);
        f1 = session.createDocument(f1);

        BinaryBlob bblob0 = (BinaryBlob) f0.getPropertyValue("file:content");
        BinaryBlob bblob1 = (BinaryBlob) f1.getPropertyValue("file:content");

        Assert.assertEquals("BlobProvider_domain0", bblob0.getBinary().getBlobProviderId());
        Assert.assertEquals("BlobProvider_domain1", bblob1.getBinary().getBlobProviderId());

    }

}
