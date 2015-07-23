package org.nuxeo.ecm.multi.tenant.blobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.blob.BlobDispatcher;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobManagerComponent;
import org.nuxeo.ecm.core.blob.BlobProviderDescriptor;
import org.nuxeo.ecm.core.blob.binary.AESBinaryManager;
import org.nuxeo.ecm.core.blob.binary.BinaryManager;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.multi.tenant.MultiTenantService;
import org.nuxeo.runtime.api.Framework;

public class MultitenantBlobDispatcher implements BlobDispatcher {

    @Override
    public String getBlobProvider(String repositoryName) {
        return repositoryName;
    }

    @Override
    public BlobDispatch getBlobProvider(Document doc, Blob blob) {

        initTenantsIfNeeded();

        try {
            String path = doc.getPath();

            if (doc.isVersion()) {
                // XXX NPE
                path = doc.getSession().getDocumentByUUID(doc.getVersionSeriesId()).getPath();
            }

            for (String tenantId :tenantIds) {
                if (path.startsWith("/"+tenantId+"/")) {
                    return new BlobDispatch(getProviderNameForTenant(tenantId), true);
                }
            }

            return new BlobDispatch(doc.getRepositoryName(), false);

        } catch (DocumentException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public Collection<String> getBlobProviderIds() {
       return providerIds;
    }

    protected List<String> providerIds = new ArrayList<String>();
    protected List<String> tenantIds = new ArrayList<String>();


    protected String getProviderNameForTenant(String tenantId) {
        return "BlobProvider_" + tenantId;
    }

    @Override
    public void initialize(Map<String, String> properties) {
        initTenants();
    }

    protected void initTenantsIfNeeded() {
        if (tenantIds.size()==0) {
            initTenants();
        }
    }

    protected void initTenants() {

        providerIds = new ArrayList<String>();
        tenantIds = new ArrayList<String>();

        MultiTenantService mts = Framework.getService(MultiTenantService.class);
        List<DocumentModel> tenants = mts.getTenants();

        BlobManagerComponent bmc = (BlobManagerComponent) Framework.getService(BlobManager.class);

        for (DocumentModel tenant : tenants) {

            String tenantId = (String) tenant.getProperty("tenant", "id");
            tenantIds.add(tenantId);

            String pwd = "password="+tenantId;

            BlobProviderDescriptor bpd = new BlobProviderDescriptor();
            bpd.name = getProviderNameForTenant(tenantId);
            bpd.klass= AESBinaryManager.class;
            bpd.properties=new HashMap<String, String>();
            bpd.properties.put("key", pwd);
            bpd.properties.put(BinaryManager.PROP_PATH, tenantId);

            bmc.registerBlobProvider(bpd);
            providerIds.add(bpd.name);
        }

    }

    @Override
    public void notifyChanges(Document doc, Set<String> xpaths) {
        // NOP
    }

}
