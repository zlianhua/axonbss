package com.ai.bss.mutitanent;
/**
 * Thread local storage of the tenant name. This is the only place, where the tenant name is available across all calls
 * and beans.
 * 
 */
public class TenantContext  {
	private static final InheritableThreadLocal<String> currentTenantName = new InheritableThreadLocal<>();

    public static String getCurrentTenant() {
        return currentTenantName.get();
    }

    public static void setCurrentTenant(final String tenantName) {
        currentTenantName.set(tenantName);
    }

    public static void cleanupTenant() {
        currentTenantName.remove();
    }

}
