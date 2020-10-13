package org.gioneco.analytics.android.sdk.helper;

/*public*/ enum DataTable {
    APP_STARTED("app_started"),
    APP_PAUSED_TIME("app_paused_time"),
    APP_END_STATE("app_end_state");

    DataTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;
}
